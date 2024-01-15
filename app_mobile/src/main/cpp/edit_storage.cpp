//
// Created by panda on 28.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "thekey.h"

using namespace thekey;
using namespace brooklyn;
using namespace std;

std::shared_ptr<ModelStorage> EngineEditStorageEngine::findStorageInfo(const std::string &path) {
    auto storage = shared_ptr<ModelStorage>{

    };
    return storage;
}


int EngineEditStorageEngine::createStorage(const brooklyn::ModelStorage &storage) {
    auto error = thekey_v1::createStorage(thekey::Storage{
            .file = storage.path,
            .name = storage.name,
            .description = storage.description,
    });
    return error;
}

int EngineEditStorageEngine::editStorage(const brooklyn::ModelStorage &storage) {

    return JNI_FALSE;
}

