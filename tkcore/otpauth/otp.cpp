//
// Created by panda on 10.02.24.
//

#include <map>
#include <openssl/evp.h>
#include <cstring>
#include <openssl/hmac.h>
#include <iostream>
#include "otp.h"
#include "tools/base32.h"

#define NUMBERS_COUNT 10
#define EN_SYMBOLS_COUNT 26

using namespace std;
using namespace key_otp;

struct OtpAlgoDetails {
    const EVP_MD *method;
    int hmacLen;
};

static map<OtpAlgo, const OtpAlgoDetails> algoMap = {
        {OtpAlgo::SHA1,   {.method = EVP_sha1(), .hmacLen= 160 / 8 /* 20 */  }},
        {OtpAlgo::SHA256, {.method = EVP_sha256(), .hmacLen= 256 / 8 /* 32 */ }},
        {OtpAlgo::SHA512, {.method = EVP_sha512(), .hmacLen= 512 / 8 /* 64 */ }}
};

std::string key_otp::generateOtpRaw(
        const std::vector<uint8_t> &secret,
        const OtpAlgo &algorithm,
        const uint64_t &counter,
        const uint &digits
) {
    auto algo = algoMap[algorithm];

    //rfc4226 5.1
    //C       8-byte counter value, the moving factor.  This counter
    //        MUST be synchronized between the HOTP generator (client)
    //        and the HOTP validator (server).
    uint64_t count = swap(counter, bigEndian);

    // rfc4226 5.3
    // Step 1: Generate an HMAC-SHA-1 value Let HS = HMAC-SHA-1(K,C)
    //      HS is a 20-byte string
    char hmacResult[algo.hmacLen + 1];
    memset(hmacResult, 0, sizeof(hmacResult));
    unsigned int hmacLen = algo.hmacLen;
    HMAC(
            algo.method,                                                // algorithm
            (unsigned char *) secret.data(), secret.size(),             // key
            (unsigned char *) &count, sizeof(count),                    // data
            (unsigned char *) hmacResult,                               // output
            &hmacLen                                                    // output length
    );

    //  rfc4226 5.4
    uint64_t offset = hmacResult[19] & 0xf;
    uint64_t binCode = (hmacResult[offset] & 0x7f) << 24
                       | (hmacResult[offset + 1] & 0xff) << 16
                       | (hmacResult[offset + 2] & 0xff) << 8
                       | (hmacResult[offset + 3] & 0xff);

    uint64_t module = 1;
    for (int i = 0; i < digits; ++i) module *= NUMBERS_COUNT;
    binCode %= module;

    string result;
    result.reserve(digits);
    result = to_string(binCode);
    while (result.length() < digits) result = "0" + result;
    return result;
}

// based on
std::string key_otp::generateYaOtpRaw(
        const std::vector<uint8_t> &secret,
        const key_otp::OtpAlgo &algorithm,
        const uint64_t &counter,
        const uint &digits
) {
    auto algo = algoMap[algorithm];

    //rfc4226 5.1
    //C       8-byte counter value, the moving factor.  This counter
    //        MUST be synchronized between the HOTP generator (client)
    //        and the HOTP validator (server).
    uint64_t count = htobig(counter);


    // rfc4226 5.3
    // Step 1: Generate an HMAC-SHA-1 value Let HS = HMAC-SHA-1(K,C)
    //      HS is a 20-byte string
    char hmacResult[algo.hmacLen + 1];
    memset(hmacResult, 0, sizeof(hmacResult));
    unsigned int hmacLen = algo.hmacLen;
    HMAC(
            algo.method,                                                // algorithm
            (unsigned char *) secret.data(), secret.size(),             // key
            (unsigned char *) &count, sizeof(count),                    // data
            (unsigned char *) hmacResult,                               // output
            &hmacLen                                                    // output length
    );

    //  rfc4226 5.4
    uint64_t offset = hmacResult[algo.hmacLen - 1] & 0xf;
    hmacResult[offset] &= 0x7f;
    uint64_t binCode = htobig(*(uint64_t *) (hmacResult + offset));

    uint64_t module = 1;
    for (int i = 0; i < digits; ++i) module *= EN_SYMBOLS_COUNT;
    binCode %= module;

    char yaotpCode[digits + 1];
    yaotpCode[digits] = 0;
    for (int i = digits - 1; i >= 0; --i) {
        yaotpCode[i] = char('a' + (binCode % EN_SYMBOLS_COUNT));
        binCode /= EN_SYMBOLS_COUNT;
    }

    return yaotpCode;
}

string key_otp::generateOtpRaw(const OtpInfo &otp, uint64_t counter) {
    return generateOtpRaw(
            otp.secret,
            otp.algorithm,
            counter,
            otp.digits
    );
}

std::string key_otp::generate(key_otp::OtpInfo &otp, time_t now) {
    switch (otp.method) {
        case OTP:
            return generateOtpRaw(otp, otp.counter);
        case TOTP:
            return generateOtpRaw(otp, otp.interval ? now / otp.interval : 0);
        case HOTP:
            return generateOtpRaw(otp, otp.counter++);
        case YAOTP:
            //TODO
            break;
    }
    return "";
}
