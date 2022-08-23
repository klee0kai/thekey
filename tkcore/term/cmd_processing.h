//
// Created by panda on 06.06.2020.
//

#ifndef TKCORE_CMD_PROCESSING_H
#define TKCORE_CMD_PROCESSING_H

#include <def_header.h>


namespace cmd_pr {
    void printHelp();

    void findStorages(const char *srcDir = NULL);

    void login(const char *filePath);
}

#endif //TKCORE_CMD_PROCESSING_H
