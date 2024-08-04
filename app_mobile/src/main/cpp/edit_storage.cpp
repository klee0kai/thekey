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

typedef EngineModelStorage JvmStorage;
typedef EngineEditstorageEditStorageEngine JvmEditStorageEngine;
typedef brooklyn::EngineModelDecryptedNote JvmDecryptedNote;
typedef brooklyn::EngineModelDecryptedOtpNote JvmDecryptedOtpNote;


std::shared_ptr<JvmStorage> JvmEditStorageEngine::findStorageInfo(const std::string &path) {
    auto storage = shared_ptr<JvmStorage>{

    };
    return storage;
}


int JvmEditStorageEngine::createStorage(const JvmStorage &storage) {
    auto error = thekey_v2::createStorage(thekey::Storage{
            .file = storage.path,
            .name = storage.name,
            .description = storage.description,
    });
    return error;
}

int JvmEditStorageEngine::editStorage(const JvmStorage &jStorage) {
    auto storage = thekey_v2::storage(jStorage.path, "");
    if (!storage) return -1;
    storage->readAll();
    auto info = storage->info();
    info.name = jStorage.name;
    info.description = jStorage.description;
    storage->setInfo(info);
    storage->save();
    return 0;
}

int JvmEditStorageEngine::move(const std::string &from, const std::string &to) {
    rename(from.c_str(), to.c_str());
    return 0;
}


void JvmEditStorageEngine::changePassw(
        const std::string &path,
        const std::string &currentPassw,
        const std::string &newPassw
) {
    auto storage = thekey_v2::storage(path, currentPassw);
    if (!storage) return;
    storage->readAll();
    storage->saveNewPassw(path, newPassw);
}