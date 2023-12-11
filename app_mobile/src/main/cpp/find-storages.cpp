#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "dll_interface/key_finder.h"

using namespace brooklyn;


std::shared_ptr<EngineFindStorageListener> findStorageListener = {};

void EngineFindStorageEngine::findStorages(const std::string &folder,
                                           const EngineFindStorageListener &listener) {
    ::findStorageListener = std::make_shared<EngineFindStorageListener>(listener);

    key_finder::findStorages(folder.c_str(), [](Storage storage) {
        findStorageListener->onStorageFound(ModelStorage{
                .path = storage.file,
                .name = storage.name,
                .description  = storage.description
        });
    });
}

