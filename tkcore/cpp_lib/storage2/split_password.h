//
// Created by panda on 21.01.24.
//

#ifndef THEKEY_SPLIT_PASSWORD_H
#define THEKEY_SPLIT_PASSWORD_H

#include "key_core.h"

namespace thekey_v2 {

    struct SplitPasswords {
        std::string passwForPassw; // passw power 1f
        std::string passwForLogin; // passw power 0.8f
        std::string passwForHistPassw; // passw power 0.7f
        std::string passwForDescription; // passw power 0.5f
    };

    SplitPasswords split(const std::string &passw);

}

#endif //THEKEY_SPLIT_PASSWORD_H
