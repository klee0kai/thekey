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

SplitPasswords thekey_v2::split(const std::string &passw) {
    auto passw_wide = from(passw);
    auto type = find_scheme_id(passw_wide, 30);
    SplitPasswords passwords{};

    auto passwordForOtp = password_masked(type, passw_wide, 0.9f);
    auto passwForLogin = password_masked(type, passw_wide, 0.8f);
    auto passwForHistPassw = password_masked(type, passw_wide, 0.7f);
    auto passwForDescription = password_masked(type, passw_wide, 0.5f);

    return {
            .passwForPassw = passw,
            .passwForOtp = from(passwordForOtp),
            .passwForLogin = from(passwForLogin),
            .passwForHistPassw = from(passwForHistPassw),
            .passwForDescription = from(passwForDescription),
    };
}