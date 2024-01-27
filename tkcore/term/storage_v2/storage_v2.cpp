//
// Created by panda on 21.01.24.
//

#include "storage_v2.h"
#include "public/storage2/storage.h"
#include "../utils/term_utils.h"
#include "../utils/Interactive.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace thekey_v2;
using namespace term;

static void printNote(const thekey_v2::DecryptedNote &note);

void thekey_term_v2::login(const std::string &filePath) {
    shared_ptr<thekey_v2::KeyStorageV2> storageV2 = {};

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
    cout << string("Welcome to storage '") << storageInfo->name << "'" << endl;

    auto it = Interactive();
    it.helpTitle = "Storage '" + storageInfo->name + "' interactive mode. "
                   + "Storage version is " + to_string(storageInfo->storageVersion);

    it.cmd({"info"}, "print storage info", [&]() {
        if (!storageV2)return;
        auto info = storageV2->info();
        cout << "storage: " << info.path << endl;
        cout << "storageVersion: " << info.storageVersion << endl;
        cout << "name: " << info.name << endl;
        cout << "desc: " << info.description << endl;

        cout << endl;
    });

    it.cmd({"l", "list"}, "list storage notes", [&]() {
        if (!storageV2)return;
        for (const auto &item: storageV2->notes()) {
            auto note = storageV2->note(item);
            cout << "-------------------------------------------" << endl;
            printNote(*note);
        }
        cout << "-------------------------------------------" << endl;
    });

    it.cmd({"p", "passw"}, "print note password", [&]() {
        if (!storageV2)return;
        auto index = 0;
        auto notes = storageV2->notes();
        for (const auto &item: notes) {
            auto note = storageV2->note(item, 0);
            cout << ++index << ") '" << note->site << "' / '" << note->login << "' / '" << note->description << "'"
                 << endl;
        }
        auto noteIndex = term::ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto noteFull = storageV2->note(notes[noteIndex - 1], 1);
        printNote(*noteFull);
        cout << endl;
    });

    it.cmd({"noteHist"}, "note passwords history", [&]() {
        if (!storageV2)return;
        auto index = 0;
        auto notes = storageV2->notes();
        for (const auto &item: notes) {
            auto note = storageV2->note(item, 0);
            cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
        }
        auto noteIndex = term::ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto note = storageV2->note(notes[noteIndex], 0);
        for (const auto &item: note->history) {
            auto hist = storageV2->passwordHistory(item);
            cout << "-------------------------------------------" << endl;
            cout << "passw: " << hist->passw << endl;
            std::tm *changeTm = std::gmtime((time_t *) &hist->genTime);
            cout << "change time : " << asctime(changeTm) << endl;
        }
        cout << endl;
    });

    it.cmd({"create"}, "create new note", [&]() {
        if (!storageV2)return;
        auto site = ask_from_term("site : ");
        auto login = ask_from_term("login : ");
        auto passw = ask_password_from_term("password : ");
        auto desc = ask_from_term("description : ");
        auto notePtr = storageV2->createNote();
        int error = storageV2->setNote(notePtr, {
                .site =site,
                .login = login,
                .passw = passw,
                .description = desc,
        }, TK2_SET_NOTE_TRACK_HISTORY);
        if (error) {
            cerr << "error to save note " << error << endl;
            return;
        }
        cout << "note saved " << notePtr << endl;
    });

    it.cmd({"edit"}, "edit note", [&]() {
        if (!storageV2)return;
        auto index = 0;
        auto notes = storageV2->notes();
        for (const auto &item: notes) {
            auto note = storageV2->note(item, 0);
            cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto notePtr = notes[noteIndex - 1];
        auto note = storageV2->note(notePtr, 1);
        auto info = storageV2->info();

        auto editIt = Interactive();
        cout << "Note " << to_string(notePtr) << " edit mode";
        editIt.helpTitle = "Note " + to_string(notePtr) + " edit mode";

        editIt.cmd({"p", "print"}, "print note", [&]() {
            cout << "current note is: " << endl;
            printNote(*note);
        });

        editIt.cmd({"s", "site"}, "edit site", [&]() {
            note->site = term::ask_from_term("site : ");
        });

        editIt.cmd({"l", "login"}, "edit login", [&]() {
            note->login = term::ask_from_term("login : ");
        });

        editIt.cmd({"passw"}, "edit password", [&]() {
            note->passw = term::ask_password_from_term("password : ");
        });

        editIt.cmd({"d", "desc"}, "edit description", [&]() {
            note->description = term::ask_from_term("description : ");
        });

        editIt.loop();

        int error = storageV2->setNote(notePtr, *note, TK2_SET_NOTE_TRACK_HISTORY);
        if (error) {
            cerr << "error to save note " << error << endl;
            return;
        } else {
            cout << "note saved " << notePtr << endl;
        }
    });

    it.cmd({"remove"}, "remove note", [&]() {
        if (!storageV2)return;
        auto index = 0;
        auto notes = storageV2->notes();
        for (const auto &item: notes) {
            auto note = storageV2->note(item, 0);
            cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
        }
        auto noteIndex = term::ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto notePtr = notes[noteIndex - 1];
        storageV2->removeNote(notePtr);
        cout << "note removed" << endl;
    });

    it.cmd({"gen"}, "generate new password", [&]() {
        if (!storageV2)return;

        cout << "select password encSelect: " << endl;
        cout << "0) numbers only " << endl;
        cout << "1) english symbols and numbers " << endl;
        cout << "2) english symbols, numbers, spec symbols " << endl;
        cout << "3) english symbols, numbers, spec symbols, space " << endl;
        auto schemeFlags = 0;
        auto encSelect = term::ask_int_from_term();
        switch (encSelect) {
            case 0:
                schemeFlags = SCHEME_NUMBERS;
                break;
            case 1:
                schemeFlags = SCHEME_ENGLISH | SCHEME_NUMBERS;
                break;
            case 2:
                schemeFlags = SCHEME_ENGLISH | SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS;
                break;
            case 3:
                schemeFlags = SCHEME_ENGLISH | SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS | SCHEME_SPACE_SYMBOL;
                break;
        }
        auto schemeType = tkey2_salt::find_scheme_type_by_flags(schemeFlags);

        auto len = term::ask_int_from_term("length of password: ");
        auto passw = storageV2->genPassword(schemeType, len);
        cout << "generated password '" << passw << "' " << endl;
    });

    it.cmd({"hist"}, "print gen password history", [&]() {
        if (!storageV2)return;
        for (const auto &item: storageV2->passwordsHistory()) {
            auto hist = storageV2->passwordHistory(item);
            cout << "-------------------------------------------" << endl;
            cout << "passw: " << hist->passw << endl;
            std::tm *changeTm = std::gmtime((time_t *) &hist->genTime);
            cout << "change time : " << asctime(changeTm) << endl;
        }
        cout << "-------------------------------------------" << endl;
    });

    it.loop();
    storageV2.reset();

    cout << "Storage '" << storageInfo->name << "' closed." << endl;

}


static void printNote(const thekey_v2::DecryptedNote &note) {
    cout << "site: '" << note.site << "'" << endl;
    cout << "login: '" << note.login << "'" << endl;
    if (!note.passw.empty()) cout << "passw: '" << note.passw << "'" << endl;
    cout << "desc: '" << note.description << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.genTime);
    cout << "gen time : " << asctime(changeTm) << endl;
    cout << "color : " << note.color << endl;
    cout << "hist len : " << note.history.size() << endl;
}