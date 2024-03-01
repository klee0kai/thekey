//
// Created by panda on 01.03.24.
//

#ifndef THEKEY_K1TOK2_H
#define THEKEY_K1TOK2_H

#include "key_core.h"

namespace thekey_v1 {

    int migrateK1toK2(
            const std::string &inPath,
            const std::string &outPath,
            const std::string &passw
    );

}

#endif //THEKEY_K1TOK2_H
