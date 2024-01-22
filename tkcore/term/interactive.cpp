//
// Created by panda on 21.01.24.
//

#include "cmd_processing.h"
#include "public/storage2/storage.h"
#include "public/key_storage_v1.h"
#include "def_header.h"
#include "term_utils.h"

using namespace thekey_term;
using namespace term_utils;
using namespace std;

#define COLUMN_WIDTH 40
static char ident = '\t';

static void print_help();

static void find_storage();

static void create_storage();

void interactive() {
    cout << "TheKey interactive mode" << endl;

    int exitFlag = 0;
    while (!exitFlag) {
        auto cmd = ask_from_term("> ");

        if (cmd == "q" || cmd == "exit" || cmd == "quit")
            return;

        if (cmd == "h" || cmd == "help") {
            print_help();
            continue;
        }

        if (cmd == "find") {
            find_storage();
            continue;
        }

        if (cmd == "create") {
            create_storage();
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

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "find";
    cout << "find storage" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "create";
    cout << "create storage" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "login";
    cout << "login storage" << endl;
}

void find_storage() {
    auto path = ask_from_term("input path: ");
    thekey_term::findStorages(path);
}

void create_storage() {
    auto path = ask_from_term("input path: ");
    auto version = ask_int_from_term("input storage version: ");
    if (version > 2) {
        cerr << "version not support " << version << " last version support " << 2 << endl;
        return;
    }
    auto name = ask_from_term("input name: ");
    auto dest = ask_from_term("input dest: ");

    int error = 0;
    switch (version) {
        case 1: {
            error = thekey_v1::createStorage(
                    {.file = path, .storageVersion = uint(version), .name = name, .description = dest});
            break;
        }
        case 2: {
            error = thekey_v2::createStorage(
                    {.file = path, .storageVersion = uint(version), .name = name, .description = dest});
            break;
        }
    }

    if (error) {
        cout << "error to create storage " << error << endl;
    } else {
        cout << "storage created successfully" << endl;
    }

}

