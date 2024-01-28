//
// Created by panda on 20.01.24.
//

#ifndef THEKEY_SALT2_ENCODING_SCHEMA_H
#define THEKEY_SALT2_ENCODING_SCHEMA_H

#include "key_core.h"
#include "salt/salt_base.h"

#define SCHEME_NUMBERS 0x1
#define SCHEME_SPEC_SYMBOLS 0x2
#define SCHEME_ENGLISH 0x4
#define SCHEME_RUSSIAN 0x8
#define SCHEME_SPACE_SYMBOL 0x10

namespace thekey_v2 {

    struct SymbolRange {
        thekey_salt::wide_char start{};
        thekey_salt::wide_char end = start;

        [[nodiscard]] int len() const;

        [[nodiscard]] int contains(const thekey_salt::wide_char &sym) const;

        void all_symbols(void (*callback)(const thekey_salt::wide_char &)) const;

    };

    struct EncodingScheme {
        uint32_t type;
        uint32_t flags;
        std::vector<SymbolRange> ranges;

        [[nodiscard]] thekey_salt::wide_char encoded(thekey_salt::wide_char original, int offset = 0) const;

        [[nodiscard]] thekey_salt::wide_char decoded(thekey_salt::wide_char original, int offset = 0) const;

        [[nodiscard]] uint len() const;

        [[nodiscard]] int all_contains(const thekey_salt::wide_string &wideString) const;

        void all_symbols(void (*callback)(const thekey_salt::wide_char &)) const;

    };

    const EncodingScheme *find_scheme(uint32_t type);

    uint32_t find_scheme_type(const thekey_salt::wide_string &str, const int &minLen = 0);

    uint32_t find_scheme_type_by_flags(const uint32_t &flags);

}


#endif //THEKEY_SALT2_ENCODING_SCHEMA_H
