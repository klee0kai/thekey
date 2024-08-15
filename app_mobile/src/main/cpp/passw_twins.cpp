#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "key_find.h"
#include "key2.h"
#include "split_password.h"

using namespace brooklyn;
using namespace thekey;
using namespace thekey_v2;
using namespace std;

typedef brooklyn::EngineTwinsPasswordTwinsEngine JvmTwinsEngine;
typedef brooklyn::EngineModelTwinsCollection JvmTwinsCollection;


std::shared_ptr<JvmTwinsCollection> JvmTwinsEngine::findTwins(
        const std::string &passw
) {
    const auto &twins = thekey_v2::twins(passw);
    auto jTwins = make_shared<JvmTwinsCollection>(JvmTwinsCollection{});
    jTwins->otpTwins.insert(jTwins->otpTwins.end(),
                            twins.passwForOtpTwins.begin(),
                            twins.passwForOtpTwins.end());
    jTwins->loginTwins.insert(jTwins->loginTwins.end(),
                              twins.passwForLoginTwins.begin(),
                              twins.passwForLoginTwins.end());
    jTwins->histTwins.insert(jTwins->histTwins.end(),
                             twins.passwForHistPasswTwins.begin(),
                             twins.passwForHistPasswTwins.end());
    jTwins->descTwins.insert(jTwins->descTwins.end(),
                             twins.passwForDescriptionTwins.begin(),
                             twins.passwForDescriptionTwins.end());

    return jTwins;
}



