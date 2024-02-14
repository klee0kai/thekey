//
// Created by panda on 06.06.2020.
//
#include "def_header.h"
#include "utils/Interactive.h"
#include "utils/term_utils.h"
#include "termk1.h"
#include "termk2.h"
#include "key2.h"
#include "key1.h"
#include "key_find.h"
#include "termotp.h"

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

using namespace term;
using namespace std;
using namespace thekey;

int main(int argc, char **argv) {
    cout << string("TheKey - ")
         << "cryp/encrypt your secure passwords storages\n"
         << "Designed by Andrey Kuzubov / klee0kai"
         << endl;

    auto it = Interactive();
    it.helpTitle = string("TheKey Main interactive mode ")
                   + "(app version " + TERM_VERSION + ") ";

    it.cmd({"f", "find"}, "find available storages in folder", []() {
        auto folder = ask_from_term("input path: ");
        cout << "Available crypted storages in folder: " << fs::absolute(folder) << endl;
        thekey::findStorages(folder, [](const Storage &storage) {
            cout << "-------------------------------------" << endl;
            cout << "storagePath: " << storage.file << endl;
            cout << "name: " << storage.name << endl;
            cout << "version: " << storage.storageVersion << endl;
            cout << "desc: " << storage.description << endl;
            cout << endl;
        });
        cout << "-------------------------------------" << endl;
    });

    it.cmd({"c", "create"}, "create new storage", []() {
        auto path = ask_from_term("input path: ");
        auto version = ask_int_from_term("input storage version: ");
        if (version > 2) {
            cerr << "version not support " << version << " last version support " << 2 << endl;
            return;
        }
        auto name = ask_from_term("input name: ");
        auto dest = ask_from_term("input dest: ");

        keyError = 0;
        switch (version) {
            case 1: {
                thekey_v1::createStorage(
                        {
                                .file = path,
                                .storageVersion = uint(version),
                                .name = name,
                                .description = dest
                        });
                break;
            }
            case 2: {
                thekey_v2::createStorage(
                        {
                                .file = path,
                                .storageVersion = uint(version),
                                .name = name,
                                .description = dest
                        });
                break;
            }
        }

        if (keyError) {
            cout << "error to create storage " << errorToString(keyError) << endl;
        } else {
            cout << "storage created successfully" << endl;
        }
    });

    it.cmd({"l", "login"}, "enter encrypted storage", []() {
        auto filePath = ask_from_term("input path : ");
        auto storageInfo = thekey::storage(filePath);
        if (!storageInfo) {
            cerr << "can't open file " << filePath << endl;
            return;
        }
        switch (storageInfo->storageVersion) {
            case 1:
                thekey_v1_term::login(filePath);
                return;
            case 2:
                thekey_term_v2::login(filePath);
                return;
            default:
                cerr << "storage version " << storageInfo->storageVersion << " not supported " << filePath
                     << endl;
                return;
        }
    });


    it.cmd({"otp"}, "otp light tools. Generate and verify one-time passwords without storage use", []() {
        thekey_otp::interactive();
    });

    it.loop();
    cout << "bye" << endl;


    return 0;
}
