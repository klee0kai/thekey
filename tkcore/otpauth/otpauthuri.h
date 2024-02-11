//
// Created by panda on 08.02.24.
//

#ifndef THEKEY_OTPAUTHURI_H
#define THEKEY_OTPAUTHURI_H

#include "key_core.h"

namespace key_otp {

    typedef enum OtpScheme {
        otpuri, authuri
    } OtpScheme;

    typedef enum OtpType {
        OTP, TOTP, HOTP
    } OtpMethod;

    typedef enum OtpAlgo {
        SHA1, SHA256, SHA512
    } OtpAlgo;

    struct OtpInfo {
        OtpScheme scheme;
        OtpMethod method;
        std::string issuer;
        std::string name; // email

        std::string secret; // base32 secret

        OtpAlgo algorithm;
        uint digits;
        uint interval;
        uint count;

        static OtpInfo fromUri(const std::string &uriString);

        std::string toUri();
    };


}


#endif //THEKEY_OTPAUTHURI_H
