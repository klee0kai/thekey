//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>
#include "tools/uri.h"
#include "salt_text/salt2_schema.h"
#include "tools/base32.h"

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

#define TIME_TOLERANCE 60

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

static auto now = time(NULL);
static std::list<string> expectedPasswHist{};


TEST(CreateStorage2, CreateStorage) {
    auto error = thekey_v2::createStorage(
            {
                    .file = "ts_v2.ckey",
                    .name = "simple_storage",
                    .description ="simple create and use storage"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);


    auto createNote = storage->createNote(
            {
                    .site = "somesite.com",
                    .login = "some_user_login",
                    .passw = "simpplepassw",
                    .description = "somesite_desc",
                    .color = ORANGE,
            });

    storage->createNote(
            {
                    .site = "testget.cv",
                    .login = "person@email.su",
                    .passw = "12@21QW",
                    .description = "desc",
                    .color = VIOLET,
            });

    storage->createNote(
            {
                    .site = "rty",
                    .login = "secret@dev.com",
                    .passw = "$345!@$%",
                    .description = "_"
            });

    storage->createOtpNotes("otpauth://hotp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example");
    storage->createOtpNotes("otpauth://totp/sha1Issuer%3Asimple%40test.com?"
                            "secret=WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A"
                            "&issuer=sha1Issuer");
    auto otpNote = storage->otpNotes(TK2_GET_NOTE_INFO)[1];
    otpNote.color = PINK;
    storage->setOtpNote(otpNote);

    auto createOtpNote = storage->createOtpNotes("otpauth://yaotp/user@yandex.ru"
                                                 "?secret=6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY&name=user",
                                                 TK2_GET_NOTE_INFO)
            .front();
    createOtpNote.color = ORANGE;
    createOtpNote.pin = "5239";
    storage->setOtpNote(createOtpNote);

    expectedPasswHist.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH), 6)
    );
    expectedPasswHist.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPACE_SYMBOL), 8)
    );
    expectedPasswHist.push_back(
            storage->genPassword(findSchemeByFlags(SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS), 16)
    );


    storage->save();
}


// run after TEST(CreateStorage2, CreateStorage)
TEST(CreateStorage2, ReadNotes) {
    // WHEN
    auto storage = thekey_v2::storage("ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // THEN
    const auto &notes = storage->notes();
    ASSERT_EQ(3, notes.size());

    auto note = storage->note(notes[0].notePtr, TK2_GET_NOTE_INFO);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(ORANGE, note->color);
    ASSERT_TRUE(note->genTime - now < 30)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;
    ASSERT_TRUE(note->passw.empty()) << "read without passw ";

    note = storage->note(notes[1].notePtr, TK2_GET_NOTE_INFO);
    ASSERT_EQ("testget.cv", note->site);
    ASSERT_EQ("person@email.su", note->login);
    ASSERT_EQ("desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(VIOLET, note->color);
    ASSERT_TRUE(note->genTime - now < 30)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;
    ASSERT_TRUE(note->passw.empty()) << "read without passw ";
}

// run after TEST(CreateStorage2, CreateStorage)
TEST(CreateStorage2, ReadOtpNotes) {
    // WHEN
    auto storage = thekey_v2::storage("ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO);
    ASSERT_EQ(3, otpNotes.size());

    auto otpNote = otpNotes[0];
    auto otpInfo = storage->exportOtpNote(otpNote.notePtr);
    ASSERT_EQ("alice@google.com", otpNote.name);
    ASSERT_EQ("Example", otpNote.issuer);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote.color);
    ASSERT_TRUE(otpNote.createTime - now < 30)
                                << "gen time incorrect now = " << now
                                << " createTime time " << otpNote.createTime << endl;

    otpNote = otpNotes[1];
    otpInfo = storage->exportOtpNote(otpNote.notePtr);
    ASSERT_EQ("simple@test.com", otpNote.name);
    ASSERT_EQ("sha1Issuer", otpNote.issuer);
    ASSERT_EQ("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(PINK, otpNote.color);
    ASSERT_TRUE(otpNote.createTime - now < 30)
                                << "gen time incorrect now = " << now
                                << " createTime time " << otpNote.createTime << endl;
}

// run after TEST(CreateStorage2, CreateStorage)
TEST(CreateStorage2, ReadGenHistory) {
    // WHEN
    auto storage = thekey_v2::storage("ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto genHist = storage->genPasswHistoryList(TK2_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPasswHist.size(), genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPasswHist.begin();
    for (int i = 0; i < expectedPasswHist.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index " << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index " << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }

}



// run after TEST(CreateStorage2, CreateStorage)
TEST(CreateStorage2, YaotpGenTest) {
    // GIVEN
    auto storage = thekey_v2::storage("ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    auto otpNotes = storage->otpNotes();
    ASSERT_EQ(3, otpNotes.size());
    auto yaotNote = storage->otpNote(otpNotes[2].notePtr, TK2_GET_NOTE_FULL, 1641559648L);

    //THEN
    ASSERT_EQ("umozdicq", yaotNote->otpPassw);
}

// run after TEST(CreateStorage2, CreateStorage)
TEST(CreateStorage2, totpGenTest) {
    // GIVEN
    auto storage = thekey_v2::storage("ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    auto otpNotes = storage->otpNotes();
    ASSERT_EQ(3, otpNotes.size());
    auto yaotNote = storage->otpNote(otpNotes[1].notePtr, TK2_GET_NOTE_FULL, 1707657186);

    //THEN
    ASSERT_EQ("970135", yaotNote->otpPassw);
}