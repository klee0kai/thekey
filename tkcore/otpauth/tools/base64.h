#ifndef BASE64_BASE64_H
#define BASE64_BASE64_H

#include <stdint.h>
#include <string>
#include <vector>

namespace base64 {

    std::string encode(const std::vector<uint8_t> &data);

    std::string encode(const std::string &data);

    std::string decode(const std::vector<uint8_t> &data);

    std::string decode(const std::string &data);

    std::string urlEncode(const std::vector<uint8_t> &data);

    std::string urlEncode(const std::string &data);

    std::string urlDecode(const std::vector<uint8_t> &data);

    std::string urlDecode(const std::string &data);

}

#endif // BASE64_BASE64_H
