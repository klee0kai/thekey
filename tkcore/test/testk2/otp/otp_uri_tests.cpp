//
// Created by panda on 24.01.24.
//

#include <gtest/gtest.h>
#include <regex>
#include "otp.h"
#include "tools/base32.h"

using namespace std;
using namespace key_otp;

TEST(OtpUri, ParseGoogleExample) {
    // When
    string gUri = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example";
    OtpInfo otp = parseOtpUri(gUri).front();

    // Then
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ(OtpType::TOTP, otp.method);
    ASSERT_EQ("Example", otp.issuer);
    ASSERT_EQ("alice@google.com", otp.name);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otp.secret, true));
}

TEST(OtpUri, ExportImportTest) {
    // Given
    string originUri = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example";
    OtpInfo otpOrigin = parseOtpUri(originUri).front();

    // When
    auto exportUri = otpOrigin.toUri();
    OtpInfo otp = parseOtpUri(exportUri).front();

    // Then
    ASSERT_EQ(otpOrigin.scheme, otp.scheme);
    ASSERT_EQ(otpOrigin.method, otp.method);
    ASSERT_EQ(otpOrigin.issuer, otp.issuer);
    ASSERT_EQ(otpOrigin.name, otp.name);
    ASSERT_EQ(otpOrigin.secret, otp.secret);
}

TEST(OtpUri, HotpEncodedTest) {
    // When
    string gUri = "otpauth://hotp/someIssuers%3Asome%40mail.rd?secret=OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y&issuer=someIssuers&digits=6&counter=10";
    OtpInfo otp = parseOtpUri(gUri).front();

    // Then
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ(OtpType::HOTP, otp.method);
    ASSERT_EQ(OtpAlgo::SHA1, otp.algorithm);
    ASSERT_EQ("someIssuers", otp.issuer);
    ASSERT_EQ("some@mail.rd", otp.name);
    ASSERT_EQ("OIS7EQ3JU3OY2NSEZ3GQIXIMR6XB3MKDWCMZPER44RZIFVE6PXRKT4KFN66VZGAXQE2J7Q45IY6YAXVK3S7GBW2PMNTDAJQMKNMH35Y",
              base32::encode(otp.secret, true));
    ASSERT_EQ(6, otp.digits);
    ASSERT_EQ(10, otp.counter);
}


TEST(OtpUri, HotpDecodedTest) {
    // When
    string gUri = "otpauth://hotp/someIssuers:user@addres.com?secret=UJ3G7B6662EESSP5DHGAY25MAPJG5SSEFGULWLQYP6WKFFNQXC5NESFOYOHZJRZRXAALPCF63CIYCYP4ACYIJX7TEHYNMBRY7HOLFZQ&issuer=someIssuers&algorithm=sha512&counter=10";
    OtpInfo otp = parseOtpUri(gUri).front();

    // Then
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ(OtpType::HOTP, otp.method);
    ASSERT_EQ(OtpAlgo::SHA512, otp.algorithm);
    ASSERT_EQ("someIssuers", otp.issuer);
    ASSERT_EQ("user@addres.com", otp.name);
    ASSERT_EQ("UJ3G7B6662EESSP5DHGAY25MAPJG5SSEFGULWLQYP6WKFFNQXC5NESFOYOHZJRZRXAALPCF63CIYCYP4ACYIJX7TEHYNMBRY7HOLFZQ",
              base32::encode(otp.secret, true));
    ASSERT_EQ(6, otp.digits);
    ASSERT_EQ(10, otp.counter);
}


TEST(OtpUri, GoogleAuthTest) {
    // When
    string gUri = "otpauth://totp/employee%40company.com?secret=QTSC7ZCECAN7OHFGGJCJM62JXGZ4CIRBR4MTEZTT32LB"
                  "S25SJMKI4NTYN3S2FXMGC5EBTKEMFYCPFGZM6VNDUKXHRX25RWEVUB7N2MY";
    list<OtpInfo> otpNotes = parseOtpUri(gUri);
    auto otp = *otpNotes.begin();

    // Then
    ASSERT_EQ(OtpMethod::TOTP, otp.method);
    ASSERT_EQ(OtpScheme::authuri, otp.scheme);
    ASSERT_EQ("employee@company.com", otp.name);
    ASSERT_EQ("company.com", otp.issuer);
    ASSERT_EQ("QTSC7ZCECAN7OHFGGJCJM62JXGZ4CIRBR4MTEZTT32LB"
              "S25SJMKI4NTYN3S2FXMGC5EBTKEMFYCPFGZM6VNDUKXHRX25RWEVUB7N2MY",
              base32::encode(otp.secret, true));
}


TEST(OtpUri, YaotpUriTest) {
    // When
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    //then
    ASSERT_EQ("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(yaotp.secret, true));
    ASSERT_EQ("yandex.ru", yaotp.issuer);
    ASSERT_EQ("user@yandex.ru", yaotp.name);

}
