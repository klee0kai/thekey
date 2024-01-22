//
// Created by panda on 21.01.24.
//

#include "storage_v2.h"
#include "public/storage2/storage.h"
#include "term_utils.h"

using namespace std;
using namespace thekey_v2;
using namespace term_utils;

#define COLUMN_WIDTH 40
static char ident = '\t';

static shared_ptr<thekey_v2::KeyStorageV2> storageV2 = {};

static void print_help();

void thekey_term_v2::login(const std::string &filePath) {
    auto storageInfo = thekey_v2::storageFullInfo(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << filePath << endl;
        return;
    }

    for (int tryPasswInput = 0; tryPasswInput < 3; tryPasswInput++) {
        auto passw = ask_password_from_term("Input password: ");
        storageV2 = storage(filePath, passw);
        if (!storageV2) {
            cerr << "error login to " << filePath << endl;
            continue;
        }
        break;
    }

    cout << "Reading storage..." << endl;
    storageV2->readAll();
    cout << "Welcome to storage '" << storageInfo->name << "' version " << storageInfo->storageVersion << endl;
    cout << "Input help for look the manual" << endl;
    while (true) {
        auto cmd = ask_from_term("> ");

        if (cmd == "q" || cmd == "exit" || cmd == "quit") {
            storageV2.reset();
            return;
        }

        if (cmd == "h" || cmd == "help") {
            print_help();
            continue;
        }
    }

}

void print_help() {
    cout << "TheKey - (storageVersion. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << endl;
    cout << ident << "Commands:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "help or h";
    cout << "help for commands" << endl;

}