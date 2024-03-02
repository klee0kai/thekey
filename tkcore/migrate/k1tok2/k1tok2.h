//
// Created by panda on 01.03.24.
//

#ifndef THEKEY_K1TOK2_H
#define THEKEY_K1TOK2_H

#include "key_core.h"
#include "key1.h"
#include "key2.h"

namespace thekey_v1 {

    int migrateK1toK2(
            const std::string &inPath,
            const std::string &outPath,
            const std::string &passw
    );

    int migrateK1toK2(
            thekey_v1::KeyStorageV1 &source,
            thekey_v2::KeyStorageV2 &dest
    );

}

#endif //THEKEY_K1TOK2_H
