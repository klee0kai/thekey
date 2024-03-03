//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>
#include "tools/uri.h"
#include "tools/base32.h"
#include "salt_text/salt2_schema.h"

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

#define TIME_TOLERANCE 60

using namespace std;
using namespace thekey_v2;
using namespace key_salt;


TEST(Storage2ChangePassw, ChangePasswToNewFile) {
    // GIVEN
    static std::list<string> expectedPassw{};
    auto now = time(NULL);

    auto error = thekey_v2::createStorage(
            {
                    .file = "ts_change_passw.ckey",
                    .name = "ch_passw",
                    .description ="Change Storage Passw"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ts_change_passw.ckey", "simple@pas#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    storage->createNote(
            {
                    .site = "target.site",
                    .login = "@wePers@n1",
                    .passw = "12$3",
                    .description = "mock createNote description",
                    .color = VIOLET,
            });

    auto createNote = storage->createNote(
            {
                    .site = "login.company.vd",
                    .login = "sect@d1v",
                    .passw = "$3$#",
                    .description = "_"
            });
    createNote->passw = "J23";
    storage->setNote(*createNote);

    createNote->passw = "321";
    storage->setNote(*createNote);

    storage->createOtpNotes("otpauth://hotp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example");
    auto createOtpNote = storage->createOtpNotes("otpauth://totp/sha1Issuer%3Asimple%40test.com"
                                                 "?secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A"
                                                 "&issuer=sha1Issuer", TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = PINK;
    storage->setOtpNote(createOtpNote);

    createOtpNote = storage->createOtpNotes("otpauth://yaotp/user@yandex.ru"
                                            "?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user",
                                            TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = ORANGE;
    createOtpNote.pin = "1234";
    storage->setOtpNote(createOtpNote);


    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH), 6));
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH | SCHEME_SPACE_SYMBOL), 8)
    );
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS), 8)
    );


    // WHEN
    storage->saveNewPassw("ts2_change_passw.ckey", "newpassw");

    storage.reset();



    // THEN
    storage = thekey_v2::storage("ts2_change_passw.ckey", "newpassw");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    const auto &notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(2, notes.size());

    auto note = notes.begin();
    ASSERT_EQ("target.site", note->site);
    ASSERT_EQ("@wePers@n1", note->login);
    ASSERT_EQ("12$3", note->passw);
    ASSERT_EQ("mock createNote description", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(VIOLET, note->color);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    note++;
    ASSERT_EQ("login.company.vd", note->site);
    ASSERT_EQ("sect@d1v", note->login);
    ASSERT_EQ("321", note->passw);
    ASSERT_EQ("_", note->description);
    ASSERT_EQ(2, note->history.size());
    ASSERT_EQ(NOCOLOR, note->color);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << createNote->genTime << endl;
    auto noteHist = note->history.begin();
    ASSERT_EQ("J23", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(NOCOLOR, noteHist->color);

    noteHist++;
    ASSERT_EQ("$3$#", noteHist->passw);
    ASSERT_EQ(NOCOLOR, noteHist->color);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);


    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(3, otpNotes.size());

    auto otpNote = otpNotes.begin();
    auto otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_EQ("alice@google.com", otpNote->name);
    ASSERT_EQ("Example", otpNote->issuer);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(NOCOLOR, otpNote->color);
    ASSERT_EQ("", otpNote->pin);


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_EQ("simple@test.com", otpNote->name);
    ASSERT_EQ("sha1Issuer", otpNote->issuer);
    ASSERT_EQ("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(PINK, otpNote->color);
    ASSERT_EQ("", otpNote->pin);


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_EQ("user@yandex.ru", otpNote->name);
    ASSERT_EQ("yandex.ru", otpNote->issuer);
    ASSERT_EQ("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(otpInfo.secret, true))
                                << "yaotp should truncate to 16. Not validate use"
                                << endl;
    ASSERT_EQ(ORANGE, otpNote->color);
    ASSERT_EQ("1234", otpNote->pin);


    auto genHist = storage->genPasswHistoryList(TK2_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPassw.size(), genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPassw.begin();
    for (int i = 0; i < expectedPassw.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index " << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index " << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }

}


TEST(Storage2ChangePassw, ChangePasswToOldFile) {
    // GIVEN
    static std::list<string> expectedPassw{};
    auto now = time(NULL);

    auto error = thekey_v2::createStorage(
            {
                    .file = "ts_change_passw.ckey",
                    .name = "ch_passw",
                    .description ="Change Storage Passw"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ts_change_passw.ckey", "simple@pas#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    storage->createNote(
            {
                    .site = "target.site",
                    .login = "@wePers@n1",
                    .passw = "12$3",
                    .description = "mock createNote description",
                    .color = VIOLET,
            });

    auto createNote = storage->createNote(
            {
                    .site = "login.company.vd",
                    .login = "sect@d1v",
                    .passw = "$3$#",
                    .description = "_"
            });
    createNote->passw = "J23";
    storage->setNote(*createNote);

    createNote->passw = "321";
    storage->setNote(*createNote);

    storage->createOtpNotes("otpauth://hotp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example");
    auto createOtpNote = storage->createOtpNotes("otpauth://totp/sha1Issuer%3Asimple%40test.com"
                                                 "?secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A"
                                                 "&issuer=sha1Issuer", TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = PINK;
    storage->setOtpNote(createOtpNote);

    createOtpNote = storage->createOtpNotes("otpauth://yaotp/user@yandex.ru"
                                            "?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user",
                                            TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = ORANGE;
    createOtpNote.pin = "1234";
    storage->setOtpNote(createOtpNote);


    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH), 6));
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH | SCHEME_SPACE_SYMBOL), 8)
    );
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS), 8)
    );


    // WHEN
    storage->saveNewPassw("ts_change_passw.ckey", "newpassw");

    storage.reset();



    // THEN
    storage = thekey_v2::storage("ts_change_passw.ckey", "newpassw");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    const auto &notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(2, notes.size());

    auto note = notes.begin();
    ASSERT_EQ("target.site", note->site);
    ASSERT_EQ("@wePers@n1", note->login);
    ASSERT_EQ("12$3", note->passw);
    ASSERT_EQ("mock createNote description", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(VIOLET, note->color);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    note++;
    ASSERT_EQ("login.company.vd", note->site);
    ASSERT_EQ("sect@d1v", note->login);
    ASSERT_EQ("321", note->passw);
    ASSERT_EQ("_", note->description);
    ASSERT_EQ(2, note->history.size());
    ASSERT_EQ(NOCOLOR, note->color);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << createNote->genTime << endl;
    auto noteHist = note->history.begin();
    ASSERT_EQ("J23", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(NOCOLOR, noteHist->color);

    noteHist++;
    ASSERT_EQ("$3$#", noteHist->passw);
    ASSERT_EQ(NOCOLOR, noteHist->color);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);


    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(3, otpNotes.size());

    auto otpNote = otpNotes.begin();
    auto otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_EQ("alice@google.com", otpNote->name);
    ASSERT_EQ("Example", otpNote->issuer);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(NOCOLOR, otpNote->color);
    ASSERT_EQ("", otpNote->pin);


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_EQ("simple@test.com", otpNote->name);
    ASSERT_EQ("sha1Issuer", otpNote->issuer);
    ASSERT_EQ("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(PINK, otpNote->color);
    ASSERT_EQ("", otpNote->pin);


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_EQ("user@yandex.ru", otpNote->name);
    ASSERT_EQ("yandex.ru", otpNote->issuer);
    ASSERT_EQ("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(otpInfo.secret, true))
                                << "yaotp should truncate to 16. Not validate use"
                                << endl;
    ASSERT_EQ(ORANGE, otpNote->color);
    ASSERT_EQ("1234", otpNote->pin);


    auto genHist = storage->genPasswHistoryList(TK2_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPassw.size(), genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPassw.begin();
    for (int i = 0; i < expectedPassw.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index " << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index " << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }

}


TEST(Storage2ChangePassw, OldPassw) {
    // GIVEN
    static std::list<string> expectedPassw{};
    auto now = time(NULL);

    auto error = thekey_v2::createStorage(
            {
                    .file = "ts_change_passw.ckey",
                    .name = "ch_passw",
                    .description ="Change Storage Passw"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ts_change_passw.ckey", "simple@pas#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    storage->createNote(
            {
                    .site = "target.site",
                    .login = "@wePers@n1",
                    .passw = "12$3",
                    .description = "mock createNote description",
                    .color = VIOLET,
            });

    auto createNote = storage->createNote(
            {
                    .site = "login.company.vd",
                    .login = "sect@d1v",
                    .passw = "$3$#",
                    .description = "_"
            });
    createNote->passw = "J23";
    storage->setNote(*createNote);

    createNote->passw = "321";
    storage->setNote(*createNote);

    storage->createOtpNotes("otpauth://hotp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example");
    auto createOtpNote = storage->createOtpNotes("otpauth://totp/sha1Issuer%3Asimple%40test.com"
                                                 "?secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A"
                                                 "&issuer=sha1Issuer", TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = PINK;
    storage->setOtpNote(createOtpNote);

    createOtpNote = storage->createOtpNotes("otpauth://yaotp/user@yandex.ru"
                                            "?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user",
                                            TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = ORANGE;
    createOtpNote.pin = "1234";
    storage->setOtpNote(createOtpNote);


    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH), 6));
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH | SCHEME_SPACE_SYMBOL), 8)
    );
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS), 8)
    );


    // WHEN
    storage->saveNewPassw("ts_change_passw.ckey", "newpassw");
    storage.reset();//close storage


    // THEN
    storage = thekey_v2::storage("ts_change_passw.ckey", "simple@pas#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    const auto &notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(2, notes.size());

    auto note = notes.begin();
    ASSERT_NE("target.site", note->site);
    ASSERT_NE("@wePers@n1", note->login);
    ASSERT_NE("12$3", note->passw);
    ASSERT_NE("mock createNote description", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(VIOLET, note->color);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    note++;
    ASSERT_NE("login.company.vd", note->site);
    ASSERT_NE("sect@d1v", note->login);
    ASSERT_NE("321", note->passw);
    ASSERT_NE("_", note->description);
    ASSERT_EQ(2, note->history.size());
    ASSERT_EQ(NOCOLOR, note->color);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << createNote->genTime << endl;
    auto noteHist = note->history.begin();
    ASSERT_NE("J23", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(NOCOLOR, noteHist->color);

    noteHist++;
    ASSERT_NE("$3$#", noteHist->passw);
    ASSERT_EQ(NOCOLOR, noteHist->color);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);


    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(3, otpNotes.size());

    auto otpNote = otpNotes.begin();
    auto otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_NE("alice@google.com", otpNote->name);
    ASSERT_NE("Example", otpNote->issuer);
    ASSERT_NE("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(NOCOLOR, otpNote->color);
    ASSERT_EQ("", otpNote->pin) << "if pin empty, should be empty every where" << endl;


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_NE("simple@test.com", otpNote->name);
    ASSERT_NE("sha1Issuer", otpNote->issuer);
    ASSERT_NE("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(PINK, otpNote->color);
    ASSERT_EQ("", otpNote->pin) << "if pin empty, should be empty every where" << endl;


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->notePtr);
    ASSERT_NE("user@yandex.ru", otpNote->name);
    ASSERT_NE("yandex.ru", otpNote->issuer);
    ASSERT_NE("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(otpInfo.secret, true))
                                << "yaotp should truncate to 16. Not validate use"
                                << endl;
    ASSERT_EQ(ORANGE, otpNote->color);
    ASSERT_NE("1234", otpNote->pin);


    auto genHist = storage->genPasswHistoryList(TK2_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPassw.size(), genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPassw.begin();
    for (int i = 0; i < expectedPassw.size(); ++i) {
        ASSERT_NE(*expectGenPasswIt, genHistIt->passw) << "index " << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index " << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }

}

