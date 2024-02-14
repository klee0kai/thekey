#pragma once

#include <string>
#include <vector>

namespace base32 {

    std::string encode(const std::vector<uint8_t> &data, bool omitPadding = false);

    std::string encode(const std::string &data, bool omitPadding = false);

    std::string decode(const std::vector<uint8_t> &data);

    std::string decode(const std::string &data);

}
