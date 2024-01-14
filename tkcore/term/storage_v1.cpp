//
// Created by panda on 14.01.24.
//

#include "storage_v1.h"
#include "term_utils.h"

using namespace std;
using namespace thekey;

#define COLUMN_WIDTH 40
static char ident = '\t';

static shared_ptr<thekey_v1::KeyStorageV1> storageV1 = {};

static void printHelp();

static void listNotes();

static void createNote();

static void noteHist();

static void showGenHistory();

static void showInfo();

static int processCmdsStTerm(int argc, char **argv);

void thekey_term_v1::login(const std::string &filePath) {
    auto storageInfo = thekey_v1::storageV1Info(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << filePath << endl;
        return;
    }

    cout << "TheKey. Version is " << TERM_VERSION << ". Designed by Panda" << endl;
    for (int tryPasswInput = 0; tryPasswInput < 3; tryPasswInput++) {
        auto message = "Input password. Max length " + to_string(storageInfo->passwLen) + " : ";
        auto passw = term_utils::ask_password_from_term(message);
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

    cout << "Welcome to storage '" << storageInfo->name << "' version " << storageInfo->storageVersion << endl;
    cout << "Input help for look the manual" << endl;

    char cmdBuf[100];
    int exitFlag = 0;
    while (!exitFlag) {
        term_utils::clear_opt();
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

static int processCmdsStTerm(int argc, char **argv) {
    if (argc <= 0 || strlen(argv[0]) == 0)
        //ignore, continue
        return 0;

    if (strcmp(argv[0], "q") == 0 || strcmp(argv[0], "exit") == 0 || strcmp(argv[0], "quit") == 0)
        return 1;

    if (strcmp(argv[0], "help") == 0 || strcmp(argv[0], "h") == 0) {
        printHelp();
        return 0;
    }

    if (strcmp(argv[0], "list") == 0 || strcmp(argv[0], "l") == 0) {
        listNotes();
        return 0;
    }

    if (strcmp(argv[0], "noteHist") == 0) {
        noteHist();
        return 0;
    }

    if (strcmp(argv[0], "create") == 0) {
        createNote();
        return 0;
    }

    if (strcmp(argv[0], "hist") == 0) {
        showGenHistory();
        return 0;
    }

    if (strcmp(argv[0], "info") == 0 || strcmp(argv[0], "i") == 0) {
        showInfo();
        return 0;
    }

    cerr << "unknown command  " << argv[0] << endl;

    return 0;
}

static void printHelp() {
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
    cout << std::left << "noteHist";
    cout << "note passwords list" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "create";
    cout << "create new note" << endl;


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

static void noteHist() {
    if (!storageV1)return;
    auto index = 0;
    auto notes = storageV1->notes();
    for (const auto &item: notes) {
        auto note = storageV1->note(item, 0);
        cout << ++index << ") '" << note->site << "' / '" << note->login << "' hist length " << note->histLen << endl;
    }
    auto noteIndex = stoi(term_utils::ask_from_term("Select note. Write index: "));
    if (noteIndex < 1 || noteIndex > notes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return;
    }
    for (const auto &item: storageV1->noteHist(notes[noteIndex - 1])) {
        cout << "password: " << item.passw << endl;
    }
    cout << endl;
}

static void createNote() {
    if (!storageV1)return;
    auto info = storageV1->info();
    auto site = term_utils::ask_from_term("site. max len " + to_string(info.siteLen) + " : ");
    auto login = term_utils::ask_from_term("login max len " + to_string(info.loginLen) + " : ");
    auto passw = term_utils::ask_password_from_term("password max len " + to_string(info.passwLen) + " : ");
    auto desc = term_utils::ask_from_term("description max len " + to_string(info.descLen) + " : ");
    auto notePtr = storageV1->createNote();
    int error = storageV1->setNote(notePtr, {
            .site =site,
            .login = login,
            .passw =passw,
            .description = desc,
    });
    if (error) {
        cerr << "error to save note " << error << endl;
        return;
    }
    cout << "note saved " << notePtr;
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
    cout << "technical limitations" << endl;
    cout << "storage name max length: " << info.storageNameLen << endl;
    cout << "storage description max length: " << info.storageDescriptionLen << endl;
    cout << "site max length: " << info.siteLen << endl;
    cout << "password max length: " << info.passwLen << endl;
    cout << "description max length: " << info.descLen << endl;
    cout << "note max history: " << info.noteMaxHist << endl;

    cout << endl;
}