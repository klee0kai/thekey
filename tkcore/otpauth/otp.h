//
// Created by panda on 10.02.24.
//

#ifndef THEKEY_OTP_H
#define THEKEY_OTP_H

#include "otpauthuri.h"

namespace key_otp {

    std::string generateOtpRaw(
            const std::vector<uint8_t> &secret,
            const OtpAlgo &algorithm,
            const uint64_t &counter,
            const uint &digits
    );

    std::string generateYaOtpRaw(
            const std::vector<uint8_t> &secret,
            const std::string &pin,
            const OtpAlgo &algorithm,
            const uint64_t &counter,
            const uint &digits
    );

    std::string generateOtpRaw(const OtpInfo &otp, uint64_t counter);

    std::string generate(
            OtpInfo &otp,
            const time_t &now = time(NULL)
    );

}

#endif //THEKEY_OTP_H
