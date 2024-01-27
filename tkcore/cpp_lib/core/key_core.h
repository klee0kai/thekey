//
// Created by panda on 11.05.2020.
//

#ifndef THEKEY_KEY_CORE_H
#define THEKEY_KEY_CORE_H

#include <string>
#include <fcntl.h>
#include <unistd.h>
#include <vector>
#include <list>
#include <ctime>
#include <cstdio>
#include <memory>
#include <unistd.h>
#include <netinet/in.h>
#include <filesystem>

#include "key_errors.h"
#include "key_signatures.h"

#define MAX(a, b) (a>b?a:b)
#define MIN(a, b) (a<b?a:b)

#define INT32_BIG_ENDIAN(name) \
    uint32_t raw##name;        \
    [[nodiscard]] uint32_t name() const { return htonl(raw##name); } \
    void name(uint32_t name) { raw##name = htonl(name); }

#define INT64_BIG_ENDIAN(name) \
    uint64_t raw##name;        \
    [[nodiscard]] uint64_t name() const { return htole64(raw##name); } \
    void name(uint64_t name) { raw##name = htole64(name); }

#define INT32_BIG_ENDIAN_ENUM(name, enum) \
    uint32_t raw##name;        \
    [[nodiscard]] enum name() const { return enum( htonl(raw##name) ); } \
    void name(enum name) { raw##name = htonl( uint32_t(name) ); }


#endif //THEKEY_KEY_CORE_H
