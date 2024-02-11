#ifndef BASE64_BASE64_H
#define BASE64_BASE64_H

/**
 * @file base64.hpp
 *
 * This module declares the base64 functions.
 *
 * © 2018 by Richard Walters
 */

#include <stdint.h>
#include <string>
#include <vector>

namespace base64 {

    /**
     * This function encodes the given data using the base64 algorithm.
     *
     * @param[in] data
     *     This is the data to encode using the base64 algorithm.
     *
     * @return
     *     The base64 encoding of the given data is returned.
     */
    std::string encode(const std::vector<uint8_t> &data);

    /**
     * This function encodes the given data using the base64 algorithm.
     *
     * @param[in] data
     *     This is the data to encode using the base64 algorithm.
     *
     * @return
     *     The base64 encoding of the given data is returned.
     */
    std::string encode(const std::string &data);

    /**
     * This function decodes the given data using the base64 algorithm.
     *
     * @param[in] data
     *     This is the data to decodes using the base64 algorithm.
     *
     * @return
     *     The base64 decoding of the given data is returned.
     */
    std::string decode(const std::vector<uint8_t> &data);

    /**
     * This function decodes the given data using the base64 algorithm.
     *
     * @param[in] data
     *     This is the data to decodes using the base64 algorithm.
     *
     * @return
     *     The base64 decoding of the given data is returned.
     */
    std::string decode(const std::string &data);

    /**
     * This function encodes the given data using the Base64Url algorithm.
     *
     * @param[in] data
     *     This is the data to encode using the Base64Url algorithm.
     *
     * @return
     *     The Base64Url encoding of the given data is returned.
     */
    std::string urlEncode(const std::vector<uint8_t> &data);

    /**
     * This function encodes the given data using the Base64Url algorithm.
     *
     * @param[in] data
     *     This is the data to encode using the Base64Url algorithm.
     *
     * @return
     *     The Base64Url encoding of the given data is returned.
     */
    std::string urlEncode(const std::string &data);

    /**
     * This function decodes the given data using the Base64Url algorithm.
     *
     * @param[in] data
     *     This is the data to decodes using the Base64Url algorithm.
     *
     * @return
     *     The Base64Url decoding of the given data is returned.
     */
    std::string urlDecode(const std::vector<uint8_t> &data);

    /**
     * This function decodes the given data using the Base64Url algorithm.
     *
     * @param[in] data
     *     This is the data to decodes using the Base64Url algorithm.
     *
     * @return
     *     The Base64Url decoding of the given data is returned.
     */
    std::string urlDecode(const std::string &data);

}

#endif // BASE64_BASE64_H
