//
// Created by panda on 27.01.24.
//

#ifndef THEKEY_KEY_SIGNATURES_H
#define THEKEY_KEY_SIGNATURES_H

#include <string>

#define SIGNATURE_LEN 7

#define STORAGE_VER_FIRST 0x01
/**
 * unsigned char rawStorageVersion; without htonl support
 */
#define TKEY_SIGNATURE_V1 {'t', 'k', 'e', 'y', 0x2, 0x47, 0x00}


#define STORAGE_VER_SECOND 0x02
/**
 * unsigned int rawStorageVersion; with htonl support
 */
#define TKEY_SIGNATURE_V2 {'t', 'k', 'e', 'y', 0x2, 0x47, 0x02}

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


namespace thekey {

    struct Storage {
        std::string file;
        unsigned int storageVersion;
        std::string name;
        std::string description;
    };

    extern const char *const storageFormat;
    extern const char storageSignature_V1[SIGNATURE_LEN];
    extern const char storageSignature_V2[SIGNATURE_LEN];

}


#endif //THEKEY_KEY_SIGNATURES_H
