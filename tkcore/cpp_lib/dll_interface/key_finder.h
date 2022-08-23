//
// Created by panda on 07.06.2020.
//

#ifndef THEKEY_KEY_FINDER_H
#define THEKEY_KEY_FINDER_H

#include "dll_interface/key_manager_ctx.h"

namespace key_finder {

    int findStorages(const char *srcDir, void  (*fnStoreFound)(Storage));

}

#endif //THEKEY_KEY_FINDER_H
