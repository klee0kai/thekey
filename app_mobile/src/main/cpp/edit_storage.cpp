//
// Created by panda on 28.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "key_find.h"
#include "key2.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v2;

typedef EngineModelStorage JvmStorage;
typedef EngineEditstorageEditStorageEngine JvmFindStorageListener;

std::shared_ptr<JvmStorage> JvmFindStorageListener::findStorageInfo(const std::string &path) {
    auto storage = shared_ptr<JvmStorage>{

    };
    return storage;
}


int JvmFindStorageListener::createStorage(const JvmStorage &storage) {
    auto error = thekey_v2::createStorage(thekey::Storage{
            .file = storage.path,
            .name = storage.name,
            .description = storage.description,
    });
    return error;
}

int JvmFindStorageListener::editStorage(const JvmStorage &storage) {

    return JNI_FALSE;
}

int JvmFindStorageListener::move(const std::string &from, const std::string &to) {
    rename(from.c_str(), to.c_str());
    return 0;
}
