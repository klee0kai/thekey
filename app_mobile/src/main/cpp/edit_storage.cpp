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
typedef brooklyn::EngineModelChPasswStrategy JvmChStrategy;


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


void JvmEditStorageEngine::changePasswStrategy(
        const std::string &path,
        const std::vector<JvmChStrategy> &jStrategies) {
    auto defaultStrategy = std::find_if(jStrategies.begin(), jStrategies.end(),
                                        [](const JvmChStrategy &it) { return it.defaultStrategy; });
    if (defaultStrategy == jStrategies.end()) return;
    auto storage = thekey_v2::storage(path, defaultStrategy->currentPasswd);
    if (!storage) return;
    storage->readAll();

    list<thekey_v2::StoragePasswMigrateStrategy> strategies = {};


    for (const auto &jStrategy: jStrategies) {
        strategies.push_back(thekey_v2::StoragePasswMigrateStrategy{
                .currentPassword = jStrategy.currentPasswd,
                .newPassw = jStrategy.newPassw,
                .isDefault = jStrategy.defaultStrategy,
        });
        auto &strategy = strategies.back();
        strategy.noteIds.insert(strategy.noteIds.end(),
                                jStrategy.noteIds.begin(),
                                jStrategy.noteIds.end());
        strategy.otpNoteIds.insert(strategy.otpNoteIds.end(),
                                jStrategy.otpNoteIds.begin(),
                                jStrategy.otpNoteIds.end());
    }
    storage->saveNewPasswStrategy(path, strategies);
}
