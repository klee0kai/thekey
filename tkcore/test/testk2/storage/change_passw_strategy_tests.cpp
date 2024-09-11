//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>
#include "tools/base32.h"
#include "salt_text/salt2_schema.h"

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

#define TIME_TOLERANCE 60 * 5

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

TEST(Storage2ChangePasswStrategy, ChangePasswToNewFile) {
    // GIVEN
    static std::list<string> expectedPassw{};
    auto now = time(NULL);

    auto error = thekey_v2::createStorage(
            {
                    .file = "ts_change_passw_strategy.ckey",
                    .name = "ch_passw",
                    .description ="Change Storage password using strategy"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ts_change_passw_strategy.ckey", "simple@pas#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto violetGroup = storage->createColorGroup({.color = VIOLET, .name = "violet"});
    auto pinkGroup = storage->createColorGroup({.color = PINK, .name = "pink"});
    auto orangeGroup = storage->createColorGroup({.color = ORANGE, .name = "orange"});

    auto originalNote1 = storage->createNote(
            {
                    .site = "target.site",
                    .login = "@wePers@n1",
                    .passw = "12$3",
                    .description = "mock originalNote2 description",
                    .colorGroupId = violetGroup->id,
            });

    auto originalNote2 = storage->createNote(
            {
                    .site = "login.company.vd",
                    .login = "sect@d1v",
                    .passw = "$3$#",
                    .description = "_"
            });
    originalNote2->passw = "J23";
    storage->setNote(*originalNote2, TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY);

    originalNote2->passw = "321";
    storage->setNote(*originalNote2, TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY);

    auto createOtpNote1 = storage->createOtpNotes(
            "otpauth://hotp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example").front();
    auto createOtpNote2 = storage->createOtpNotes("otpauth://totp/sha1Issuer%3Asimple%40test.com"
                                                  "?secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A"
                                                  "&issuer=sha1Issuer", TK2_GET_NOTE_INFO).front();
    createOtpNote2.colorGroupId = pinkGroup->id;
    storage->setOtpNote(createOtpNote2);

    auto createOtpNote3 = storage->createOtpNotes("otpauth://yaotp/user@yandex.ru"
                                                  "?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user",
                                                  TK2_GET_NOTE_INFO).front();
    createOtpNote3.colorGroupId = orangeGroup->id;
    createOtpNote3.pin = "1234";
    storage->setOtpNote(createOtpNote3, TK2_SET_NOTE_INFO);

    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH), 6));
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH | SCHEME_SPACE_SYMBOL), 8));
    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS), 8));
    storage->save();

    /* add new notes with second password */
    storage = thekey_v2::storage("ts_change_passw_strategy.ckey", "secPas2#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto originalNote3 = storage->createNote(
            {
                    .site = "some.site.online",
                    .login = "me@as@login",
                    .passw = "!3QEasz",
                    .description = "simple note 3",
                    .colorGroupId = violetGroup->id,
            });

    auto originalNote4 = storage->createNote(
            {
                    .site = "world.example.su",
                    .login = "hello@world@login",
                    .passw = "p@world",
                    .description = "simple note 3",
                    .colorGroupId = violetGroup->id,
            });

    auto createOtpNote4 = storage->createOtpNotes(
            "otpauth://hotp/simpleIssuer:bob@simple.su?secret=BWEQEOKWUXM6DK27&issuer=simpleIssuer").front();

    auto createOtpNote5 = storage->createOtpNotes(
            "otpauth://hotp/hello:jon@simple.su?secret=ZRFTTTVAQA5YAWN6&issuer=hello").front();
    storage->save();

    auto allNotes1 = storage->notes(TK2_GET_NOTE_FULL);

    /* add new notes with third password */
    storage = thekey_v2::storage("ts_change_passw_strategy.ckey", "secret3$");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto originalNote5 = storage->createNote(
            {
                    .site = "animal.zoo",
                    .login = "elephant",
                    .passw = "i_am_big",
                    .description = "zoo support",
                    .colorGroupId = pinkGroup->id,
            });

    auto originalNote6 = storage->createNote(
            {
                    .site = "admin.animal.zoo",
                    .login = "fox",
                    .passw = "fox@#hide",
                    .description = "admin dashboard",
                    .colorGroupId = orangeGroup->id,
            });
    storage->save();

    auto allNotes2 = storage->notes(TK2_GET_NOTE_FULL);

    // WHEN
    storage = thekey_v2::storage("ts_change_passw_strategy.ckey", "secret3$");
    /* note ids reset after readAll  */
    storage->readAll();
    auto allNotes = storage->notes();
    auto allNotesIt = allNotes.begin();
    auto allOtpNotes = storage->otpNotes();
    auto allOtpNotesIt = allOtpNotes.begin();

    auto strategy1 = StoragePasswMigrateStrategy{
            .currentPassword = "simple@pas#",
            .newPassw = "my_new_passw1",
            .isDefault = 1,
    };
    strategy1.noteIds.push_back((allNotesIt++)->id);
    strategy1.noteIds.push_back((allNotesIt++)->id);
    strategy1.otpNoteIds.push_back((allOtpNotesIt++)->id);
    strategy1.otpNoteIds.push_back((allOtpNotesIt++)->id);
    strategy1.otpNoteIds.push_back((allOtpNotesIt++)->id);

    auto strategy2 = StoragePasswMigrateStrategy{
            .currentPassword = "secPas2#",
            .newPassw = "psww_#",
    };
    strategy2.noteIds.push_back((allNotesIt++)->id);
    strategy2.noteIds.push_back((allNotesIt++)->id);
    strategy2.otpNoteIds.push_back((allOtpNotesIt++)->id);
    strategy2.otpNoteIds.push_back((allOtpNotesIt++)->id);

    auto strategy3 = StoragePasswMigrateStrategy{
            .currentPassword = "secret3$",
            .newPassw = "new_paaaw_3",
    };
    strategy3.noteIds.push_back((allNotesIt++)->id);
    strategy3.noteIds.push_back((allNotesIt++)->id);

    storage->saveNewPasswStrategy(
            "ts2_change_passw_strategy.ckey",
            {strategy1, strategy2, strategy3}
    );
    storage.reset();

    // THEN
    storage = thekey_v2::storage("ts2_change_passw_strategy.ckey", "my_new_passw1");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(6, notes.size());
    auto note = notes.begin();

    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(5, otpNotes.size());
    auto otpNote = otpNotes.begin();


    ASSERT_EQ("target.site", note->site);
    ASSERT_EQ("@wePers@n1", note->login);
    ASSERT_EQ("12$3", note->passw);
    ASSERT_EQ("mock originalNote2 description", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    note++;
    ASSERT_EQ("login.company.vd", note->site);
    ASSERT_EQ("sect@d1v", note->login);
    ASSERT_EQ("321", note->passw);
    ASSERT_EQ("_", note->description);
    ASSERT_EQ(2, note->history.size());
    ASSERT_EQ(0, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << originalNote2->genTime << endl;
    auto noteHist = note->history.begin();
    ASSERT_EQ("J23", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);

    noteHist++;
    ASSERT_EQ("$3$#", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);

    auto otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("alice@google.com", otpNote->name);
    ASSERT_EQ("Example", otpNote->issuer);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);

    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("simple@test.com", otpNote->name);
    ASSERT_EQ("sha1Issuer", otpNote->issuer);
    ASSERT_EQ("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(pinkGroup->id, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("user@yandex.ru", otpNote->name);
    ASSERT_EQ("yandex.ru", otpNote->issuer);
    ASSERT_EQ("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(otpInfo.secret, true))
                                << "yaotp should truncate to 16. Not validate use"
                                << endl;
    ASSERT_EQ(orangeGroup->id, otpNote->colorGroupId);
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


    storage = thekey_v2::storage("ts2_change_passw_strategy.ckey", "psww_#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(6, notes.size());
    note = notes.begin();

    otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(5, otpNotes.size());
    otpNote = otpNotes.begin();

    note++;
    note++;
    ASSERT_EQ("some.site.online", note->site);
    ASSERT_EQ("me@as@login", note->login);
    ASSERT_EQ("!3QEasz", note->passw);
    ASSERT_EQ("simple note 3", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;

    note++;
    ASSERT_EQ("world.example.su", note->site);
    ASSERT_EQ("hello@world@login", note->login);
    ASSERT_EQ("p@world", note->passw);
    ASSERT_EQ("simple note 3", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    otpNote++;
    otpNote++;
    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("bob@simple.su", otpNote->name);
    ASSERT_EQ("simpleIssuer", otpNote->issuer);
    ASSERT_EQ("BWEQEOKWUXM6DK27", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);

    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("jon@simple.su", otpNote->name);
    ASSERT_EQ("hello", otpNote->issuer);
    ASSERT_EQ("ZRFTTTVAQA5YAWN6", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);


    storage = thekey_v2::storage("ts2_change_passw_strategy.ckey", "new_paaaw_3");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(6, notes.size());
    note = notes.begin();
    otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(5, otpNotes.size());
    otpNote = otpNotes.begin();


    note++;
    note++;
    note++;
    note++;
    ASSERT_EQ("animal.zoo", note->site);
    ASSERT_EQ("elephant", note->login);
    ASSERT_EQ("i_am_big", note->passw);
    ASSERT_EQ("zoo support", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(pinkGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;

    note++;
    ASSERT_EQ("admin.animal.zoo", note->site);
    ASSERT_EQ("fox", note->login);
    ASSERT_EQ("fox@#hide", note->passw);
    ASSERT_EQ("admin dashboard", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(orangeGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


}

TEST(Storage2ChangePasswStrategy, ChangePasswToSameFile) {
    // GIVEN
    static std::list<string> expectedPassw{};
    auto now = time(NULL);

    auto error = thekey_v2::createStorage(
            {
                    .file = "ts_change_passw_strategy_same_file.ckey",
                    .name = "ch_passw",
                    .description ="Change Storage password using strategy"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "simple@pas#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto violetGroup = storage->createColorGroup({.color = VIOLET, .name = "violet"});
    auto pinkGroup = storage->createColorGroup({.color = PINK, .name = "pink"});
    auto orangeGroup = storage->createColorGroup({.color = ORANGE, .name = "orange"});

    auto originalNote1 = storage->createNote(
            {
                    .site = "target.site",
                    .login = "@wePers@n1",
                    .passw = "12$3",
                    .description = "mock originalNote2 description",
                    .colorGroupId = violetGroup->id,
            });

    auto originalNote2 = storage->createNote(
            {
                    .site = "login.company.vd",
                    .login = "sect@d1v",
                    .passw = "$3$#",
                    .description = "_"
            });
    originalNote2->passw = "J23";
    storage->setNote(*originalNote2, TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY);

    originalNote2->passw = "321";
    storage->setNote(*originalNote2, TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY);

    auto createOtpNote1 = storage->createOtpNotes(
            "otpauth://hotp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example").front();
    auto createOtpNote2 = storage->createOtpNotes("otpauth://totp/sha1Issuer%3Asimple%40test.com"
                                                  "?secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A"
                                                  "&issuer=sha1Issuer", TK2_GET_NOTE_INFO).front();
    createOtpNote2.colorGroupId = pinkGroup->id;
    storage->setOtpNote(createOtpNote2);

    auto createOtpNote3 = storage->createOtpNotes("otpauth://yaotp/user@yandex.ru"
                                                  "?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user",
                                                  TK2_GET_NOTE_INFO).front();
    createOtpNote3.colorGroupId = orangeGroup->id;
    createOtpNote3.pin = "1234";
    storage->setOtpNote(createOtpNote3, TK2_SET_NOTE_INFO);

    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH), 6));
    expectedPassw.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH | SCHEME_SPACE_SYMBOL), 8));
    expectedPassw.push_back(storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS), 8));
    storage->save();

    /* add new notes with second password */
    storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "secPas2#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto originalNote3 = storage->createNote(
            {
                    .site = "some.site.online",
                    .login = "me@as@login",
                    .passw = "!3QEasz",
                    .description = "simple note 3",
                    .colorGroupId = violetGroup->id,
            });

    auto originalNote4 = storage->createNote(
            {
                    .site = "world.example.su",
                    .login = "hello@world@login",
                    .passw = "p@world",
                    .description = "simple note 3",
                    .colorGroupId = violetGroup->id,
            });

    auto createOtpNote4 = storage->createOtpNotes(
            "otpauth://hotp/simpleIssuer:bob@simple.su?secret=BWEQEOKWUXM6DK27&issuer=simpleIssuer").front();

    auto createOtpNote5 = storage->createOtpNotes(
            "otpauth://hotp/hello:jon@simple.su?secret=ZRFTTTVAQA5YAWN6&issuer=hello").front();
    storage->save();

    auto allNotes1 = storage->notes(TK2_GET_NOTE_FULL);

    /* add new notes with third password */
    storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "secret3$");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto originalNote5 = storage->createNote(
            {
                    .site = "animal.zoo",
                    .login = "elephant",
                    .passw = "i_am_big",
                    .description = "zoo support",
                    .colorGroupId = pinkGroup->id,
            });

    auto originalNote6 = storage->createNote(
            {
                    .site = "admin.animal.zoo",
                    .login = "fox",
                    .passw = "fox@#hide",
                    .description = "admin dashboard",
                    .colorGroupId = orangeGroup->id,
            });
    storage->save();

    auto allNotes2 = storage->notes(TK2_GET_NOTE_FULL);

    // WHEN
    storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "secret3$");
    /* note ids reset after readAll  */
    storage->readAll();
    auto allNotes = storage->notes();
    auto allNotesIt = allNotes.begin();
    auto allOtpNotes = storage->otpNotes();
    auto allOtpNotesIt = allOtpNotes.begin();

    auto strategy1 = StoragePasswMigrateStrategy{
            .currentPassword = "simple@pas#",
            .newPassw = "my_new_passw1",
            .isDefault = 1,
    };
    strategy1.noteIds.push_back((allNotesIt++)->id);
    strategy1.noteIds.push_back((allNotesIt++)->id);
    strategy1.otpNoteIds.push_back((allOtpNotesIt++)->id);
    strategy1.otpNoteIds.push_back((allOtpNotesIt++)->id);
    strategy1.otpNoteIds.push_back((allOtpNotesIt++)->id);

    auto strategy2 = StoragePasswMigrateStrategy{
            .currentPassword = "secPas2#",
            .newPassw = "psww_#",
    };
    strategy2.noteIds.push_back((allNotesIt++)->id);
    strategy2.noteIds.push_back((allNotesIt++)->id);
    strategy2.otpNoteIds.push_back((allOtpNotesIt++)->id);
    strategy2.otpNoteIds.push_back((allOtpNotesIt++)->id);

    auto strategy3 = StoragePasswMigrateStrategy{
            .currentPassword = "secret3$",
            .newPassw = "new_paaaw_3",
    };
    strategy3.noteIds.push_back((allNotesIt++)->id);
    strategy3.noteIds.push_back((allNotesIt++)->id);

    storage->saveNewPasswStrategy(
            "ts_change_passw_strategy_same_file.ckey",
            {strategy1, strategy2, strategy3}
    );
    storage.reset();

    // THEN
    storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "my_new_passw1");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(6, notes.size());
    auto note = notes.begin();

    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(5, otpNotes.size());
    auto otpNote = otpNotes.begin();


    ASSERT_EQ("target.site", note->site);
    ASSERT_EQ("@wePers@n1", note->login);
    ASSERT_EQ("12$3", note->passw);
    ASSERT_EQ("mock originalNote2 description", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    note++;
    ASSERT_EQ("login.company.vd", note->site);
    ASSERT_EQ("sect@d1v", note->login);
    ASSERT_EQ("321", note->passw);
    ASSERT_EQ("_", note->description);
    ASSERT_EQ(2, note->history.size());
    ASSERT_EQ(0, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << originalNote2->genTime << endl;
    auto noteHist = note->history.begin();
    ASSERT_EQ("J23", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);

    noteHist++;
    ASSERT_EQ("$3$#", noteHist->passw);
    ASSERT_TRUE(noteHist->genTime - now < TIME_TOLERANCE);

    auto otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("alice@google.com", otpNote->name);
    ASSERT_EQ("Example", otpNote->issuer);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);

    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("simple@test.com", otpNote->name);
    ASSERT_EQ("sha1Issuer", otpNote->issuer);
    ASSERT_EQ("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(pinkGroup->id, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);


    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("user@yandex.ru", otpNote->name);
    ASSERT_EQ("yandex.ru", otpNote->issuer);
    ASSERT_EQ("6SB2IKNM6OBZPAVBVTOHDKS4FA", base32::encode(otpInfo.secret, true))
                                << "yaotp should truncate to 16. Not validate use"
                                << endl;
    ASSERT_EQ(orangeGroup->id, otpNote->colorGroupId);
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


    storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "psww_#");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(6, notes.size());
    note = notes.begin();

    otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(5, otpNotes.size());
    otpNote = otpNotes.begin();

    note++;
    note++;
    ASSERT_EQ("some.site.online", note->site);
    ASSERT_EQ("me@as@login", note->login);
    ASSERT_EQ("!3QEasz", note->passw);
    ASSERT_EQ("simple note 3", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;

    note++;
    ASSERT_EQ("world.example.su", note->site);
    ASSERT_EQ("hello@world@login", note->login);
    ASSERT_EQ("p@world", note->passw);
    ASSERT_EQ("simple note 3", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;


    otpNote++;
    otpNote++;
    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("bob@simple.su", otpNote->name);
    ASSERT_EQ("simpleIssuer", otpNote->issuer);
    ASSERT_EQ("BWEQEOKWUXM6DK27", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);

    otpNote++;
    otpInfo = storage->exportOtpNote(otpNote->id);
    ASSERT_EQ("jon@simple.su", otpNote->name);
    ASSERT_EQ("hello", otpNote->issuer);
    ASSERT_EQ("ZRFTTTVAQA5YAWN6", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote->colorGroupId);
    ASSERT_EQ("", otpNote->pin);


    storage = thekey_v2::storage("ts_change_passw_strategy_same_file.ckey", "new_paaaw_3");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    notes = storage->notes(TK2_GET_NOTE_FULL);
    ASSERT_EQ(6, notes.size());
    note = notes.begin();
    otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    ASSERT_EQ(5, otpNotes.size());
    otpNote = otpNotes.begin();


    note++;
    note++;
    note++;
    note++;
    ASSERT_EQ("animal.zoo", note->site);
    ASSERT_EQ("elephant", note->login);
    ASSERT_EQ("i_am_big", note->passw);
    ASSERT_EQ("zoo support", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(pinkGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;

    note++;
    ASSERT_EQ("admin.animal.zoo", note->site);
    ASSERT_EQ("fox", note->login);
    ASSERT_EQ("fox@#hide", note->passw);
    ASSERT_EQ("admin dashboard", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(orangeGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;

}


