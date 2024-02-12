//
// Created by panda on 24.01.24.
//

#include <gtest/gtest.h>
#include <regex>
#include "otp.h"

using namespace std;
using namespace key_otp;

TEST(OtpGen, ParseGoogleGenerate) {
    // Given
    string gUri = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 2);

    // Then
    ASSERT_EQ("2287", v);
}


TEST(OtpGen, HOTP4Test) {
    // Given
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y&issuer=someIssuers&digits=4&counter=0";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 0);

    // Then
    ASSERT_EQ("8229", v);
}

TEST(OtpGen, HOTP4Counter1Test) {
    // Given
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y&issuer=someIssuers&digits=4&counter=0";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 1);

    // Then
    ASSERT_EQ("7837", v);
}


TEST(OtpGen, HOTP6Test) {
    // Given
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=YLN7MSSHD53KB53C52NWQXCFFXLNGDOP2T5XK6RTE75FWRDROOG7GH5XA4E5GWYVLOXRS7YS5KWAUAQ5EN4FYBMQISZOERCUEZBTVAA&digits=6&issuer=someIssuers&counter=0";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 1);

    // Then
    ASSERT_EQ("682412", v);
}

TEST(OtpGen, TOTPSimple6Test) {
    // Given
    string gUri = "otpauth://totp/sha1Issuer%3Asimple%40test.com?secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A&issuer=sha1Issuer";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generate(otp, 1707657186);

    // Then
    ASSERT_EQ("970135", v);
}

TEST(OtpGen, TOTP6Test) {
    // Given
    string gUri = "otpauth://totp/anyIssuer%3Amyuseradress%40test.com?secret=ADLCW6TRYHEU56SSI73SDRI3KIJEDHBGWUZC4ZOUGDZNAQEKFBLJMA6CSSP6PIR3HSRGP4CAE4PME65ICEN3LP7MTDGWXEH5XOVVGLI&issuer=anyIssuer&period=40&algorithm=sha256";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, time(NULL) / otp.interval);

    // Then
    ASSERT_EQ("682412", v);
}


TEST(OtpGen, GoogleAuthTest) {
    // https://github.com/google/google-authenticator-android/blob/6f65e99fcbc9bbefdc3317c008345db595052a2b/javatests/com/google/android/apps/authenticator/otp/PasscodeGeneratorTest.java#L77
    // Given
    OtpInfo otp = {
            .method = HOTP,
            .algorithm = OtpAlgo::SHA1,
            .secretBase32 = "7777777777777777",
            .digits = 6
    };

    // then
    auto v = generateByCounter(otp, 0);

    //then
    ASSERT_EQ("724477", v);
}


TEST(OtpGen, GoogleAuth2Test) {
    // https://github.com/google/google-authenticator-android/blob/6f65e99fcbc9bbefdc3317c008345db595052a2b/javatests/com/google/android/apps/authenticator/otp/PasscodeGeneratorTest.java#L77
    // Given
    OtpInfo otp = {
            .method = HOTP,
            .algorithm = OtpAlgo::SHA1,
            .secretBase32 = "7777777777777777",
            .digits = 6
    };

    // then
    auto v = generateByCounter(otp, 123456789123456789L);

    //then
    ASSERT_EQ("815107", v);
}