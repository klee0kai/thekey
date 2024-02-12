//
// Created by panda on 24.01.24.
//

#include <gtest/gtest.h>
#include <regex>
#include "otp.h"

using namespace std;
using namespace key_otp;

TEST(OtpUri, ParseGoogleExample) {
    // When
    string gUri = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // Then
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ(OtpType::TOTP, otp.method);
    ASSERT_EQ("Example", otp.issuer);
    ASSERT_EQ("alice@google.com", otp.name);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", otp.secretBase32);
    ASSERT_EQ("Example", otp.issuer);
}


TEST(OtpUri, HotpEncodedTest) {
    // When
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y&issuer=someIssuers&digits=6&counter=10";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // Then
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ(OtpType::HOTP, otp.method);
    ASSERT_EQ(OtpAlgo::SHA1, otp.algorithm);
    ASSERT_EQ("someIssuers", otp.issuer);
    ASSERT_EQ("some@mail.rd", otp.name);
    ASSERT_EQ("OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y",
              otp.secretBase32);
    ASSERT_EQ(6, otp.digits);
    ASSERT_EQ(10, otp.count);
}


TEST(OtpUri, HotpDecodedTest) {
    // When
    string gUri = "otpauth://hotp/someIssuers:user@addres.com?secret=UJ3G7B6662EESSP5DHGAY25MAPJG5SSEFGULWLQYP6WKFFNQXC5NESFOYOHZJRZRXAALPCF63CIYCYP4ACYIJX7TEHYNMBRY7HOLFZQ&issuer=someIssuers&algorithm=sha512&counter=10";
    OtpInfo otp = OtpInfo::fromUri(gUri);

    // Then
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ(OtpType::HOTP, otp.method);
    ASSERT_EQ(OtpAlgo::SHA512, otp.algorithm);
    ASSERT_EQ("someIssuers", otp.issuer);
    ASSERT_EQ("user@addres.com", otp.name);
    ASSERT_EQ("UJ3G7B6662EESSP5DHGAY25MAPJG5SSEFGULWLQYP6WKFFNQXC5NESFOYOHZJRZRXAALPCF63CIYCYP4ACYIJX7TEHYNMBRY7HOLFZQ",
              otp.secretBase32);
    ASSERT_EQ(6, otp.digits);
    ASSERT_EQ(10, otp.count);
}
