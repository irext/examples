/**
 *
 * Copyright (c) 2020-2025 IRext Opensource Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

#include <Arduino.h>
#include <Base64.h>
#include <IRremote.hpp>

#include "control_command.h"
#include "utils.h"
#include "serial_log.h"
#include "ir_decode.h"

#include "remote.h"

#define REMOTE_BIN_MAX    (1024)

#define ABIN_COMMAND_SEG  (5)
#define ACTRL_COMMAND_SEG (3)

#define SEG_ABIN_HEADER   (0)
#define SEG_ABIN_CATE     (1)
#define SEG_ABIN_SUBCATE  (2)
#define SEG_ABIN_LENGTH   (3)
#define SEG_ABIN_BIN      (4)

#define SEG_ACTRL_HEADER  (0)
#define SEG_ACTRL_LENGTH  (1)
#define SEG_ACTRL_COMMAND (2)

#define IR_SEND_PIN       (3)


// external variable declarations
extern char *eError;


// private variable definitions
static uint8_t categoryId = -1;
static uint8_t subCategoryId = -1;
static unsigned char *remoteBin = nullptr;
static int remoteBinLen = 0;
static uint16_t remoteUserData[USER_DATA_SIZE] = { 0 };
static uint16_t userDataLen = 0;

// public function definitions
void remoteInit() {
    IrSender.begin(IR_SEND_PIN);
}

int remoteOpen(const char *binStr) {
    char *aBinCommand[ABIN_COMMAND_SEG];
    char *remoteBinStr = nullptr;
    int aBinCommandSeg = 0;
    int remoteBinBase64Len = 0;
    int retVal = 0;

    aBinCommandSeg = splitString(binStr, aBinCommand, ABIN_COMMAND_SEG, ",");
    if (ABIN_COMMAND_SEG != aBinCommandSeg) {
        serialPrint(LOG_ERROR, "Invalid aBin command: %s", binStr);
        retVal = -1;
        goto _exit;
    }
    categoryId = strtol(aBinCommand[SEG_ABIN_CATE], nullptr, 10);
    subCategoryId = strtol(aBinCommand[SEG_ABIN_SUBCATE], nullptr, 10);
    remoteBinBase64Len = strtol(aBinCommand[SEG_ABIN_LENGTH], nullptr, 10);
    remoteBinStr = aBinCommand[SEG_ABIN_BIN];
    if (remoteBinBase64Len != strlen(remoteBinStr)) {
        serialPrint(LOG_ERROR, "Remote bin length not correct, expected : %d, decoded : %d",
            remoteBinBase64Len, remoteBinLen);
        retVal = -1;
        goto _exit;
    }

    remoteBinLen = base64_dec_len(remoteBinStr, remoteBinBase64Len);

    // free the previously used buffer
    if (nullptr != remoteBin) {
        free(remoteBin);
        remoteBin = nullptr;
    }
    remoteBin = static_cast<unsigned char*>(malloc(remoteBinLen));
    if (nullptr == remoteBin) {
        serialPrint(LOG_ERROR, "Not enough memory for remote bin");
        retVal = -1;
        goto _exit;
    }
    memset(remoteBin, 0, remoteBinLen);

    if (remoteBinLen != base64_decode(reinterpret_cast<char*>(remoteBin), remoteBinStr, remoteBinBase64Len)) {
        serialPrint(LOG_ERROR, "Failed to decode remote bin");
        retVal = -1;
        goto _exit;
    }

    if (getLogLevel() == LOG_VERBOSE) {
        char debugStr[129] = { 0 };
        serialPrint(LOG_VERBOSE, "Remote bin(%d): ", remoteBinLen);
        snprintf(debugStr, 128, "%02x %02x %02x %02x %02x %02x %02x %02x",
            remoteBin[0], remoteBin[1], remoteBin[2], remoteBin[3],
            remoteBin[4], remoteBin[5], remoteBin[6], remoteBin[7]);
        serialPrint(LOG_VERBOSE, debugStr);
        snprintf(debugStr, 128, "%02x %02x %02x %02x %02x %02x %02x %02x",
            remoteBin[remoteBinLen - 8], remoteBin[remoteBinLen - 7], remoteBin[remoteBinLen - 6], remoteBin[remoteBinLen - 5],
            remoteBin[remoteBinLen - 4], remoteBin[remoteBinLen - 3], remoteBin[remoteBinLen - 2], remoteBin[remoteBinLen - 1]);
        serialPrint(LOG_VERBOSE, debugStr);
    }

    if (IR_DECODE_FAILED == ir_binary_open(categoryId, subCategoryId, remoteBin, remoteBinLen)) {
        serialPrint(LOG_ERROR, "Failed to load remote bin");
        retVal = -1;
        goto _exit;
    }

    retVal = remoteBinLen;
    serialPrint(LOG_INFO, "Remote bin loaded successfully");

_exit:
    return retVal;
}

int remoteControl(const char *controlStr) {
    char *aCtrlCommand[ACTRL_COMMAND_SEG];
    char *commandStrBase64 = nullptr;
    char *commandStr = nullptr;
    int aCtrlCommandSeg = 0;
    int commandBase64Len = 0;
    int commandLen = 0;

    t_remote_ac_status acStatus;
    int keyCode = 0;

    int retVal = 0;

    aCtrlCommandSeg = splitString(controlStr, aCtrlCommand, ACTRL_COMMAND_SEG, ",");
    if (ACTRL_COMMAND_SEG != aCtrlCommandSeg) {
        serialPrint(LOG_ERROR, "Invalid aCtrl command: ");
        retVal = -1;
        goto _exit;
    }
    commandBase64Len = strtol(aCtrlCommand[SEG_ACTRL_LENGTH], nullptr, 10);
    commandStrBase64 = aCtrlCommand[SEG_ACTRL_COMMAND];
    if (commandBase64Len != strlen(commandStrBase64)) {
        serialPrint(LOG_ERROR, "Remote command length not correct, expected : %d, decoded : %d",
            commandBase64Len, commandStrBase64);
        retVal = -1;
        goto _exit;
    }

    commandLen = base64_dec_len(commandStrBase64, commandBase64Len);
    commandStr = static_cast<char*>(malloc(commandLen));
    if (nullptr == commandStr) {
        serialPrint(LOG_ERROR, "Not enough memory for remote command");
        retVal = -1;
        goto _exit;
    }
    memset(commandStr, 0, commandLen);

    if (commandLen != base64_decode(commandStr, commandStrBase64, commandBase64Len)) {
        serialPrint(LOG_ERROR, "Failed to decode remote command");
        retVal = -1;
        goto _exit;
    }

    serialPrint(LOG_DEBUG, "Received remote command: %s", commandStr);

    if (-1 != categoryId) {
        if (0 != parseControlCommand(categoryId, commandStr, &acStatus, &keyCode)) {
            serialPrint(LOG_ERROR, "Failed to parse command JSON");
            retVal = -1;
            goto _exit;
        }
    } else {
        serialPrint(LOG_ERROR, "No remote bin loaded");
        retVal = - 1;
        goto _exit;
    }

    userDataLen = ir_decode(keyCode, remoteUserData, &acStatus);
    if (userDataLen > 0) {
        serialPrint(LOG_INFO, "IR decoded successfully: %d", userDataLen);
    }
    if (getLogLevel() == LOG_VERBOSE) {
        remoteDebug(remoteUserData, userDataLen);
    }

    IrSender.sendRaw(remoteUserData, userDataLen, 38);
    serialPrint(LOG_INFO, "IR sent successfully");

    retVal = 0;
_exit:

    if (nullptr != commandStr) {
        free(commandStr);
    }

    return retVal;
}

void remoteClose() {
    serialPrint(LOG_INFO, "Closing remote");
    ir_close();
}


// private function definitions
void remoteDebug(const uint16_t* userData, const uint16_t userDataLen) {
    if (userData == nullptr || userDataLen == 0) {
        serialPrint(LOG_VERBOSE, "userData is empty or null");
        return;
    }

    char debugStr[256] = { 0 };
    int offset = 0;

    for (uint16_t i = 0; i < userDataLen; i++) {
        if (i % 16 == 0) {
            offset = snprintf(debugStr, sizeof(debugStr), "userData[%d-%d]: ",
                             i, (i + 15 < userDataLen) ? i + 15 : userDataLen - 1);
        }
        offset += snprintf(debugStr + offset, sizeof(debugStr) - offset, "%d ", userData[i]);

        if ((i + 1) % 16 == 0 || i == userDataLen - 1) {
            serialPrint(LOG_VERBOSE, "%s", debugStr);
            offset = 0;
        }
    }
}

int irControlSend() {

}