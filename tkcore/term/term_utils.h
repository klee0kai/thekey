//
// Created by panda on 07.06.2020.
//

#ifndef THEKEY_TERM_UTILS_H
#define THEKEY_TERM_UTILS_H

#include "def_header.h"

namespace term_utils {

    std::string ask_from_term(std::string message);

    std::string ask_password_from_term(std::string message);

    /**
    *
    * @param sourceText изменяет исходный текст
    * @param argsOut
    * @param len
    */
    void splitArgs(char *sourceText, char **&argsOut, size_t &len);

    size_t argsCount(const char *sourceText);

    /**
     * очистить константы getopt
     */
    void clear_opt();
}

#endif //THEKEY_TERM_UTILS_H
