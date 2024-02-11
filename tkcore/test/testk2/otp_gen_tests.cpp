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
    ASSERT_EQ("1",v);
}




TEST(OtpGen, HTOPT4Test) {
    // Given
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y&issuer=someIssuers&digits=4&counter=0";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 0);

    // Then
    ASSERT_EQ("8229",v);
}

TEST(OtpGen, HTOPT4TestCounter1) {
    // Given
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y&issuer=someIssuers&digits=4&counter=0";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 1);

    // Then
    ASSERT_EQ("7837",v);
}


TEST(OtpGen, HTOPT6Test) {
    // Given
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=YLN7MSSHD53KB53C52NWQXCFFXLNGDOP2T5XK6RTE75FWRDROOG7GH5XA4E5GWYVLOXRS7YS5KWAUAQ5EN4FYBMQISZOERCUEZBTVAA&issuer=someIssuers&counter=0";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // When
    auto v = generateByCounter(otp, 1);

    // Then
    ASSERT_EQ("682412",v);
}
