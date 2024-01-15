//
// Created by panda on 06.06.2020.
//

#ifndef TKCORE_CMD_PROCESSING_H
#define TKCORE_CMD_PROCESSING_H

#include "def_header.h"


namespace thekey_term {

    void printHelp();

    void findStorages(const std::string &storage);

    void login(const std::string &filePath);

}

#endif //TKCORE_CMD_PROCESSING_H
