#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "key_find.h"
#include "key1.h"

using namespace brooklyn;
using namespace thekey;
using namespace thekey_v1;

typedef EngineFindstorageFindStorageListener JvmFindStorageListener;
typedef EngineFindstorageFindStorageEngine JvmFindStorageEngine;
typedef EngineModelStorage JvmStorageInfo;


std::shared_ptr<JvmFindStorageListener> findStorageListener = {};

void JvmFindStorageEngine::findStorages(const std::string &folder, const JvmFindStorageListener &listener) {
    ::findStorageListener = std::make_shared<JvmFindStorageListener>(listener);

    thekey::findStorages(folder, [](const Storage &item) {
        findStorageListener->onStorageFound(JvmStorageInfo{
                .path = item.file,
                .name = item.name,
                .description = item.description,
                .version = int(item.storageVersion)
        });
    });
}

int JvmFindStorageEngine::storageVersion(const std::string &path) {
    auto storage = thekey::storage(path);
    if (storage) {
        return storage->storageVersion;
    } else {
        return 0;
    }
}

std::shared_ptr<JvmStorageInfo> JvmFindStorageEngine::storageInfo(const std::string &path) {
    auto storage = thekey::storage(path);
    if (storage) {
        return std::make_shared<JvmStorageInfo>(JvmStorageInfo{
                .path = storage->file,
                .name = storage->name,
                .description = storage->description,
                .version = int(storage->storageVersion)
        });
    } else {
        return {};
    }
}

std::shared_ptr<JvmStorageInfo> JvmFindStorageEngine::storageInfoFromDescriptor(const int &fd) {
    auto storage = thekey::storage(fd);
    if (storage) {
        return std::make_shared<JvmStorageInfo>(JvmStorageInfo{
                .path = storage->file,
                .name = storage->name,
                .description = storage->description,
                .version = int(storage->storageVersion)
        });
    } else {
        return {};
    }
}