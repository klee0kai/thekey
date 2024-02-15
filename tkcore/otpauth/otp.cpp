//
// Created by panda on 10.02.24.
//

#include <map>
#include <openssl/evp.h>
#include <cstring>
#include <openssl/hmac.h>
#include "otp.h"
#include "tools/base32.h"

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

string key_otp::generateByCounter(const OtpInfo &otp, uint64_t counter) {
    auto secret = base32::decode(otp.secretBase32);
    auto algo = algoMap[otp.algorithm];

    //rfc4226 5.1
    //C       8-byte counter value, the moving factor.  This counter
    //        MUST be synchronized between the HOTP generator (client)
    //        and the HOTP validator (server).
    counter = swap(counter, bigEndian);

    // rfc4226 5.3
    // Step 1: Generate an HMAC-SHA-1 value Let HS = HMAC-SHA-1(K,C)
    //      HS is a 20-byte string
    char hmacResult[algo.hmacLen + 1];
    memset(hmacResult, 0, sizeof(hmacResult));
    unsigned int hmacLen = algo.hmacLen;
    HMAC(
            algo.method,                                                // algorithm
            (unsigned char *) secret.c_str(), secret.length(),          // key
            (unsigned char *) &counter, sizeof(counter),                // data
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
    for (int i = 0; i < otp.digits; ++i) module *= 10;
    binCode %= module;

    string result;
    result.reserve(otp.digits);
    result = to_string(binCode);
    while (result.length() < otp.digits) result = "0" + result;
    return result;
}

std::string key_otp::generate(key_otp::OtpInfo &otp, time_t now) {
    switch (otp.method) {
        case OTP:
            return generateByCounter(otp, otp.count);
        case TOTP:
            return generateByCounter(otp, otp.interval ? now / otp.interval : 0);
        case HOTP:
            return generateByCounter(otp, otp.count++);
    }
    return "";
}