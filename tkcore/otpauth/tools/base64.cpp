
#include "base64.h"
#include <map>
#include <sstream>
#include <vector>

const char EncodingTable[65] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

const std::map<char, uint32_t> DecodingTable{
        {'A', 0},
        {'B', 1},
        {'C', 2},
        {'D', 3},
        {'E', 4},
        {'F', 5},
        {'G', 6},
        {'H', 7},
        {'I', 8},
        {'J', 9},
        {'K', 10},
        {'L', 11},
        {'M', 12},
        {'N', 13},
        {'O', 14},
        {'P', 15},
        {'Q', 16},
        {'R', 17},
        {'S', 18},
        {'T', 19},
        {'U', 20},
        {'V', 21},
        {'W', 22},
        {'X', 23},
        {'Y', 24},
        {'Z', 25},
        {'a', 26},
        {'b', 27},
        {'c', 28},
        {'d', 29},
        {'e', 30},
        {'f', 31},
        {'g', 32},
        {'h', 33},
        {'i', 34},
        {'j', 35},
        {'k', 36},
        {'l', 37},
        {'m', 38},
        {'n', 39},
        {'o', 40},
        {'p', 41},
        {'q', 42},
        {'r', 43},
        {'s', 44},
        {'t', 45},
        {'u', 46},
        {'v', 47},
        {'w', 48},
        {'x', 49},
        {'y', 50},
        {'z', 51},
        {'0', 52},
        {'1', 53},
        {'2', 54},
        {'3', 55},
        {'4', 56},
        {'5', 57},
        {'6', 58},
        {'7', 59},
        {'8', 60},
        {'9', 61},
        {'+', 62},
        {'/', 63},
};

const char UrlEncodingTable[65] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

const std::map<char, uint32_t> UrlDecodingTable{
        {'A', 0},
        {'B', 1},
        {'C', 2},
        {'D', 3},
        {'E', 4},
        {'F', 5},
        {'G', 6},
        {'H', 7},
        {'I', 8},
        {'J', 9},
        {'K', 10},
        {'L', 11},
        {'M', 12},
        {'N', 13},
        {'O', 14},
        {'P', 15},
        {'Q', 16},
        {'R', 17},
        {'S', 18},
        {'T', 19},
        {'U', 20},
        {'V', 21},
        {'W', 22},
        {'X', 23},
        {'Y', 24},
        {'Z', 25},
        {'a', 26},
        {'b', 27},
        {'c', 28},
        {'d', 29},
        {'e', 30},
        {'f', 31},
        {'g', 32},
        {'h', 33},
        {'i', 34},
        {'j', 35},
        {'k', 36},
        {'l', 37},
        {'m', 38},
        {'n', 39},
        {'o', 40},
        {'p', 41},
        {'q', 42},
        {'r', 43},
        {'s', 44},
        {'t', 45},
        {'u', 46},
        {'v', 47},
        {'w', 48},
        {'x', 49},
        {'y', 50},
        {'z', 51},
        {'0', 52},
        {'1', 53},
        {'2', 54},
        {'3', 55},
        {'4', 56},
        {'5', 57},
        {'6', 58},
        {'7', 59},
        {'8', 60},
        {'9', 61},
        {'-', 62},
        {'_', 63},
};


static std::string encode(
        const std::vector<uint8_t> &data,
        const char encodingTable[65],
        bool includePadding
) {
    std::ostringstream output;
    size_t bits = 0;
    uint32_t buffer = 0;
    for (auto datum: data) {
        buffer <<= 8;
        buffer += (uint32_t) datum;
        bits += 8;
        while (bits >= 6) {
            output << encodingTable[(buffer >> (bits - 6)) & 0x3f];
            buffer &= ~(0x3f << (bits - 6));
            bits -= 6;
        }
    }
    if ((data.size() % 3) == 1) {
        buffer <<= 4;
        output << encodingTable[buffer & 0x3f];
        if (includePadding) {
            output << "==";
        }
    } else if ((data.size() % 3) == 2) {
        buffer <<= 2;
        output << encodingTable[buffer & 0x3f];
        if (includePadding) {
            output << '=';
        }
    }
    return output.str();
}

static std::string decode(
        const std::vector<uint8_t> &data,
        const std::map<char, uint32_t> &decodingTable
) {
    std::ostringstream output;
    uint32_t buffer = 0;
    size_t bits = 0;
    for (auto datum: data) {
        const auto entry = decodingTable.find(datum);
        uint32_t group = 0;
        if (entry != decodingTable.end()) {
            group = entry->second;
        }
        buffer <<= 6;
        bits += 6;
        buffer += group;
        if (bits >= 8) {
            if (datum != '=') {
                output << (char) (buffer >> (bits - 8));
            }
            buffer &= ~(0xff << (bits - 8));
            bits -= 8;
        }
    }
    return output.str();
}


std::string base64::encode(const std::vector<uint8_t> &data) {
    return ::encode(data, EncodingTable, true);
}

std::string base64::encode(const std::string &data) {
    return encode(
            std::vector<uint8_t>(
                    data.begin(),
                    data.end()
            )
    );
}

std::string base64::decode(const std::vector<uint8_t> &data) {
    return ::decode(data, DecodingTable);
}

std::string base64::decode(const std::string &data) {
    return decode(
            std::vector<uint8_t>(
                    data.begin(),
                    data.end()
            )
    );
}

std::string base64::urlEncode(const std::vector<uint8_t> &data) {
    return ::encode(data, UrlEncodingTable, false);
}

std::string base64::urlEncode(const std::string &data) {
    return urlEncode(
            std::vector<uint8_t>(
                    data.begin(),
                    data.end()
            )
    );
}

std::string base64::urlDecode(const std::vector<uint8_t> &data) {
    return ::decode(data, UrlDecodingTable);
}

std::string base64::urlDecode(const std::string &data) {
    return urlDecode(
            std::vector<uint8_t>(
                    data.begin(),
                    data.end()
            )
    );
}

