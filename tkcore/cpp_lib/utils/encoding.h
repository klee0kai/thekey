//
// Created by panda on 19.01.24.
//

#ifndef THEKEY_ENCODING_H
#define THEKEY_ENCODING_H

#include "thekey_core.h"

namespace tkey_encoding {

    uint32_t findEncodingType(const std::string &str);

    uint32_t findEncodingTypeByFlags(const uint32_t &flags);

    uint32_t encodingLen(uint32_t type);

    std::string encodingSymbols(uint32_t type);

}

#endif //THEKEY_ENCODING_H
