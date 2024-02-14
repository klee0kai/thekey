#include "base32.h"
#include <sstream>
#include <vector>

using namespace std;

static string encodingTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

std::string base32::encode(
        const std::vector<uint8_t> &data,
        bool omitPadding
) {
    std::ostringstream output;
    size_t bits = 0;
    uint32_t buffer = 0;
    for (auto datum: data) {
        buffer <<= 8;
        buffer += (uint32_t) datum;
        bits += 8;
        while (bits >= 5) {
            output << encodingTable[(buffer >> (bits - 5)) & 0x3f];
            buffer &= ~(0x1f << (bits - 5));
            bits -= 5;
        }
    }
    if ((data.size() % 5) == 1) {
        buffer <<= 2;
        output << encodingTable[buffer & 0x1f];
        if (!omitPadding) {
            output << "======";
        }
    } else if ((data.size() % 5) == 2) {
        buffer <<= 4;
        output << encodingTable[buffer & 0x1f];
        if (!omitPadding) {
            output << "====";
        }
    } else if ((data.size() % 5) == 3) {
        buffer <<= 1;
        output << encodingTable[buffer & 0x1f];
        if (!omitPadding) {
            output << "===";
        }
    } else if ((data.size() % 5) == 4) {
        buffer <<= 3;
        output << encodingTable[buffer & 0x1f];
        if (!omitPadding) {
            output << '=';
        }
    }
    return output.str();
}

std::string base32::encode(
        const std::string &data,
        bool omitPadding
) {
    return encode(std::vector<uint8_t>(data.begin(), data.end()), omitPadding);
}

std::string base32::decode(const std::vector<uint8_t> &data) {
    std::ostringstream output;
    uint32_t buffer = 0;
    size_t bits = 0;
    for (auto datum: data) {
        const auto entry = encodingTable.find(datum);
        uint32_t group = 0;
        group = entry;
        buffer <<= 5;
        bits += 5;
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

std::string base32::decode(const std::string &data) {
    return decode(std::vector<uint8_t>(data.begin(), data.end()));
}

