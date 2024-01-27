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
#define SALT_IN_RING(x, ring) ( (x) + (ring) * thekey_salt::rand( TYPE_MAX( sizeof(x) ) / (ring) ))
#define DESALT_IN_RING(x, ring) ( x % ring )


namespace thekey_salt {

    typedef char16_t wide_char;
    typedef std::basic_string<wide_char> wide_string;

    long rand(ulong max);

    void randmem(unsigned char *mem, uint len);

    void memsalt(unsigned char *mem, uint len, uint ring);

    void memdesalt(unsigned char *mem, uint len, uint ring);

    // --- wide char ---
    void randmem(wide_char *mem, uint len);

    void memsalt(wide_char *mem, uint len, uint ring);

    void memdesalt(wide_char *mem, uint len, uint ring);

    std::string from(const wide_string &wideString);

    wide_string from(const std::string &string);

    std::string from(const wide_char &wideChar);

}

#endif //THEKEY_SALT_BASE_H
