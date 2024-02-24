//
// Created by panda on 20.01.24.
//

#include "salt2.h"
#include "key_core.h"
#include "salt2_schema.h"
#include <cstdarg>
#include <cstring>
#include <algorithm>

using namespace std;
using namespace thekey_v2;
using namespace key_salt;


static vector<EncodingScheme> encodingSchemas = {
        // not inited text. decode to null text
        {.type=0, .flags = SCHEME_NUMBERS, .ranges={{0}}},

        // numbers
        {.type=1, .flags = SCHEME_NUMBERS, .ranges={
                {U'0', U'9'},
        }},

        // english
        {.type=2, .flags = 0, .ranges={
                {U'a', U'z'},
        }},
        {.type=3, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'.'},
        }},

        {.type=4, .flags = SCHEME_ENGLISH, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
        }},

        {.type=5, .flags = SCHEME_ENGLISH | SCHEME_NUMBERS, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
        }},

        {.type=6, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'.'},
        }},

        {.type=7, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
                {U'.'},
        }},

        {.type=8, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
                {U'.'},
                {U'!'},
                {U' '},
        }},
        {.type=9, .flags = SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS | SCHEME_ENGLISH, .ranges={
                {U'!', U'~'},
        }},

        // full latin list
        {.type=10, .flags = SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS | SCHEME_ENGLISH | SCHEME_SPACE_SYMBOL, .ranges={
                {U' ', U'~'},
        }},

        // rus
        {.type=11, .flags = 0, .ranges={
                {U'а', U'я'},
        }},
        {.type=12, .flags = 0, .ranges={
                {U'а', U'я'},
                {U'.'},
        }},
        {.type=13, .flags = 0, .ranges={
                {U'а', U'я'},
                {U'А', U'Я'},
        }},
        {.type=14, .flags = 0, .ranges={
                {U'а', U'я'},
                {U'А', U'Я'},
                {U'.'},
                {U'!'},
                {U' '},
        }},

        // full latin + cyrillic list
        {.type=15, .flags = 0, .ranges={
                {U' ', U'~'},
                {U'а', U'я'},
                {U'А', U'Я'},
        }},


        // full latin + cyrillic list
        {.type=16, .flags = 0, .ranges={
                {U' ', U'~'},
                {U'а', U'я'},
                {U'А', U'Я'},
        }},

        // unicode last symbol
        {.type=(uint32_t) 0xff00, .flags = 0, .ranges={
                {0x20, 0xff}, //
        }},
        // unicode last symbol
        {.type=(uint32_t) 0xff01, .flags = 0, .ranges={
                {0x20, 0xfff}, //
        }},
        // unicode last symbol
        {.type=(uint32_t) 0xff02, .flags = 0, .ranges={
                {0x20, 0x32ff}, //
        }}
};


// ------------ range  --------------------
[[nodiscard]] int thekey_v2::SymbolRange::len() const {
    return end - start + 1;
}

[[nodiscard]] int thekey_v2::SymbolRange::contains(const wide_char &sym) const {
    return sym >= start && sym <= end;
}

void thekey_v2::SymbolRange::all_symbols(void (*callback)(const wide_char &)) const {
    for (wide_char c = start; c <= end; c++) {
        callback(c);
    }
}


// ------------ encoding find_scheme --------------------
wide_char thekey_v2::EncodingScheme::encoded(wide_char original, int offset) const {
    while (offset < 0) offset += len();
    for (int i = 0; i < ranges.size(); ++i) {
        if (ranges[i].contains(original)) {
            return (original - ranges[i].start + offset) % len();
        } else {
            offset += ranges[i].len();
        }
    }
    return 0;
}

wide_char thekey_v2::EncodingScheme::decoded(wide_char encoded, int offset) const {
    offset += encoded;
    while (offset < 0) offset += len();
    while (true) {
        for (auto &range: ranges) {
            if (offset > range.len() - 1) {
                offset -= range.len();
            } else {
                return range.start + offset;
            }
        }
    }
}

uint thekey_v2::EncodingScheme::len() const {
    int len = 0;
    for (const auto &item: ranges) len += item.len();
    return len;
}

int thekey_v2::EncodingScheme::all_contains(const wide_string &wideString) const {
    for (const auto &c: wideString) {
        int found = 0;
        for (const auto &range: ranges) {
            found |= range.contains(c);
            if (found)break;
        }
        if (!found)return 0;
    }
    return 1;
}

void thekey_v2::EncodingScheme::all_symbols(void (*callback)(const wide_char &)) const {
    for (const auto &item: ranges) {
        item.all_symbols(callback);
    }
}


// ------------ public methods --------------------
const EncodingScheme *thekey_v2::find_scheme(uint32_t type) {
    auto it = std::find_if(encodingSchemas.begin(), encodingSchemas.end(),
                           [type](const EncodingScheme &schema) { return schema.type == type; });
    if (it != encodingSchemas.end()) {
        return &*it;
    }
    return &encodingSchemas.back();
}


uint32_t thekey_v2::find_scheme_type(const wide_string &str, const int &minLen) {
    auto it = std::find_if(encodingSchemas.begin(), encodingSchemas.end(),
                           [str, minLen](const EncodingScheme &schema) {
                               return schema.all_contains(str) && schema.len() > minLen;
                           });

    if (it != encodingSchemas.end()) {
        return it->type;
    }
    return encodingSchemas.back().type;
}

uint32_t thekey_v2::find_scheme_type_by_flags(const uint32_t &flags) {
    auto it = std::find_if(encodingSchemas.begin(), encodingSchemas.end(),
                           [flags](const EncodingScheme &schema) { return (schema.flags & flags) == flags; });
    if (it != encodingSchemas.end()) {
        return it->type;
    }
    return encodingSchemas.back().type;
}