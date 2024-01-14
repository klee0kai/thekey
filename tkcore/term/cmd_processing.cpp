//
// Created by panda on 06.06.2020.
//

#include "cmd_processing.h"
#include <iomanip>
#include <sys/ioctl.h>
#include <sys/fcntl.h>
#include <curses.h>
#include "thekey.h"
#include "term_utils.h"

#define COLUMN_WIDTH 40

using namespace std;
using namespace thekey;

static char ident = '\t';

static shared_ptr<thekey_v1::KeyStorageV1> storageV1 = {};

void cmd_pr::findStorages(const string &folder) {
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

void cmd_pr::printHelp() {
    cout << "TheKey - (storageVersion. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << endl;
    cout << ident << "Options:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-h or --help";
    cout << "help" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-f or --find or --list";
    cout << "list available storages on device" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-l [storagePath] or --login [storagePath]";
    cout << "login to crypted storage" << endl;
}

static void printHelpStTerm() {
    cout << "TheKey - (storageVersion. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << endl;
    cout << ident << "Commands:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "help or h";
    cout << "help for commands" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "list or l";
    cout << "list notes" << endl;


    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "hist";
    cout << "show history of gen passwds" << endl;


    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "info or i";
    cout << "info about storage" << endl;


    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "exit or quit or q";
    cout << "exit from program" << endl;
}


static void listNotes() {
    if (!storageV1)return;
    for (const auto &item: storageV1->notes()) {
        auto note = storageV1->note(item, 1);
        cout << "-------------------------------------------" << endl;
        cout << "site: " << note->site << endl;
        cout << "login: " << note->login << endl;
        cout << "pass: " << note->passw << endl;
        cout << "desc: " << note->description << endl;
        cout << "hist len: " << note->histLen << endl;
    }
    cout << "-------------------------------------------" << endl;
}

static void showGenHistory() {
    if (!storageV1)return;
    for (const auto &item: storageV1->genPasswHist()) {
        cout << "-------------------------------------------" << endl;
        cout << "passw: " << item.passw << endl;
    }
    cout << "-------------------------------------------" << endl;
}

static void showInfo() {
    if (!storageV1)return;
    auto info = storageV1->info();
    cout << "storage: " << info.path << endl;
    cout << "storageVersion: " << info.storageVersion << endl;
    cout << "name: " << info.name << endl;
    cout << "desc: " << info.description << endl;
    cout << "notesCount: " << info.notesCount << endl;
    cout << "histCount: " << info.genPasswCount << endl;
    cout << endl;
}

static int processCmdsStTerm(int argc, char **argv) {
    if (argc <= 0 || strlen(argv[0]) == 0)
        //ignore, continue
        return 0;

    if (strcmp(argv[0], "q") == 0 || strcmp(argv[0], "exit") == 0 || strcmp(argv[0], "quit") == 0)
        return 1;

    if (strcmp(argv[0], "help") == 0 || strcmp(argv[0], "h") == 0) {
        printHelpStTerm();
        return 0;
    }

    if (strcmp(argv[0], "list") == 0 || strcmp(argv[0], "l") == 0) {
        listNotes();
        return 0;
    }

    //TODO hist of note

    if (strcmp(argv[0], "hist") == 0) {
        showGenHistory();
        return 0;
    }


    if (strcmp(argv[0], "info") == 0 || strcmp(argv[0], "i") == 0) {
        showInfo();
        return 0;
    }

    cout << "unknown command  " << argv[0] << endl;

    return 0;
}

void cmd_pr::login(const std::string &filePath) {
    auto storageInfo = thekey::storage(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << filePath << endl;
        return;
    }

    cout << "TheKey (version " << TERM_VERSION << ") designed by Panda" << endl;
    for (int tryPasswInput = 0; tryPasswInput < 3; tryPasswInput++) {
        cout << "Input passw:";
        auto passw = term_utils::get_password();
        cout << endl;;
        storageV1 = thekey_v1::storage(filePath, passw);

        if (!storageV1) {
            cerr << "error login to " << filePath << endl;
            cout << endl;
            continue;
        }
        break;
    }

    cout << "Reading storage..." << endl;
    storageV1->readAll();

    cout << "Welcome to storage " << storageInfo->name << " version " << storageInfo->storageVersion << endl;
    cout << "Input help for look the manual" << endl;

    char cmdBuf[100];
    int exitFlag = 0;
    while (!exitFlag) {
        cout << ">";
        memset(cmdBuf, 0, 0);
        cin.getline(cmdBuf, sizeof(cmdBuf) - 1, '\n');

        size_t argc = term_utils::argsCount(cmdBuf);
        char **argv = new char *[argc];
        term_utils::splitArgs(cmdBuf, argv, argc);

        exitFlag = processCmdsStTerm(argc, argv);
        delete[]argv;
    }

    storageV1.reset();
}







