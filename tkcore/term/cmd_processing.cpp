//
// Created by panda on 06.06.2020.
//

#include "cmd_processing.h"
#include <iomanip>
#include "thekey.h"
#include "storage_v1.h"

#define COLUMN_WIDTH 40

using namespace std;
using namespace thekey;

static char ident = '\t';

static shared_ptr<thekey_v1::KeyStorageV1> storageV1 = {};

void thekey_term::findStorages(const string &folder) {
    cout << "Available crypted storages in folder: " << folder << endl;
    auto storagesFound = thekey::findStorages(folder);
    for (const auto &item: storagesFound) {
        cout << "-------------------------------------" << endl;
        cout << "storagePath: " << item.file << endl;
        cout << "name: " << item.name << endl;
        cout << "version: " << item.storageVersion << endl;
        cout << "desc: " << item.description << endl;
        cout << endl;
    }
    cout << "-------------------------------------" << endl;
}

void thekey_term::printHelp() {
    cout << "TheKey - (storageVersion. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << "Designed by Andrey Kuzubov / klee0kai" << endl;
    cout << endl;
    cout << ident << "Options:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-h or --help";
    cout << "help" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-f or --find";
    cout << "list available storages on device" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-l [storagePath] or --login [storagePath]";
    cout << "login to crypted storage" << endl;
}

void thekey_term::login(const std::string &filePath) {
    auto storageInfo = thekey::storage(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << filePath << endl;
        return;
    }
    switch (storageInfo->storageVersion) {
        case 1:
            thekey_term_v1::login(filePath);
            return;
        default:
            cerr << "storage version " << storageInfo->storageVersion << " not supported " << filePath << endl;
            return;
    }
}







