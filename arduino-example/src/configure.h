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

#ifndef ARDUINO_EXAMPLE_CONFIGURE_H
#define ARDUINO_EXAMPLE_CONFIGURE_H

#include <cstdint>

// Wi-Fi Configs
#define SECRET_SSID "maomao"
#define SECRET_PASS "20121207"

// #define TEST_BIN_RECEIVE  (1)

// LED Matrix Definitions
constexpr uint32_t chip[] = {
    0x1503f811,
    0x3181103,
    0xf8150000
};

constexpr uint32_t danger[] = {
    0x400a015,
    0x1502082,
    0x484047fc
};

constexpr uint32_t happy[] = {
    0x19819,
    0x80000001,
    0x81f8000
};

constexpr uint32_t heart[] = {
    0x3184a444,
    0x44042081,
    0x100a0040
};

constexpr uint32_t fullOn[] = {
    0xffffffff,
    0xffffffff,
    0xffffffff
};

#endif //ARDUINO_EXAMPLE_CONFIGURE_H