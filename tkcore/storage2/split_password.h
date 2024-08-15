//
// Created by panda on 21.01.24.
//

#ifndef THEKEY_SPLIT_PASSWORD_H
#define THEKEY_SPLIT_PASSWORD_H

#include <set>
#include "key_core.h"

namespace thekey_v2 {

    struct SplitPasswords {
        std::string passwForPassw; // passw power 1f
        std::string passwForOtp; // passw power 0.9f
        std::string passwForLogin; // passw power 0.8f
        std::string passwForHistPassw; // passw power 0.7f
        std::string passwForDescription; // passw power 0.5f
    };

    struct PasswordTwins {
        std::set<std::string> passwForOtpTwins;
        std::set<std::string> passwForLoginTwins;
        std::set<std::string> passwForHistPasswTwins;
        std::set<std::string> passwForDescriptionTwins;
    };

    SplitPasswords split(const std::string &passw);

    PasswordTwins twins(const std::string &passw);

}

#endif //THEKEY_SPLIT_PASSWORD_H
