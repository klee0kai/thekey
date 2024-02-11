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


OtpInfo OtpInfo::fromUri(const std::string &uriString) {
    OtpInfo info{};
    struct uri u(uriString);
    if (u.scheme == "otpuri") {
        info.scheme = otpuri;
    } else if (u.scheme == "otpauth") {
        info.scheme = authuri;
    }

    if (u.type == "totp") {
        info.method = TOTP;
    } else if (u.type == "hotp") {
        info.method = HOTP;
    } else if (u.type == "otp") {
        info.method = OTP;
    }

    info.issuer = u.issuer.empty() ? u.query["issuer"] : u.issuer;
    info.name = u.accountName + "@" + u.host;
    info.secret = u.query["secret"];

    auto algo = u.query["algorithm"];
    transform(algo.begin(), algo.end(), algo.begin(), [](unsigned char c) { return tolower(c); });
    if (algo == "sha1") {
        info.algorithm = SHA1;
    } else if (algo == "sha256") {
        info.algorithm = SHA256;
    } else if (algo == "sha512") {
        info.algorithm = SHA512;
    }

    auto digits = u.query["digits"];
    info.digits = !digits.empty() ? std::strtol(digits.c_str(), NULL, 10) : 4;

    switch (info.method) {
        case OTP:
            break;
        case TOTP: {
            auto period = u.query["period"];
            info.interval = !period.empty() ? std::strtol(period.c_str(), NULL, 10) : 30;
            break;
        }
        case HOTP: {
            auto counter = u.query["count"];
            if (counter.empty()) counter = u.query["counter"];
            info.count = std::strtol(counter.c_str(), NULL, 10);
            break;
        }
    }

    return info;
}