//
// Created by panda on 11.02.24.
//

#include "otpauthuri.h"
#include "tools/uri.h"
#include "tools/base64.h"
#include "tools/base32.h"
#include <regex>

using namespace std;
using namespace key_otp;

#if Protobuf_FOUND
#include "OtpMigration.pb.h"
#endif

int key_otp::isGoogleAuthMigrationSupport() {
#if Protobuf_FOUND
    return 1;
#else
    return 0;
#endif
}

std::list<OtpInfo> key_otp::fromGoogleAuthMigration(const uri &uri) {
#if Protobuf_FOUND
    if (uri.scheme != GOOGLE_AUTH_MIGRATION_SCHEME || uri.host != "offline") {
        return {};
    }
    auto dataIt = uri.query.find("data");
    if (dataIt == uri.query.end())return {};
    string base64Data = dataIt->second;
    string data = base64::decode(base64Data);
    MigrationPayload payload;
    payload.ParseFromString(data);

    list<OtpInfo> otpList;
    for (const auto &otpParam: payload.otp_parameters()) {
        const auto &secret = otpParam.secret();
        OtpInfo info = {
                .scheme = authuri,
                .method = OtpMethod::TOTP,
                .algorithm = OtpAlgo::SHA1,
                .issuer = otpParam.issuer(),
                .name = otpParam.name(),
                .secret = vector<uint8_t>(secret.begin(), secret.end()),
                .digits = DEFAULT_DIGITS,
                .interval = DEFAULT_INTERVAL,
                .counter = uint64_t(otpParam.counter()),
        };

        auto sepIt = find(info.name.begin(), info.name.end(), ':');
        if (info.name.end() > sepIt + 1) {
            info.name.assign(sepIt + 1, info.name.end());
        }

        switch (otpParam.type()) {
            case MigrationPayload_OtpType_OTP_TYPE_HOTP:
                info.method = HOTP;
                break;
            case MigrationPayload_OtpType_OTP_TYPE_TOTP:
                info.method = TOTP;
                break;
        }

        switch (otpParam.algorithm()) {
            case MigrationPayload_Algorithm_ALGORITHM_SHA1:
                info.algorithm = SHA1;
                break;
            case MigrationPayload_Algorithm_ALGORITHM_SHA256:
                info.algorithm = SHA256;
                break;
            case MigrationPayload_Algorithm_ALGORITHM_SHA512:
                info.algorithm = SHA512;
                break;
        }

        switch (otpParam.digits()) {
            case MigrationPayload_DigitCount_DIGIT_COUNT_EIGHT:
                info.digits = 8;
                break;
            case MigrationPayload_DigitCount_DIGIT_COUNT_SIX:
                info.digits = 6;
                break;
        }

        otpList.push_back(info);
    }

    return otpList;
#else
    return {};
#endif
}

