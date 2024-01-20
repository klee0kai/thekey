//
// Created by panda on 20.01.24.
//

#ifndef THEKEY_SALT_BASE_H
#define THEKEY_SALT_BASE_H

#include <cstdlib>
#include <cstdint>
#include <codecvt>
#include <locale>

#define TYPE_MAX(typeLen) ((1L << ( (typeLen) * 8L)) -1L)
#define SALT_IN_RING(x, ring) ( (x) + (ring) * tkey_salt::rand( TYPE_MAX( sizeof(x) ) / (ring) ))
#define DESALT_IN_RING(x, ring) ( x % ring )


namespace tkey_salt {

    typedef uint32_t wide_char;
    typedef std::basic_string<wide_char> wide_string;
    extern std::wstring_convert<std::codecvt_utf8<wide_char>, wide_char> converter;

    long rand(ulong max);

    void randmem(unsigned char *mem, uint len);

    void memsalt(unsigned char *mem, uint len, uint ring);

    void memdesalt(unsigned char *mem, uint len, uint ring);

    // --- wide char ---
    void randmem(wide_char *mem, uint len);

    void memsalt(wide_char *mem, uint len, uint ring);

    void memdesalt(wide_char *mem, uint len, uint ring);

}

#endif //THEKEY_SALT_BASE_H
