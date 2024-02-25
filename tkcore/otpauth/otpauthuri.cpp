//
// Created by panda on 08.02.24.
//

#include "key_core.h"
#include "otpauthuri.h"
#include "tools/uri.h"
#include "tools/base32.h"

using namespace std;
using namespace key_otp;

std::string OtpInfo::toUri() const {
    stringstream builder;
    switch (scheme) {
        case otpuri:
            builder << OTP_URI_SCHEME << "://";
            break;
        case authuri:
            builder << GOOGLE_AUTH_SCHEME << "://";
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
        case YAOTP:
            builder << "yaotp";
            break;
        default:
            return "";
    }

    builder << "/" << encodeURIComponent(issuer) << ":" << name;
    builder << "?secret=" << encodeURIComponent(base32::encode(secret, true));
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
            builder << "&count=" << counter;
            break;
    }

    return builder.str();
}

/**
 *
 * @param uriString otpuri or otpauth scheme uri
 * @return parsed otp info
 *
 * @details
 * Based on
 * https://github.com/google/google-authenticator-android/blob/6f65e99fcbc9bbefdc3317c008345db595052a2b/java/com/google/android/apps/authenticator/AuthenticatorActivity.java#L952
 * https://github.com/tilkinsc/COTP/blob/2c15a20bf5a914f9fd1312b7305ffec0fa685ac8/otpuri.c#L84
 *
 */
shared_ptr<OtpInfo> parseSingleOtp(
        const std::string &uriString,
        const std::function<Result<std::string>(const OtpInfo &)> &otpPin
) {
    OtpInfo info{};
    struct uri u(uriString);

    info.scheme = otpuri;
    if (u.scheme == OTP_URI_SCHEME) {
        info.scheme = otpuri;
    } else if (u.scheme == GOOGLE_AUTH_SCHEME) {
        info.scheme = authuri;
    }

    if (u.type == "totp") {
        info.method = TOTP;
    } else if (u.type == "hotp") {
        info.method = HOTP;
    } else if (u.type == "otp") {
        info.method = OTP;
    } else if (u.type == "yaotp") {
        info.method = YAOTP;
    } else {
        return {};
    }

    info.issuer = u.issuer.empty() ? u.query["issuer"] : u.issuer;
    info.name = u.accountName + "@" + u.host;
    info.secret = base32::decodeRaw(u.query["secret"]);

    info.algorithm = SHA1;
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
    info.digits = !digits.empty() ? std::strtol(digits.c_str(), NULL, 10) : TOTP_DEFAULT_DIGITS;

    switch (info.method) {
        case OTP:
            break;
        case HOTP: {
            auto counter = u.query["count"];
            if (counter.empty()) counter = u.query["counter"];
            info.counter = std::strtol(counter.c_str(), NULL, 10);
            break;
        }
        case TOTP: {
            auto period = u.query["period"];
            info.interval = !period.empty() ? std::strtol(period.c_str(), NULL, 10) : TOTP_DEFAULT_INTERVAL;
            break;
        }
        case YAOTP:
            info.algorithm = SHA256;
            info.digits = YAOTP_DEFAULT_DIGITS;
            info.interval = TOTP_DEFAULT_INTERVAL;

            auto pinResult = otpPin(info);
            if (pinResult.error)return {};

            if (info.secret.size() > YAOTP_SECRET_LENGTH) info.secret.resize(YAOTP_SECRET_LENGTH);
            auto pin = to_vector(pinResult.result);

            auto pinWithHash = std::vector<uint8_t>();
            pinWithHash.insert(pinWithHash.end(), pin.begin(), pin.end());
            pinWithHash.insert(pinWithHash.end(), info.secret.begin(), info.secret.end());

            info.secret = sha256(pinWithHash);

            break;
    }

    return make_shared<OtpInfo>(info);
}

std::list<OtpInfo> key_otp::parseOtpUri(
        const std::string &uriString,
        const std::function<Result<std::string>(const OtpInfo &)> &otpPin
) {
    struct uri u(uriString);
    if (u.scheme == GOOGLE_AUTH_MIGRATION_SCHEME) {
        return fromGoogleAuthMigration(u);
    } else {
        auto otp = parseSingleOtp(uriString, otpPin);
        if (otp) return {*otp};
        return {};
    }
}