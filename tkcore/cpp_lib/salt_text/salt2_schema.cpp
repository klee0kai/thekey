//
// Created by panda on 20.01.24.
//

#include "salt2.h"
#include "thekey_core.h"
#include "salt2_schema.h"
#include <cstdarg>
#include <cstring>
#include <algorithm>

using namespace std;
using namespace tkey2_salt;
using namespace tkey_salt;

static vector<EncodingScheme> encodingSchemas = {
        // english
        {.type=1, .flags = 0, .ranges={
                {U'a', U'z'},
        }},
        {.type=2, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'.'},
        }},

        {.type=3, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
        }},

        {.type=4, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
        }},

        {.type=5, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'.'},
        }},

        {.type=6, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
                {U'.'},
        }},

        {.type=7, .flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
                {U'.'},
                {U'!'},
                {U' '},
        }},
        // full latin list
        {.type=8, .flags = 0, .ranges={
                {U' ', U'~'},
        }},

        // rus
        {.type=9, .flags = 0, .ranges={
                {U'а', U'я'},
        }},
        {.type=10, .flags = 0, .ranges={
                {U'а', U'я'},
                {U'.'},
        }},
        {.type=11, .flags = 0, .ranges={
                {U'а', U'я'},
                {U'А', U'Я'},
        }},
        {.type=12, .flags = 0, .ranges={
                {U'а', U'я'},
                {U'А', U'Я'},
                {U'.'},
                {U'!'},
                {U' '},
        }},

        // full latin + cyrillic list
        {.type=13, .flags = 0, .ranges={
                {U' ', U'~'},
                {U'а', U'я'},
                {U'А', U'Я'},
        }},


        // full latin + cyrillic list
        {.type=14, .flags = 0, .ranges={
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
        }},
};


// ------------ range  --------------------
[[nodiscard]] int tkey2_salt::SymbolRange::len() const {
    return end - start + 1;
}

[[nodiscard]] int tkey2_salt::SymbolRange::contains(const wide_char &sym) const {
    return sym >= start && sym <= end;
}

void tkey2_salt::SymbolRange::all_symbols(void (*callback)(const wide_char &)) const {
    for (wide_char c = start; c <= end; c++) {
        callback(c);
    }
}


// ------------ encoding find_scheme --------------------
wide_char tkey2_salt::EncodingScheme::encoded(wide_char original) const {
    int offset = 0;
    for (int i = 0; i < ranges.size(); ++i) {
        if (ranges[i].contains(original)) {
            return original - ranges[i].start + offset;
        } else {
            offset += ranges[i].len();
        }
    }
    return 0;
}

wide_char tkey2_salt::EncodingScheme::decoded(wide_char encoded) const {
    while (true) {
        for (auto &range: ranges) {
            if (encoded > range.len() - 1) {
                encoded -= range.len();
            } else {
                return range.start + encoded;
            }
        }
    }
}

uint tkey2_salt::EncodingScheme::len() const {
    int len = 0;
    for (const auto &item: ranges) len += item.len();
    return len;
}

int tkey2_salt::EncodingScheme::all_contains(const wide_string &wideString) const {
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

void tkey2_salt::EncodingScheme::all_symbols(void (*callback)(const wide_char &)) const {
    for (const auto &item: ranges) {
        item.all_symbols(callback);
    }
}


// ------------ public methods --------------------
const EncodingScheme *tkey2_salt::find_scheme(uint32_t type) {
    auto it = std::find_if(encodingSchemas.begin(), encodingSchemas.end(),
                           [type](const EncodingScheme &schema) { return schema.type == type; });
    if (it != encodingSchemas.end()) {
        return &*it;
    }
    return &encodingSchemas.back();
}


uint32_t tkey2_salt::find_scheme_type(const wide_string &str) {
    auto it = std::find_if(encodingSchemas.begin(), encodingSchemas.end(),
                           [str](const EncodingScheme &schema) { return schema.all_contains(str); });

    if (it != encodingSchemas.end()) {
        return it->type;
    }
    return encodingSchemas.back().type;
}

uint32_t tkey2_salt::find_scheme_type_by_flags(const uint32_t &flags) {
    auto it = std::find_if(encodingSchemas.begin(), encodingSchemas.end(),
                           [flags](const EncodingScheme &schema) { return (schema.flags & flags) == flags; });
    if (it != encodingSchemas.end()) {
        return it->type;
    }
    return encodingSchemas.back().type;
}