//
// Created by panda on 08.02.24.
//

#include "otpauthuri.h"
#include "tools/uri.h"


using namespace std;
using namespace key_otp;


std::string OtpInfo::toUri() {
    stringstream builder;
    switch (scheme) {
        case otpuri:
            builder << "otpuri://";
            break;
        case authuri:
            builder << "otpauth://";
            break;
        default:
            return "";
    }

    switch (method) {
        case TOTP:
            builder << "totp";
            break;
        case HOTP:
            builder << "hotp";
            break;
        case OTP:
            builder << "otp";
            break;
        default:
            return "";
    }

    builder << "/" << encodeURIComponent(issuer) << ":" << name;
    builder << "?secret=" << encodeURIComponent(secret);
    builder << "&issuer=" << encodeURIComponent(issuer);

    builder << "&algorithm=";
    switch (algorithm) {
        case SHA1:
            builder << "sha1";
            break;
        case SHA256:
            builder << "sha256";
            break;
        case SHA512:
            builder << "sha512";
            break;
        default:
            return "";
    }

    builder << "&digits=" << digits;

    switch (method) {
        case OTP:
            break;
        case TOTP:
            builder << "&period=" << interval;
            break;
        case HOTP:
            builder << "&count=" << count;
            break;
    }

    return builder.str();
}
