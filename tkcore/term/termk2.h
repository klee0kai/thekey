//
// Created by panda on 21.01.24.
//

#ifndef THEKEY_TERMK2_H
#define THEKEY_TERMK2_H

#include "def_header.h"

namespace thekey_term_v2 {

    void login(const std::string &filePath);

    void interactiveEditNote(const long long &notePtr);

    void interactiveEditOtpNote(const long long &notePtr);

}

#endif //THEKEY_TERMK2_H
