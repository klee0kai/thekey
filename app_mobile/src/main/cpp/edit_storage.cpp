//
// Created by panda on 28.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "dll_interface/key_finder.h"

using namespace brooklyn;
using namespace std;

std::shared_ptr<ModelStorage> EngineEditStorageEngine::findStorageInfo(const std::string &path) {
    auto storage = shared_ptr<ModelStorage>{

    };
    return storage;
}


int EngineEditStorageEngine::createStorage(const brooklyn::ModelStorage &storage) {
    auto error = key_manager_ctx::createStorage(Storage{
            .file = storage.path.c_str(),
            .name = storage.name.c_str(),
            .description = storage.description.c_str(),
    });

    return error;
}

int EngineEditStorageEngine::editStorage(const brooklyn::ModelStorage &storage) {

    return JNI_FALSE;
}

