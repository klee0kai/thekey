//
// Created by panda on 19.01.24.
//

#include "salt_test2.h"
#include "thekey_core.h"
#include <cstdarg>
#include <cstring>

using namespace std;
using namespace tkey2_salt;
using namespace tkey_salt;

typedef uint32_t wide_char;
typedef basic_string<wide_char> wide_string;
static wstring_convert<std::codecvt_utf8<wide_char>, wide_char> converter;

struct SymbolRange {
    wide_char start;
    wide_char end = start;

    [[nodiscard]] int len() const {
        return end - start + 1;
    }

    [[nodiscard]] int contains(const wchar_t &sym) const {
        return sym >= start && sym <= end;
    }

    [[nodiscard]] wide_string allSymbolsWide() const {
        wide_string wString = {};
        wString.reserve(len());
        for (wchar_t c = start; c <= end; c++) {
            wString += c;

        }
        return wString;
    }

    [[nodiscard]] std::string allSymbols() const {
        return converter.to_bytes(allSymbolsWide());
    }

};

struct EncodingSchema {
    uint32_t flags;
    std::vector<SymbolRange> ranges;

    wchar_t encoded(wide_char original) {
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

    wchar_t decoded(wide_char encoded) {
        while (encoded > 0) {
            for (auto &range: ranges) {
                if (encoded > range.len()) {
                    encoded -= range.len();
                } else {
                    return range.start + encoded;
                }
            }
        }
        return 0;
    }

    int len() const {
        int len = 0;
        for (const auto &item: ranges) len += item.len();
        return len;
    }

    int all_contains(const wide_string &wString) const {
        for (const auto &c: wString) {
            int found = 0;
            for (const auto &range: ranges) {
                found |= range.contains(c);
                if (found)break;
            }
            if (!found)return 0;
        }
        return 1;
    }

    int all_contains(const std::string &str) const {
        wide_string wString = converter.from_bytes(str);
        return all_contains(wString);
    }

    [[nodiscard]] wide_string allSymbolsWide() const {
        wide_string wString = {};
        wString.reserve(len());
        for (const auto &item: ranges) {
            wString += item.allSymbolsWide();
        }
        return wString;
    }

    std::string allSymbols() const {
        return converter.to_bytes(allSymbolsWide());
    }

};

static vector<EncodingSchema> encodingSchemas = {
        // english
        {.flags = 0, .ranges={
                {U'a', U'z'},
        }},
        {.flags = 0, .ranges={
                {U'a', U'z'},
                {U'.'},
        }},

        {.flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
        }},

        {.flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
        }},

        {.flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'.'},
        }},

        {.flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
                {U'.'},
        }},

        {.flags = 0, .ranges={
                {U'a', U'z'},
                {U'A', U'Z'},
                {U'0', U'9'},
                {U'.'},
                {U'!'},
                {U' '},
        }},
        // full latin list
        {.flags = 0, .ranges={
                {U' ', U'~'},
        }},

        // rus
        {.flags = 0, .ranges={
                {U'а', U'я'},
        }},
        {.flags = 0, .ranges={
                {U'а', U'я'},
                {U'.'},
        }},
        {.flags = 0, .ranges={
                {U'а', U'я'},
                {U'А', U'Я'},
        }},
        {.flags = 0, .ranges={
                {U'а', U'я'},
                {U'А', U'Я'},
                {U'.'},
                {U'!'},
                {U' '},
        }},

        // full latin + cyrillic list
        {.flags = 0, .ranges={
                {U' ', U'~'},
                {U'а', U'я'},
                {U'А', U'Я'},
        }},
};


void tkey2_salt::SaltedText::salted(const std::string &text) {
    auto type = findEncodingType(text);
    randmem(payload.raw, SALTED_TEXT_LEN);

    memsalt(payload.raw, SALTED_TEXT_LEN, encodingLen(type));


}

std::string tkey2_salt::SaltedText::desalted() const {


}

uint32_t tkey2_salt::findEncodingType(const std::string &str) {
    for (int type = 0; type < encodingSchemas.size(); ++type) {
        if (encodingSchemas[type].all_contains(str))
            return type;
    }
    return -1;
}

uint32_t tkey2_salt::findEncodingTypeByFlags(const uint32_t &flags) {
    for (int type = 0; type < encodingSchemas.size(); ++type) {
        if ((encodingSchemas[type].flags & flags) == flags)
            return type;
    }
    return -1;
}

uint32_t tkey2_salt::encodingLen(uint32_t type) {
    if (type == -1)return 0;
    return encodingSchemas[type].len();
}

std::string tkey2_salt::encodingSymbols(uint32_t type) {
    if (type == -1)return "";
    return encodingSchemas[type].allSymbols();
}

int tkey2_salt::encoded(
        uint32_t typeEncoding,
        unsigned char *out_chars, const unsigned char *in_chars,
        const uint &bufSize, const int &salt) {
    if (typeEncoding == -1) {
        strncpy((char *) out_chars, (char *) in_chars, bufSize);
        return int(bufSize);
    }

    randmem(out_chars, bufSize);
    auto scheme = encodingSchemas[DESALT_IN_RING(typeEncoding, encodingSchemas.size())];

    auto *out_wide = (wide_char *) out_chars;
    auto *in_wide = (wide_char *) in_chars;

    for (int i = 0; i < bufSize && in[i]; i++) {
        out[i] = scheme.encoded();
    }


}


int tkey2_salt::decoded(uint32_t typeEncoding, unsigned char *out, const unsigned char *in, const int &len) {

}