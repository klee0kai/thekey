//
// Created by panda on 10.02.24.
//

#ifndef THEKEY_OTP_H
#define THEKEY_OTP_H

#include "otpauthuri.h"

namespace key_otp {

    std::string generateByCounterRaw(
            const std::vector<uint8_t> &secret,
            const OtpAlgo &algorithm,
            const uint64_t &counter,
            const uint &digits
    );

    std::string generateByCounter(const OtpInfo &otp, uint64_t counter);

    std::string generate(OtpInfo &otp, time_t now = time(NULL));

    std::string generateRaw(
            const std::vector<uint8_t> &secret,
            const OtpMethod &method,
            const OtpAlgo &algorithm,
            const uint64_t &counter,
            const uint64_t &interval,
            const uint &digits,
            const time_t &now = time(NULL)
    );

}

#endif //THEKEY_OTP_H
