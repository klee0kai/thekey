//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>
#include "tools/uri.h"

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

using namespace std;
using namespace thekey_v2;
using namespace key_salt;


TEST(Storage2_SetHeader, ChangeTest1) {
    // GIVEN
    unlink("ch_header.ckey");
    auto error = thekey_v2::createStorage({
                                                  .file = "ch_header.ckey",
                                                  .name = "test_storage_v2_name",
                                                  .description ="some_storage_description"
                                          });
    ASSERT_FALSE(error);

    auto storage = thekey_v2::storage("ch_header.ckey", "supertest");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto orangeGroup = storage->createColorGroup({.color = ORANGE, .name = "orange simple"});
    auto violetGroup = storage->createColorGroup({.color = VIOLET, .name = "violet"});
    auto pinkGroup = storage->createColorGroup({.color = PINK, .name = "pink_group"});

    auto now = time(NULL);
    auto createNote = storage->createNote();
    storage->setNote(
            {
                    .id = createNote->id,
                    .site = "somesite.com",
                    .login = "some_user_login",
                    .passw = "simpplepassw",
                    .description = "somesite_desc",
                    .colorGroupId = orangeGroup->id,

            }, TK2_SET_NOTE_INFO | TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY);

    storage->createNote(
            {
                    .site = "testget.cv",
                    .login = "person@email.su",
                    .passw = "12@21QW",
                    .description = "desc",
                    .colorGroupId = violetGroup->id,
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
    otpNote.colorGroupId = pinkGroup->id;
    storage->setOtpNote(otpNote);
    storage->save();
    storage.reset();

    // WHEN
    storage = thekey_v2::storage("ch_header.ckey", "");
    storage->readAll();
    storage->setInfo(StorageInfo{.name = "new_name", .description = "new desc"});
    storage->save();
    storage.reset();


    //THEB
    storage = thekey_v2::storage("ch_header.ckey", "supertest");
    storage->readAll();
    auto info = storage->info();
    ASSERT_EQ("new_name", info.name);
    ASSERT_EQ("new desc", info.description);

    const auto &notes = storage->notes();
    ASSERT_EQ(3, notes.size());

    auto note = storage->note(notes[0].id, TK2_GET_NOTE_INFO);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(orangeGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < 30)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;
    ASSERT_TRUE(note->passw.empty()) << "read without passw ";

    note = storage->note(notes[1].id, TK2_GET_NOTE_INFO);
    ASSERT_EQ("testget.cv", note->site);
    ASSERT_EQ("person@email.su", note->login);
    ASSERT_EQ("desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_TRUE(note->genTime - now < 30)
                                << "gen time incorrect now = " << now
                                << " gen time " << note->genTime << endl;
    ASSERT_TRUE(note->passw.empty()) << "read without passw ";


    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO);
    ASSERT_EQ(2, otpNotes.size());

    otpNote = otpNotes[0];
    ASSERT_EQ("alice@google.com", otpNote.name);
    ASSERT_EQ("Example", otpNote.issuer);
    ASSERT_EQ(0, otpNote.colorGroupId);


    otpNote = otpNotes[1];
    ASSERT_EQ("simple@test.com", otpNote.name);
    ASSERT_EQ("sha1Issuer", otpNote.issuer);
    ASSERT_EQ(pinkGroup->id, otpNote.colorGroupId);

}
