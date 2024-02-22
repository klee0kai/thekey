//
// Created by panda on 11.02.24.
//

#ifndef THEKEY_TERMOTP_H
#define THEKEY_TERMOTP_H

#include "otp.h"

namespace thekey_otp {

    void interactive();

    void interactiveOtpCode(key_otp::OtpInfo &otpInfo);

}

#endif //THEKEY_TERMOTP_H
