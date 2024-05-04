
#include "base64.h"
#include <sstream>
#include <vector>

using namespace std;

const string encodingTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
const string urlEncodingTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

static std::string encode(
        const std::vector<uint8_t> &data,
        const string &encodingTable,
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

static std::string decode(const std::vector<uint8_t> &data, const string &encodingTable) {
    std::ostringstream output;
    uint32_t buffer = 0;
    size_t bits = 0;
    for (auto datum: data) {
        uint32_t group = encodingTable.find(datum);
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
    return ::encode(data, encodingTable, true);
}

std::string base64::encode(const std::string &data) {
    return encode(std::vector<uint8_t>(data.begin(), data.end()));
}

std::string base64::decode(const std::vector<uint8_t> &data) {
    return ::decode(data, encodingTable);
}

std::string base64::decode(const std::string &data) {
    return decode(std::vector<uint8_t>(data.begin(), data.end()));
}

std::string base64::urlEncode(const std::vector<uint8_t> &data) {
    return ::encode(data, urlEncodingTable, false);
}

std::string base64::urlEncode(const std::string &data) {
    return urlEncode(std::vector<uint8_t>(data.begin(), data.end()));
}

std::string base64::urlDecode(const std::vector<uint8_t> &data) {
    return ::decode(data, urlEncodingTable);
}

std::string base64::urlDecode(const std::string &data) {
    return urlDecode(std::vector<uint8_t>(data.begin(), data.end()));
}

