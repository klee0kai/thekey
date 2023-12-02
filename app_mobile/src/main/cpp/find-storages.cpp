#include <jni.h>
#include <string>
#include "brooklyn.h"
#include "dll_interface/key_finder.h"

using namespace brooklyn;

class FindStorageLamda {
public:
    explicit FindStorageLamda(const EngineFindStorageListener &listener) :
            findStorageListener(listener) {}

    void operator()(Storage storage) {
        findStorageListener.onStorageFound(ModelStorage{
                .path = storage.file,
                .name = storage.name,
                .description  = storage.description
        });
    }

private:
    EngineFindStorageListener findStorageListener;
};

void brooklyn::EngineFindStorageEngine::findStorages(const std::string &folder,
                                                     const EngineFindStorageListener &listener) {

    auto lambda = FindStorageLamda(listener);
    key_finder::findStorages(folder.c_str(), (void (*)(Storage)) &lambda);
}

