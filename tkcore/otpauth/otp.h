//
// Created by panda on 10.02.24.
//

#ifndef THEKEY_OTP_H
#define THEKEY_OTP_H

#include "otpauthuri.h"

namespace key_otp {

    std::string generate(OtpInfo &otp, time_t now = time(NULL));

    std::string generateByCounter(const OtpInfo &otp, uint64_t counter);

}

#endif //THEKEY_OTP_H
