//
// Created by panda on 14.01.24.
//

#include "termk1.h"
#include "../utils/term_utils.h"
#include "../utils/Interactive.h"
#include "common.h"
#include "key1.h"
#include "k1tok2.h"

using namespace std;
using namespace thekey;
using namespace thekey_v1;
using namespace term;

static void printNote(const thekey_v1::DecryptedNote &note);

void thekey_v1::login(const std::string &filePath) {
    shared_ptr<thekey_v1::KeyStorageV1> storageV1 = {};
    keyError = 0;

    auto storageInfo = thekey_v1::storageV1Info(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << errorToString(keyError) << endl;
        return;
    }

    auto message = "Input password. Max length " + to_string(storageInfo->passwLen) + " : ";
    auto passw = term::ask_password_from_term(message);
    storageV1 = thekey_v1::storage(filePath, passw);
    if (!storageV1) {
        cerr << "error login to " << filePath << " " << errorToString(keyError) << endl;
        return;
    }

    cout << "Reading storage..." << endl;
    storageV1->readAll();
    cout << string("Welcome to storage '") << storageInfo->name << "'" << endl;

    auto it = Interactive();
    it.helpTitle = "Storage '" + storageInfo->name + "' interactive mode. "
                   + "Storage version is " + to_string(storageInfo->storageVersion);

    it.cmd({"info"}, "print storage info", [&]() {
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
    });

    it.cmd({"l", "list"}, "list storage notes", [&]() {
        if (!storageV1)return;
        for (const auto &note: storageV1->notes(TK1_GET_NOTE_INFO)) {
            cout << "-------------------------------------------" << endl;
            printNote(note);
        }
        cout << "-------------------------------------------" << endl;
    });

    it.cmd({"p", "passw"}, "print note password", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes(TK1_GET_NOTE_INFO);
        for (const auto &note: notes) {
            cout << ++index << ") '" << note.site
                 << "' / '" << note.login
                 << "' / '" << note.description
                 << "'" << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }

        auto noteFull = storageV1->note(notes[noteIndex - 1].notePtr, TK1_GET_NOTE_INFO | TK1_GET_NOTE_PASSWORD);
        printNote(*noteFull);
        cout << endl;
    });

    it.cmd({"noteHist"}, "note passwords history", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes(TK1_GET_NOTE_INFO);
        for (const auto &note: notes) {
            cout << ++index << ") '" << note.site
                 << "' / '" << note.login
                 << "' hist length " << note.history.size()
                 << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        for (const auto &item: storageV1->note(notes[noteIndex - 1].notePtr, TK1_GET_NOTE_HISTORY_FULL)->history) {
            cout << "password: " << item.passw << endl;

            std::tm *changeTm = gmtime((time_t *) &item.genTime);
            cout << "change time : " << asctime(changeTm) << endl;
        }
        cout << endl;
    });

    it.cmd({"create"}, "create new note", [&]() {
        if (!storageV1)return;
        auto info = storageV1->info();
        auto site = ask_from_term("site. max len " + to_string(info.siteLen) + " : ");
        auto login = ask_from_term("login max len " + to_string(info.loginLen) + " : ");
        auto passw = ask_password_from_term("password max len " + to_string(info.passwLen) + " : ");
        auto desc = ask_from_term("description max len " + to_string(info.descLen) + " : ");
        auto notePtr = storageV1->createNote(
                {
                        .site =site,
                        .login = login,
                        .passw =passw,
                        .description = desc,
                }
        );
        if (!notePtr) {
            cerr << "error to save note " << errorToString(keyError) << endl;
            return;
        }
        cout << "note saved " << notePtr << endl;
    });

    it.cmd({"edit"}, "edit note", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes(TK1_GET_NOTE_INFO);
        for (const auto &note: notes) {
            cout << ++index << ") '" << note.site
                 << "' / '" << note.login
                 << "' hist length " << note.history.size()
                 << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto note = storageV1->note(notes[noteIndex - 1].notePtr, TK1_GET_NOTE_FULL);
        auto info = storageV1->info();

        cout << "Note " + to_string(note->notePtr) + " edit mode";
        auto editIt = Interactive();
        editIt.helpTitle = "Note " + to_string(note->notePtr) + " edit mode";

        editIt.cmd({"p", "print"}, "print note", [&]() {
            cout << "current note is: " << endl;
            printNote(*note);
        });

        editIt.cmd({"s", "site"}, "edit site", [&]() {
            note->site = ask_from_term("site. max len " + to_string(info.siteLen) + " : ");
        });

        editIt.cmd({"l", "login"}, "edit login", [&]() {
            note->login = ask_from_term("login max len " + to_string(info.loginLen) + " : ");
        });

        editIt.cmd({"passw"}, "edit password", [&]() {
            note->passw = ask_password_from_term("password max len " + to_string(info.passwLen) + " : ");
        });

        editIt.cmd({"d", "desc"}, "edit description", [&]() {
            note->description = ask_from_term("description max len " + to_string(info.descLen) + " : ");
        });

        editIt.loop();

        int error = storageV1->setNote(*note);
        if (error) {
            cerr << "error to save note " << errorToString(error) << endl;
            return;
        } else {
            cout << "note saved " << note->notePtr << endl;
        }
    });

    it.cmd({"remove"}, "remove note", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes(TK1_GET_NOTE_INFO);
        for (const auto &note: notes) {
            cout << ++index << ") '" << note.site << "' / '" << note.login << "' hist length " << note.history.size()
                 << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        storageV1->removeNote(notes[noteIndex - 1].notePtr);
        cout << "note removed" << endl;
    });

    it.cmd({"gen"}, "generate new password", [&]() {
        if (!storageV1)return;
        cout << "select password encSelect: " << endl;
        cout << "0) numbers only " << endl;
        cout << "1) english symbols and numbers " << endl;
        cout << "2) english symbols, numbers, spec symbols " << endl;
        cout << "3) english symbols, numbers, spec symbols, space " << endl;

        auto encoding = 0;
        auto encSelect = term::ask_int_from_term();
        switch (encSelect) {
            case 0:
                encoding = ENC_NUM_ONLY;
                break;
            case 1:
                encoding = ENC_EN_NUM;
                break;
            case 2:
                encoding = ENC_EN_NUM_SPEC_SYMBOLS;
                break;
            case 3:
                encoding = ENC_EN_NUM_SPEC_SYMBOLS_SPACE;
                break;
        }

        auto len = term::ask_int_from_term("length of password: ");
        auto passw = storageV1->genPassw(len, encoding);
        cout << "generated password '" << passw << "' " << endl;
    });

    it.cmd({"hist"}, "print gen password history", [&]() {
        if (!storageV1)return;
        for (const auto &hist: storageV1->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL)) {
            cout << "-------------------------------------------" << endl;
            cout << "passw: " << hist.passw << endl;
            std::tm *changeTm = std::gmtime((time_t *) &hist.genTime);
            cout << "change time : " << asctime(changeTm) << endl;
        }
        cout << "-------------------------------------------" << endl;
    });


    it.cmd({"changePassw"}, "change storage master password", [&]() {
        if (!storageV1)return;

        auto newPath = term::ask_from_term("write new path to save storage: ");
        auto passw = term::ask_password_from_term("write new storage master passw: ");
        if (!ends_with(newPath, ".ckey")) newPath += ".ckey";

        cout << "changing password for storage..." << endl;
        int error = storageV1->saveNewPassw(newPath, passw);
        if (error) {
            cerr << "error to change storage password : " << errorToString(error) << endl;
            return;
        }
        cout << "storage password has been changed to new file : " << newPath << endl;
    });

    it.cmd({"migrate"}, "migrate storage to v2", [&]() {
        if (!storageV1)return;

        auto newPath = term::ask_from_term("write new path to save storage: ");
        auto passw = term::ask_password_from_term("write new storage master passw: ");
        if (!ends_with(newPath, ".ckey")) newPath += ".ckey";

        cout << "migrating storage to version 2..." << flush;

        int error = migrateK1toK2(*storageV1, newPath, passw, [](const float &) { cout << "." << flush; });
        cout << endl;
        if (error) {
            cerr << "error to migrate storage : " << errorToString(error) << endl;
            return;
        }
        cout << "storage has been migrated to version 2 : " << newPath << endl;
    });


    it.loop();
    storageV1.reset();

    cout << "Storage '" + storageInfo->name + "' closed." << endl;

}


static void printNote(const thekey_v1::DecryptedNote &note) {
    cout << "site: '" << note.site << "'" << endl;
    cout << "login: '" << note.login << "'" << endl;
    if (!note.passw.empty()) cout << "passw: '" << note.passw << "'" << endl;
    cout << "desc: '" << note.description << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.genTime);
    cout << "change time : " << asctime(changeTm) << endl;
    cout << "hist len: " << note.history.size() << endl;
}