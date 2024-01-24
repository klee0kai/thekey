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

static void printHelp();

static void listNotes();

static void notePassword();

static void createNote();

static void editNote();

static void removeNote();

static void noteHist();

static void printPasswordHistory();

static void generateNewPassword();

static void printInfo();

static void printNote(const thekey_v2::DecryptedNote &note);

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

        if (cmd == "list" || cmd == "l") {
            listNotes();
            continue;
        }

        if (cmd == "passw" || cmd == "p") {
            notePassword();
            continue;
        }


        if (cmd == "noteHist") {
            noteHist();
            continue;
        }

        if (cmd == "create") {
            createNote();
            continue;
        }

        if (cmd == "edit") {
            editNote();
            continue;
        }

        if (cmd == "remove") {
            removeNote();
            continue;
        }

        if (cmd == "gen") {
            generateNewPassword();
            continue;
        }

        if (cmd == "hist") {
            printPasswordHistory();
            continue;
        }

        if (cmd == "info" || cmd == "i") {
            printInfo();
            continue;
        }


        if (cmd == "h" || cmd == "help") {
            printHelp();
            continue;
        }

        cerr << "unknown command  " << cmd << endl;
    }

}

void printHelp() {
    cout << "TheKey - (storageVersion. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << endl;
    cout << ident << "Commands:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "help or h";
    cout << "help for commands" << endl;
}


static void listNotes() {
    if (!storageV2)return;
    for (const auto &item: storageV2->notes()) {
        auto note = storageV2->note(item);
        cout << "-------------------------------------------" << endl;
        printNote(*note);
    }
    cout << "-------------------------------------------" << endl;
}

static void notePassword() {
    if (!storageV2)return;

    auto index = 0;
    auto notes = storageV2->notes();
    for (const auto &item: notes) {
        auto note = storageV2->note(item, 0);
        cout << ++index << ") '" << note->site << "' / '" << note->login << "' / '" << note->description << "'" << endl;
    }
    auto noteIndex = term_utils::ask_int_from_term("Select note. Write index: ");
    if (noteIndex < 1 || noteIndex > notes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return;
    }

    auto noteFull = storageV2->note(notes[noteIndex - 1], 1);
    printNote(*noteFull);
    cout << endl;
}


static void noteHist() {
    if (!storageV2)return;
    auto index = 0;
    auto notes = storageV2->notes();
    for (const auto &item: notes) {
        auto note = storageV2->note(item, 0);
        cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
    }
    auto noteIndex = term_utils::ask_int_from_term("Select note. Write index: ");
    if (noteIndex < 1 || noteIndex > notes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return;
    }
//    TODO
//    for (const auto &item: storageV2->noteHist(notes[noteIndex - 1])) {
//        cout << "password: " << item.passw << endl;
//
//        std::tm *changeTm = std::gmtime((time_t *) &item.genTime);
//        cout << "change time : " << asctime(changeTm) << endl;
//    }
//    cout << endl;
}

static void createNote() {
    if (!storageV2)return;
    auto site = term_utils::ask_from_term("site : ");
    auto login = term_utils::ask_from_term("login : ");
    auto passw = term_utils::ask_password_from_term("password : ");
    auto desc = term_utils::ask_from_term("description : ");
    auto notePtr = storageV2->createNote();
    int error = storageV2->setNote(notePtr, {
            .site =site,
            .login = login,
            .passw = passw,
            .description = desc,
    });
    if (error) {
        cerr << "error to save note " << error << endl;
        return;
    }
    cout << "note saved " << notePtr << endl;
}


static void editNote() {
    if (!storageV2)return;
    auto index = 0;
    auto notes = storageV2->notes();
    for (const auto &item: notes) {
        auto note = storageV2->note(item, 0);
        cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
    }
    auto noteIndex = term_utils::ask_int_from_term("Select note. Write index: ");
    if (noteIndex < 1 || noteIndex > notes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return;
    }
    auto notePtr = notes[noteIndex - 1];

    while (true) {
        auto note = storageV2->note(notePtr, 1);
        cout << "current note is: " << endl;
        printNote(*note);
        cout << "input 'back' - to back from edit mode" << endl;
        cout << "input 'site' - edit site" << endl;
        cout << "input 'login' - edit login" << endl;
        cout << "input 'passw' - edit passw" << endl;
        cout << "input 'desc' - edit description" << endl;

        auto cmd = term_utils::ask_from_term();

        if (cmd == "back") {
            cout << "exit from edit mode" << endl;
            return;
        }
        if (cmd == "site") {
            note->site = term_utils::ask_from_term("site : ");
        } else if (cmd == "login") {
            note->login = term_utils::ask_from_term("login : ");
        } else if (cmd == "passw") {
            note->passw = term_utils::ask_password_from_term("password : ");
        } else if (cmd == "desc") {
            note->description = term_utils::ask_from_term("description : ");
        } else {
            cerr << "cmd incorrect '" << cmd << "' exit from edit mode" << endl;
            return;
        }
        int error = storageV2->setNote(notePtr, *note);
        if (error) {
            cerr << "error to save note " << error << " exit from edit mode" << endl;
            return;
        }
    }
}


static void removeNote() {
    if (!storageV2)return;
    auto index = 0;
    auto notes = storageV2->notes();
    for (const auto &item: notes) {
        auto note = storageV2->note(item, 0);
        cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
    }
    auto noteIndex = term_utils::ask_int_from_term("Select note. Write index: ");
    if (noteIndex < 1 || noteIndex > notes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return;
    }
    auto notePtr = notes[noteIndex - 1];
    storageV2->removeNote(notePtr);
    cout << "note removed" << endl;
}

static void printPasswordHistory() {
    if (!storageV2)return;
    for (const auto &item: storageV2->passwordsHistory()) {
//        TODO
//        cout << "-------------------------------------------" << endl;
//        cout << "passw: " << item.passw << endl;
//        std::tm *changeTm = std::gmtime((time_t *) &item.genTime);
//        cout << "change time : " << asctime(changeTm) << endl;
    }
    cout << "-------------------------------------------" << endl;
}

static void generateNewPassword() {
    if (!storageV2)return;

    cout << "select password encoding: " << endl;
    cout << ") numbers only " << endl;
    cout << "1) english symbols and numbers " << endl;
    cout << "2) english symbols, numbers, spec symbols " << endl;
    cout << "3) english symbols, numbers, spec symbols, space " << endl;
    // TODO find encoding
    auto encoding = term_utils::ask_int_from_term();
    auto len = term_utils::ask_int_from_term("length of password: ");
    auto passw = storageV2->genPassword(encoding, len);
    cout << "generated password '" << passw << "' " << endl;
}


static void printInfo() {
    if (!storageV2)return;
    auto info = storageV2->info();
    cout << "storage: " << info.path << endl;
    cout << "storageVersion: " << info.storageVersion << endl;
    cout << "name: " << info.name << endl;
    cout << "desc: " << info.description << endl;

    cout << endl;
}

static void printNote(const thekey_v2::DecryptedNote &note) {
    cout << "site: '" << note.site << "'" << endl;
    cout << "login: '" << note.login << "'" << endl;
    if (!note.passw.empty()) cout << "passw: '" << note.passw << "'" << endl;
    cout << "desc: '" << note.description << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.genTime);
    cout << "gen time : " << asctime(changeTm) << endl;
    cout << "color : " << note.color << endl;
}