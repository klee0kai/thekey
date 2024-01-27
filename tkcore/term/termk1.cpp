//
// Created by panda on 14.01.24.
//

#include "termk1.h"
#include "utils/term_utils.h"
#include "utils/Interactive.h"
#include "core/common.h"
#include "storage1/key_storage_v1.h"

using namespace std;
using namespace thekey;
using namespace term;

static void printNote(const thekey_v1::DecryptedNote &note);

void thekey_v1_term::login(const std::string &filePath) {
    shared_ptr<thekey_v1::KeyStorageV1> storageV1 = {};

    auto storageInfo = thekey_v1::storageV1Info(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << filePath << endl;
        return;
    }

    auto message = "Input password. Max length " + to_string(storageInfo->passwLen) + " : ";
    auto passw = term::ask_password_from_term(message);
    storageV1 = thekey_v1::storage(filePath, passw);
    if (!storageV1) {
        cerr << "error login to " << filePath << endl;
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
        for (const auto &item: storageV1->notes()) {
            auto note = storageV1->note(item);
            cout << "-------------------------------------------" << endl;
            printNote(*note);
        }
        cout << "-------------------------------------------" << endl;
    });

    it.cmd({"p", "passw"}, "print note password", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes();
        for (const auto &item: notes) {
            auto note = storageV1->note(item, 0);
            cout << ++index << ") '" << note->site
                 << "' / '" << note->login
                 << "' / '" << note->description
                 << "'" << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }

        auto noteFull = storageV1->note(notes[noteIndex - 1], 1);
        printNote(*noteFull);
        cout << endl;
    });

    it.cmd({"noteHist"}, "note passwords history", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes();
        for (const auto &item: notes) {
            auto note = storageV1->note(item, 0);
            cout << ++index << ") '" << note->site
                 << "' / '" << note->login
                 << "' hist length " << note->histLen
                 << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        for (const auto &item: storageV1->noteHist(notes[noteIndex - 1])) {
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
        cout << "note saved " << notePtr << endl;
    });

    it.cmd({"edit"}, "edit note", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes();
        for (const auto &item: notes) {
            auto note = storageV1->note(item, 0);
            cout << ++index << ") '" << note->site
                 << "' / '" << note->login
                 << "' hist length " << note->histLen
                 << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto notePtr = notes[noteIndex - 1];
        auto note = storageV1->note(notePtr, 1);
        auto info = storageV1->info();

        cout << "Note " + to_string(notePtr) + " edit mode";
        auto editIt = Interactive();
        editIt.helpTitle = "Note " + to_string(notePtr) + " edit mode";

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

        int error = storageV1->setNote(notePtr, *note);
        if (error) {
            cerr << "error to save note " << error << endl;
            return;
        } else {
            cout << "note saved " << notePtr << endl;
        }
    });

    it.cmd({"remove"}, "remove note", [&]() {
        if (!storageV1)return;
        auto index = 0;
        auto notes = storageV1->notes();
        for (const auto &item: notes) {
            auto note = storageV1->note(item, 0);
            cout << ++index << ") '" << note->site << "' / '" << note->login << "' hist length " << note->histLen
                 << endl;
        }
        auto noteIndex = ask_int_from_term("Select note. Write index: ");
        if (noteIndex < 1 || noteIndex > notes.size()) {
            cerr << "incorrect index " << noteIndex << endl;
            return;
        }
        auto notePtr = notes[noteIndex - 1];
        storageV1->removeNote(notePtr);
        cout << "note removed" << endl;
    });

    it.cmd({"gen"}, "generate new password", [&]() {
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

    it.cmd({"hist"}, "print gen password history", [&]() {
        if (!storageV1)return;
        for (const auto &item: storageV1->genPasswHist()) {
            cout << "-------------------------------------------" << endl;
            cout << "passw: " << item.passw << endl;
            std::tm *changeTm = std::gmtime((time_t *) &item.genTime);
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
        int error = storageV1->saveToNewPassw(newPath, passw);
        if (error) {
            cerr << "error to change storage password : " << error << endl;
            return;
        }
        cout << "storage password changed to new file : " << newPath << endl;
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
    cout << "hist len: " << note.histLen << endl;
}