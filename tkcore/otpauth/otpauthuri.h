//
// Created by panda on 08.02.24.
//

#ifndef THEKEY_OTPAUTHURI_H
#define THEKEY_OTPAUTHURI_H

#include "key_core.h"
#include "tools/uri.h"

#define GOOGLE_AUTH_MIGRATION_SCHEME "otpauth-migration"
#define OTP_URI_SCHEME "otpuri"
#define GOOGLE_AUTH_SCHEME "otpauth"

#define TOTP_DEFAULT_INTERVAL 30
#define TOTP_DEFAULT_DIGITS 6

namespace key_otp {

    typedef enum OtpScheme {
        otpuri, authuri
    } OtpScheme;

    typedef enum OtpType {
        OTP, HOTP, TOTP, YAOTP,
    } OtpMethod;

    typedef enum OtpAlgo {
        SHA1, SHA256, SHA512
    } OtpAlgo;

    struct OtpInfo {
        OtpScheme scheme;
        OtpMethod method;
        OtpAlgo algorithm;
        std::string issuer;
        std::string name; // email

        std::vector<uint8_t> secret;

        uint digits;
        uint64_t interval;
        uint64_t counter;

        std::string toUri() const;
    };

    std::list<OtpInfo> parseOtpUri(
            const std::string &uriString,
            const std::function<Result<std::string>(const OtpInfo &)> &otpPin = {}
    );

    int isGoogleAuthMigrationSupport();

    std::list<OtpInfo> fromGoogleAuthMigration(const uri &uri);

}


#endif //THEKEY_OTPAUTHURI_H
