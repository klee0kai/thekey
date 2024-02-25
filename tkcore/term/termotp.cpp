//
// Created by panda on 11.02.24.
//

#include "termotp.h"
#include "utils/Interactive.h"
#include "utils/term_utils.h"
#include "otp.h"

using namespace term;
using namespace std;
using namespace thekey;
using namespace thekey_otp;
using namespace key_otp;

static int pointInterval = 5;

static void printOtp(const OtpInfo &otp);

void thekey_otp::interactive() {
    auto it = Interactive();
    cout << "Welcome OTP interactive. No Storage" << endl;
    cout << "Be careful we do not safe otp configs here" << endl;
    it.helpTitle = "OTP Interactive";

    std::list<OtpInfo> otpList;

    it.cmd({"l", "list"}, "list in-memory cached totp password", [&]() {
        int index = 1;
        for (const auto &otp: otpList) {
            cout << index++ << ") ";
            printOtp(otp);
        }
    });
    it.cmd({"add"}, "add otp note to in-memory cache.", [&]() {
        auto uri = ask_from_term("input uri (otpauth or otpauth-migration schemas): ");
        auto newOtpList = parseOtpUri(uri);
        otpList.insert(otpList.end(), newOtpList.begin(), newOtpList.end());
        cout << "added " << newOtpList.size() << " otp notes " << endl;
    });

    it.cmd({"gen"}, "generate new one-time password.", [&]() {
        int index = 1;
        for (const auto &otp: otpList) {
            cout << index++ << ") ";
            printOtp(otp);
        }

        auto otpIndex = ask_int_from_term("select otp. Write index: ");
        if (otpIndex < 1 || otpIndex > otpList.size()) {
            cerr << "incorrect index " << otpIndex << endl;
            return;
        }
        auto it = otpList.begin();
        advance(it, otpIndex - 1);
        auto otp = *it;

        cout << otpIndex << ") ";
        printOtp(otp);

        interactiveOtpCode(otp);
    });

    it.cmd({"export"}, "Export otp config.", [&]() {
        cout << "0) Export all otp configs " << endl;

        int index = 1;
        for (const auto &otp: otpList) {
            cout << index++ << ") ";
            printOtp(otp);
        }

        auto otpIndex = ask_int_from_term("select otp. Write index: ");
        if (otpIndex == 0) {
            index = 1;
            for (const auto &otp: otpList) {
                cout << index++ << ") ";
                printOtp(otp);
                cout << otp.toUri() << endl << endl;
            }
            return;
        }
        if (otpIndex < 1 || otpIndex > otpList.size()) {
            cerr << "incorrect index " << otpIndex << endl;
            return;
        }
        auto it = otpList.begin();
        advance(it, otpIndex - 1);
        auto otp = *it;

        cout << otpIndex << ") ";
        printOtp(otp);
        cout << otp.toUri() << endl;
    });


    it.loop();
    cout << string("bye from OTP interactive. Cache cleaned") << endl;
}


void thekey_otp::interactiveOtpCode(key_otp::OtpInfo &otp) {
    if (!otp.interval) {
        cerr << "error: interval is 0" << endl;
    }
    switch (otp.method) {
        case HOTP:
        case OTP:
            cout << "one-time passw: " << generate(otp) << endl;
            break;
        case TOTP: {
            cout << "click ENTER to exit from TOTP mode" << endl;
            checkInput();
            int active = 1;
            int lastInterval = 0;
            int lastPointInterval = 0;
            while (active) {
                auto now = time(NULL);
                if (now / otp.interval != lastInterval) {
                    cout << endl;
                    cout << "one-time passw: " << generate(otp, now) << "  " << flush;
                    lastInterval = now / otp.interval;
                    lastPointInterval = now / pointInterval;
                    auto pointCount = (now % otp.interval) / pointInterval;
                    for (int i = 0; i < pointCount; ++i) cout << "." << flush;
                }
                if (now / pointInterval != lastPointInterval) {
                    cout << "." << flush;
                    lastPointInterval = now / pointInterval;
                }
                if (checkInput()) {
                    active = 0;
                } else {
                    usleep(100);
                }
            }
            cout << endl;
        }

    }
}


static void printOtp(const OtpInfo &otp) {
    if (otp.issuer.empty()) {
        cout << otp.name << endl;
    } else {
        cout << otp.issuer << " ( " << otp.name << " )" << endl;
    }
}