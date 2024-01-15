#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "thekey.h"

using namespace brooklyn;
using namespace thekey;
using namespace thekey_v1;


std::shared_ptr<EngineFindStorageListener> findStorageListener = {};

void EngineFindStorageEngine::findStorages(const std::string &folder,
                                           const EngineFindStorageListener &listener) {
    ::findStorageListener = std::make_shared<EngineFindStorageListener>(listener);

    auto storages = thekey::findStorages(folder);
    for (const auto &item: storages) {
        findStorageListener->onStorageFound(ModelStorage{
                .path = item.file,
                .name = item.name,
                .description = item.description
        });
    }

}

