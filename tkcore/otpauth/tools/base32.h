#pragma once

/**
 * @file base32.hpp
 *
 * This module declares the base32 functions.
 *
 * © 2019 by Richard Walters
 */

#include <string>
#include <vector>

namespace base32 {

    /**
     * This function encodes the given data using the base32 algorithm.
     *
     * @param[in] data
     *     This is the data to encode using the base32 algorithm.
     *
     * @param[in] omitPadding
     *     If true, do not include padding characters at the end
     *     of the encoding.
     *
     * @return
     *     The base32 encoding of the given data is returned.
     */
    std::string encode(
        const std::vector< uint8_t >& data,
        bool omitPadding = false
    );

    /**
     * This function encodes the given data using the base32 algorithm.
     *
     * @param[in] data
     *     This is the data to encode using the base32 algorithm.
     *
     * @param[in] omitPadding
     *     If true, do not include padding characters at the end
     *     of the encoding.
     *
     * @return
     *     The base32 encoding of the given data is returned.
     */
    std::string encode(
        const std::string& data,
        bool omitPadding = false
    );

    /**
     * This function decodes the given data using the base32 algorithm.
     *
     * @param[in] data
     *     This is the data to decodes using the base32 algorithm.
     *
     * @return
     *     The base32 decoding of the given data is returned.
     */
    std::string decode(const std::vector< uint8_t >& data);

    /**
     * This function decodes the given data using the base32 algorithm.
     *
     * @param[in] data
     *     This is the data to decodes using the base32 algorithm.
     *
     * @return
     *     The base32 decoding of the given data is returned.
     */
    std::string decode(const std::string& data);

}
