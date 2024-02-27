//
// Created by panda on 24.01.24.
//

#include <gtest/gtest.h>
#include "otp.h"
#include "tools/base32.h"

using namespace std;
using namespace key_otp;

/**
 * Tests From
 * https://github.com/beemdevelopment/Aegis/blob/224ec2553c1d2d0c6bec4236c464bc132b426581/app/src/test/java/com/beemdevelopment/aegis/crypto/otp/YAOTPTest.java#L22
 */

TEST(YaOtpGenAegis, TruncateYaOtpSecretTest) {
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    ASSERT_EQ("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(yaotp.secret, true));
}

TEST(YaOtpGenAegis, Get1Test) {
    // Given
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    //when
    yaotp.pin = "5239";
    auto code = generate(yaotp, 1641559648L);

    // then
    ASSERT_EQ("umozdicq", code);
}


TEST(YaOtpGenAegis, Get2Test) {
    // Given
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=LA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    // when
    yaotp.pin = "7586";
    auto code = generate(yaotp, 1581064020L);

    // then
    ASSERT_EQ("oactmacq", code);
}


TEST(YaOtpGenAegis, Get3Test) {
    // Given
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=LA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    // when
    yaotp.pin = "7586";
    auto code = generate(yaotp, 1581090810L);

    //then
    ASSERT_EQ("wemdwrix", code);
}


TEST(YaOtpGenAegis, Get4Test) {
    // Given
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=JBGSAU4G7IEZG6OY4UAXX62JU4AAAAAAHTSG4HXU3M&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    //when
    yaotp.pin = "5210481216086702";
    auto code = generate(yaotp, 1581091469L);

    // then
    ASSERT_EQ("dfrpywob", code);
}


TEST(YaOtpGenAegis, Get5Test) {
    // Given
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=JBGSAU4G7IEZG6OY4UAXX62JU4AAAAAAHTSG4HXU3M&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    // When
    yaotp.pin = "5210481216086702";
    auto code = generate(yaotp, 1581093059L);

    // then
    ASSERT_EQ("vunyprpd", code);
}

