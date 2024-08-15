//
// Created by panda on 21.01.24.
//

#include "split_password.h"
#include "salt_text/salt2.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace thekey_v2;
using namespace thekey_v2;
using namespace key_salt;

#define OTP_PASSW_POWER 0.8f
#define LOGIN_PASSW_POWER 0.7f
#define HIST_PASSW_POWER 0.6f
#define DESC_PASSW_POWER 0.5f

SplitPasswords thekey_v2::split(const std::string &passw) {
    auto passw_wide = from(passw);
    auto type = find_scheme_id(passw_wide, 30);
    SplitPasswords passwords{};

    auto passwordForOtp = password_masked(type, passw_wide, OTP_PASSW_POWER);
    auto passwForLogin = password_masked(type, passw_wide, LOGIN_PASSW_POWER);
    auto passwForHistPassw = password_masked(type, passw_wide, HIST_PASSW_POWER);
    auto passwForDescription = password_masked(type, passw_wide, DESC_PASSW_POWER);

    return {
            .passwForPassw = passw,
            .passwForOtp = from(passwordForOtp),
            .passwForLogin = from(passwForLogin),
            .passwForHistPassw = from(passwForHistPassw),
            .passwForDescription = from(passwForDescription),
    };
}

PasswordTwins thekey_v2::twins(const std::string &passw) {
    auto passw_wide = from(passw);
    auto type = find_scheme_id(passw_wide, 30);

    auto twins = PasswordTwins{};
    for (int i = 0; i < 20; ++i) {
        auto passwForDescriptionTwin = from(password_masked_twin(type, passw_wide, DESC_PASSW_POWER));
        if (passwForDescriptionTwin != passw) {
            twins.passwForDescriptionTwins.insert(twins.passwForDescriptionTwins.end(), passwForDescriptionTwin);
        }
    }


    for (int i = 0; i < 20; ++i) {
        auto passwForHistPasswTwin = from(password_masked_twin(type, passw_wide, HIST_PASSW_POWER));
        if (passwForHistPasswTwin != passw) {
            twins.passwForHistPasswTwins.insert(twins.passwForHistPasswTwins.end(), passwForHistPasswTwin);
        }
    }

    for (int i = 0; i < 20; ++i) {
        auto passwForLoginTwin = from(password_masked_twin(type, passw_wide, LOGIN_PASSW_POWER));
        if (passwForLoginTwin != passw) {
            twins.passwForLoginTwins.insert(twins.passwForLoginTwins.end(), passwForLoginTwin);
        }
    }

    for (int i = 0; i < 20; ++i) {
        auto passwordForOtpTwin = from(password_masked_twin(type, passw_wide, OTP_PASSW_POWER));
        if (passwordForOtpTwin != passw) {
            twins.passwForOtpTwins.insert(twins.passwForOtpTwins.end(), passwordForOtpTwin);
        }
    }

    return twins;
}