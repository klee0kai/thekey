//
// Created by panda on 24.01.24.
//

#include <gtest/gtest.h>
#include <regex>
#include "otp.h"
#include "tools/base32.h"

#include <openssl/sha.h>

using namespace std;
using namespace key_otp;

TEST(YaOtpGen, YaUri) {
    auto secret = base32::decodeRaw("6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY");
    if (secret.size() > 16) secret.resize(16);
    auto pin = to_vector("5239");
    auto pinWithHash = std::vector<uint8_t>();
    pinWithHash.insert(pinWithHash.end(), pin.begin(), pin.end());
    pinWithHash.insert(pinWithHash.end(), secret.begin(), secret.end());

    auto keyHash = sha256(pinWithHash);

    cout << "pinBase32 " << base32::encode(pin, true) << endl;
    cout << "pinWithHash " << base32::encode(pinWithHash) << endl;
    cout << "keyHash " << base32::encode(keyHash, true) << endl;


    ASSERT_EQ("2DTXOMKLGZTW6I6ENEVFZEBDRZQQOZ7H4GOVA7TVME47BQ32W74Q",
              base32::encode(keyHash, true));

}


TEST(YaOtpGen, GenYandexRealUri) {
//    // Given
//    auto now = time(NULL);
//    auto yaotp = generateYaOtpRaw(
//            base32::decodeRaw(string("12343") + "SU3JMDYNPBJG32OGXKCXM23HDA"),
//            SHA256,
//            now / 30,
//            8
//    );
//    cout << "time = " << now << endl;
//    cout << "yaotp = " << yaotp << endl;

}


TEST(YaOtpGen, Aegis1Test) {
    // Given
//    base32::decodeRaw(string("5239") + "6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY"),

    auto yaotp = generateYaOtpRaw(
            base32::decodeRaw("6SB2IKNM6OBZPAVBVTOHDKS4FA"),
            OtpAlgo::SHA256,
            1641559648L / 30,
            8
    );
    cout << "yaotp = " << yaotp << endl;

    ASSERT_EQ("umozdicq", yaotp);

}

