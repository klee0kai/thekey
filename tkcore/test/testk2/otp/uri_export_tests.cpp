//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include <regex>
#include "tools/uri.h"
#include "otp.h"

using namespace std;
using namespace key_otp;


TEST(URIExportTests, ExportYaOtpTest) {
    auto yaotpUri = "otpauth://yaotp/user@yandex.ru?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user";
    auto yaotpList = parseOtpUri(yaotpUri);
    auto yaotp = yaotpList.front();

    ASSERT_EQ(
            "otpauth://yaotp/user%40yandex.ru?secret=6SB2IKNM6OBZPAVBVTOHDKS4FA&issuer=yandex.ru&algorithm=sha256&digits=8",
            yaotp.toUri());
}

