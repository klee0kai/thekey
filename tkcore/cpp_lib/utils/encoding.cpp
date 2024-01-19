//
// Created by panda on 19.01.24.
//

#include "encoding.h"
#include <cstdarg>

using namespace std;

static std::wstring_convert<std::codecvt_utf8<wchar_t>> converter;

struct SymbolRange {
    wchar_t start;
    wchar_t end = start;

    int len() const {
        return end - start + 1;
    }

    int contains(const wchar_t &sym) const {
        return sym >= start && sym <= end;
    }

    std::wstring allSymbolsWide() const {
        wstring wString = {};
        wString.reserve(len());
        for (wchar_t c = start; c <= end; c++) {
            wString += c;

        }
        return wString;
    }

    std::string allSymbols() const {
        return converter.to_bytes(allSymbolsWide());
    }

};

struct EncodingSchema {
    uint32_t flags;
    std::vector<SymbolRange> ranges;

    int len() const {
        int len = 0;
        for (const auto &item: ranges) len += item.len();
        return len;
    }

    int all_contains(const std::wstring &wString) const {
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
        std::wstring wString = converter.from_bytes(str);
        return all_contains(wString);
    }

    [[nodiscard]] std::wstring allSymbolsWide() const {
        wstring wString = {};
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


uint32_t tkey_encoding::findEncodingType(const std::string &str) {
    for (int type = 0; type < encodingSchemas.size(); ++type) {
        if (encodingSchemas[type].all_contains(str))
            return type;
    }
    return -1;
}

uint32_t tkey_encoding::findEncodingTypeByFlags(const uint32_t &flags) {
    for (int type = 0; type < encodingSchemas.size(); ++type) {
        if ((encodingSchemas[type].flags & flags) == flags)
            return type;
    }
    return -1;
}

uint32_t tkey_encoding::encodingLen(uint32_t type) {
    if (type == -1)return 0;
    return encodingSchemas[type].len();
}

std::string tkey_encoding::encodingSymbols(uint32_t type) {
    if (type == -1)return "";
    return encodingSchemas[type].allSymbols();
}