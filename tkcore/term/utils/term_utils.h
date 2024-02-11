//
// Created by panda on 07.06.2020.
//

#ifndef THEKEY_TERM_UTILS_H
#define THEKEY_TERM_UTILS_H

#include "../def_header.h"

namespace term {

    void flush_await();

    int checkInput();

    std::string ask_from_term(const std::string &message = "");

    int ask_int_from_term(const std::string &message = "");

    std::string ask_password_from_term(const std::string &message = "");

}

#endif //THEKEY_TERM_UTILS_H
