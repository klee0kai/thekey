//
// Created by panda on 19.01.24.
//

#ifndef tkey2_salt_header
#define tkey2_salt_header

#include "thekey_core.h"

namespace tkey2_salt_text {

    uint32_t findEncodingType(const std::string &str);

    uint32_t findEncodingTypeByFlags(const uint32_t &flags);

    uint32_t encodingLen(uint32_t type);

    std::string encodingSymbols(uint32_t type);

}

#endif //tkey2_salt_header
