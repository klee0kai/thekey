//
// Created by panda on 24.01.24.
//

#include <gtest/gtest.h>
#include <regex>
#include <openssl/hmac.h>
#include <openssl/sha.h>
#include <cmath>
#include "otp.h"
#include "tools/base32.h"

using namespace std;
using namespace key_otp;

TEST(OtpAlgo, ParseSha1GoogleGenerate) {
    // Given
    OtpInfo otp = {
            .method = HOTP,
            .algorithm = OtpAlgo::SHA1,
            .secret = base32::decodeRaw("JBSWY3DPEHPK3PXP"),
            .digits = 6
    };

    // When
    auto v = generateOtpRaw(otp, 2);

    // Then
    ASSERT_EQ("602287", v);
}

TEST(OtpAlgo, ParseSha256GoogleGenerate) {
    // Given
    OtpInfo otp = {
            .method = HOTP,
            .algorithm = OtpAlgo::SHA256,
            .secret = base32::decodeRaw(
                    "H7U7ZOPB4YPPGBHBAJSQH2MQZUVVOARKUL4K3QCFKK5AMJ4QW23K56PXOKCNLZZ5PV654FYMXXCMUIBGNGFRNFWHWIAL6MMLAJVPRXY"),
            .digits = 6
    };

    // When
    auto v = generateOtpRaw(otp, 2);

    // Then
    ASSERT_EQ("265369", v);
}


TEST(OtpAlgo, ParseSha512GoogleGenerate) {
    // Given
    OtpInfo otp = {
            .method = HOTP,
            .algorithm = OtpAlgo::SHA512,
            .secret = base32::decodeRaw(
                    "S2KMLCACOGGUCMZOWXOCYRNHGQ7NFQCRZCWQ63K4X5STBUITRNCY3HIOHMTBR22C24ZSFR4YJLE7NHQSNOBLU2U6A3EDNLMGJP536KY"),
            .digits = 6
    };

    // When
    auto v = generateOtpRaw(otp, 2);

    // Then
    ASSERT_EQ("751909", v);
}

TEST(OtpAlgo, ParseMD5GoogleGenerate) {
    // Given
    OtpInfo otp = {
            .method = HOTP,
            .algorithm = OtpAlgo::MD5,
            .secret = base32::decodeRaw(
                    "T7IKNIR4F2S2XWZU44F3PS7GHODAAXPPDHOQKCMQIYMLTHERK7XBXAFQOHJ43EJYBCUKBTXN6YOH7BO5C3J6DKQVKACLHUE565Y2CHY"),
            .digits = 6
    };

    // When
    auto v = generateOtpRaw(otp, 2);

    // Then
    ASSERT_EQ("784924", v);
}


std::string generateHOTP(const std::vector<uint8_t> &key, uint64_t counter, size_t digits = 6) {
    // Convert counter to network byte order
    counter = htobe64(counter);

    // Compute HMAC using SHA-256
    unsigned char hmac[SHA256_DIGEST_LENGTH];
    HMAC(EVP_sha256(), key.data(), key.size(), (unsigned char *) &counter, sizeof(counter), hmac, NULL);

    // Dynamic truncation
    int offset = hmac[SHA256_DIGEST_LENGTH - 1] & 0xf;
    int binary = ((hmac[offset] & 0x7f) << 24) |
                 ((hmac[offset + 1] & 0xff) << 16) |
                 ((hmac[offset + 2] & 0xff) << 8) |
                 (hmac[offset + 3] & 0xff);

    // Generate HOTP value
    int hotp = binary % (int) pow(10, digits);

    // Pad with zeroes if necessary
    std::ostringstream oss;
    oss << std::setw(digits) << std::setfill('0') << hotp;

    return oss.str();
}


TEST(OtpAlgo, ParseSha256GoogleGenerate1) {
    // Given
    auto key = base32::decodeRaw(
            "H7U7ZOPB4YPPGBHBAJSQH2MQZUVVOARKUL4K3QCFKK5AMJ4QW23K56PXOKCNLZZ5PV654FYMXXCMUIBGNGFRNFWHWIAL6MMLAJVPRXY"); // Change this to your secret key
    uint64_t counter = 2; // Change this to your counter value

    std::string hotp = generateHOTP(key, counter);

    std::cout << "HOTP: " << hotp << std::endl;

    // Then
    ASSERT_EQ("265369", hotp);
}