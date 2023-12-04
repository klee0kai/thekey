#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "dll_interface/key_finder.h"

using namespace brooklyn;

class FindStorageLamda {
public:
    explicit FindStorageLamda(const EngineFindStorageListener &listener) :
            findStorageListener(listener) {}

    void operator()(Storage storage) {
        __android_log_print(ANDROID_LOG_DEBUG, "STORAGESEARCH", "search storage found %s\n",
                            storage.file);


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
    __android_log_print(ANDROID_LOG_DEBUG, "STORAGESEARCH", "search storage started %s\n",
                        folder.c_str());
    auto lambda = FindStorageLamda(listener);
    key_finder::findStorages(folder.c_str(), (void (*)(Storage)) &lambda);
    __android_log_print(ANDROID_LOG_DEBUG, "STORAGESEARCH", "search storage finished %s\n",
                        folder.c_str());
}

