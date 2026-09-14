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
#include <WiFiS3.h>
#include <ArduinoGraphics.h>
#include <Arduino_LED_Matrix.h>

#include "configure.h"
#include "remote.h"
#include "serial_log.h"


#define WIFI_SERVER_PORT (8000)

#define ALIVE_DEBUG_INTERVAL (20 * 1000)

// global variable definitions
constexpr char ssid[] = SECRET_SSID;
constexpr char pass[] = SECRET_PASS;

// commands and events
auto *aHello = "a_hello";
auto *eHello = "e_hello";
auto *aBin = "a_bin";
auto *eBin = "e_bin";
auto *aControl = "a_control";
auto *eControl = "e_control";
auto *aError = "a_error";
auto *eError = "e_error";
auto *eControlSuccess = "e_success";
auto *eControlFailed = "e_failed";

int status = WL_IDLE_STATUS;
unsigned long lastStatusCheck = 0;
boolean wifiStatusPrinted = false;
ArduinoLEDMatrix matrix;

WiFiServer server(WIFI_SERVER_PORT);
WiFiClient client;
boolean clientConnected = false;

void drawIp(const char *ipAddr) {
    matrix.beginDraw();

    matrix.stroke(0xFFFFFFFF);
    matrix.textScrollSpeed(100);

    matrix.textFont(Font_5x7);
    matrix.beginText(0, 1, 0xFFFFFF);
    matrix.println(ipAddr);
    matrix.endText(SCROLL_LEFT);
    matrix.endDraw();
}

void printWiFiStatus() {
    unsigned long currentMillis = millis();

    if (currentMillis - lastStatusCheck >= ALIVE_DEBUG_INTERVAL) {
        const IPAddress ip = WiFi.localIP();
        if (0 == strcmp(ip.toString().c_str(), "0.0.0.0")) {
            return;
        }
        serialPrint(LOG_INFO, "Wi-Fi SSID: %s", WiFi.SSID());

        serialPrint(LOG_INFO, "Wi-Fi IP address: %s", ip.toString().c_str());

        const long rssi = WiFi.RSSI();
        serialPrint(LOG_INFO, "Wi-Fi signal strength (RSSI): %ld dBm", rssi);

        lastStatusCheck = currentMillis;

        if (0 == wifiStatusPrinted) {
            drawIp(ip.toString().c_str());
            wifiStatusPrinted = true;
        }
    }
}

static void sendToClient(WiFiClient *client, const char* content) {
    client->println(content);
    client->flush();
}

void setup() {
    Serial.begin(115200);

    while (!Serial) {
        delay(100);
    }

    remoteInit();

    matrix.begin();
    matrix.beginDraw();

    matrix.stroke(0xFFFFFFFF);
    matrix.textScrollSpeed(100);

    constexpr char text[] = "IRext Example";
    matrix.textFont(Font_4x6);
    matrix.beginText(0, 1, 0xFFFFFF);
    matrix.println(text);
    matrix.endText(SCROLL_LEFT);
    matrix.endDraw();

    serialPrint(LOG_INFO, "IRext Arduino example started in station mode");
    serialPrint(LOG_INFO, "Attempting to connect to SSID: %s", ssid);

    status = WiFi.begin(ssid, pass);

    if (status == WL_CONNECTED) {
        serialPrint(LOG_INFO, "Connected to Wi-Fi");
        server.begin();
    }
    else {
        serialPrint(LOG_ERROR, "Failed to connect Wi-Fi, status: %d", status);
    }
}

void onConnected(WiFiClient *client) {
    client->flush();
    serialPrint(LOG_DEBUG, "Client connected");
    sendToClient(client, eHello);
}

void onDisconnected(WiFiClient *client) {
    remoteClose();
    client->flush();
    client->stop();
    delay(500);
    serialPrint(LOG_DEBUG, "Client disconnected");
}

void onError(WiFiClient *client) {
    client->flush();
    client->stop();
}

void onCommand(WiFiClient *client, const String *command) {
    if (command->startsWith(aHello)) {
        serialPrint(LOG_DEBUG, "Received hello command");
        sendToClient(client, eBin);
    } else if (command->startsWith(aBin)) {
        serialPrint(LOG_DEBUG, "Received bin command");
#if !defined TEST_BIN_RECEIVE
        if (remoteOpen(command->c_str()) > 0) {
            sendToClient(client, eControl);
        } else {
            serialPrint(LOG_ERROR, "Failed to parse bin command");
            sendToClient(client, eError);
        }
#else
        sendToClient(client, eControl);
#endif
    } else if (command->startsWith(aControl)) {
        serialPrint(LOG_DEBUG, "Received control command");
        if (0 == remoteControl(command->c_str())) {
            serialPrint(LOG_INFO, "Remote control successfully");
            sendToClient(client, eControlSuccess);
        } else {
            sendToClient(client, eControlFailed);
        }
    } else if (command->startsWith(aError)) {
        serialPrint(LOG_DEBUG, "Received error command");
        onError(client);
    }
}

void loop() {
    if (WiFi.status() != WL_CONNECTED) {
        serialPrint(LOG_INFO, "Connection lost, reconnecting");
        status = WiFi.begin(ssid, pass);
        if (status == WL_CONNECTED) {
            serialPrint(LOG_INFO, "Reconnected");
        }
    } else {
        printWiFiStatus();
        if (!client || !client.connected()) {
            client = server.available();
            if (client) {
                clientConnected = true;
                onConnected(&client);
            }
        }
        if (client && client.connected()) {
            if (client.available()) {
                String received = client.readStringUntil('\n');
                received.trim();

                if (received.length() > 0) {
                    serialPrint(LOG_VERBOSE, "Data received: %d", received.length());
                    onCommand(&client, &received);
                }
            }
        } else if (clientConnected) {
            onDisconnected(&client);
            clientConnected = false;
            client.stop();
        }
    }
}
