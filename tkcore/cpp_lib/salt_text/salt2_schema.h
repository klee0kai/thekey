//
// Created by panda on 20.01.24.
//

#ifndef THEKEY_SALT2_ENCODING_SCHEMA_H
#define THEKEY_SALT2_ENCODING_SCHEMA_H

#include "thekey_core.h"
#include "salt_base.h"

#define SCHEME_NUMBERS 0x1
#define SCHEME_SPEC_SYMBOLS 0x2
#define SCHEME_ENGLISH 0x4
#define SCHEME_RUSSIAN 0x8

namespace tkey2_salt {

    struct SymbolRange {
        tkey_salt::wide_char start{};
        tkey_salt::wide_char end = start;

        [[nodiscard]] int len() const;

        [[nodiscard]] int contains(const tkey_salt::wide_char &sym) const;

        void all_symbols(void (*callback)(const tkey_salt::wide_char &)) const;

    };

    struct EncodingScheme {
        uint32_t type;
        uint32_t flags;
        std::vector<SymbolRange> ranges;

        [[nodiscard]] tkey_salt::wide_char encoded(tkey_salt::wide_char original) const;

        [[nodiscard]] tkey_salt::wide_char decoded(tkey_salt::wide_char original) const;

        [[nodiscard]] uint len() const;

        [[nodiscard]] int all_contains(const tkey_salt::wide_string &wideString) const;

        void all_symbols(void (*callback)(const tkey_salt::wide_char &)) const;

    };

    const EncodingScheme *find_scheme(uint32_t type);

    uint32_t find_scheme_type(const tkey_salt::wide_string &str);

    uint32_t find_scheme_type_by_flags(const uint32_t &flags);

}


#endif //THEKEY_SALT2_ENCODING_SCHEMA_H
