#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "key_find.h"
#include "key1.h"

using namespace brooklyn;
using namespace thekey;
using namespace thekey_v1;


std::shared_ptr<EngineFindstorageFindStorageListener> findStorageListener = {};

void EngineFindstorageFindStorageEngine::findStorages(const std::string &folder,
                                           const EngineFindstorageFindStorageListener &listener) {
    ::findStorageListener = std::make_shared<EngineFindstorageFindStorageListener>(listener);

    thekey::findStorages(folder, [](const Storage &item) {
        findStorageListener->onStorageFound(ModelStorage{
                .path = item.file,
                .name = item.name,
                .description = item.description
        });
    });
}

