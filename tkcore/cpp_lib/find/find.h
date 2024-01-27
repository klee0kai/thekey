//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_FIND_H
#define THEKEY_FIND_H

#include "core/key_core.h"

namespace thekey {

    std::list<Storage> findStorages(const std::string &filePath);

    void findStorages(const std::string &filePath, void (*foundStorageCallback)(const Storage &));

    std::shared_ptr<Storage> storage(const std::string &path);

}

#endif //THEKEY_FIND_H
