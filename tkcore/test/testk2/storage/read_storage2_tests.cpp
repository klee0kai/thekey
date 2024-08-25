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

static fs::path storageV2CollectionFolder =
        fs::current_path()
                .parent_path()
                .parent_path()
                .parent_path()
                .parent_path()
        / "somedata/storage_v2";

TEST(ReadStorageV2, CheckFolder) {
    cout << "storageV2CollectionFolder = " << storageV2CollectionFolder << endl;
    ASSERT_FALSE(fs::is_empty(storageV2CollectionFolder))
                                << "storageV2CollectionFolder is empty "
                                << storageV2CollectionFolder << endl;
}

TEST(ReadStorage2, ReadNotes) {
    // WHEN
    auto storage = thekey_v2::storage(storageV2CollectionFolder / "ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // THEN
    const auto &groups = storage->colorGroups(TK2_GET_NOTE_INFO);
    auto orangeGroup = std::find_if(groups.begin(), groups.end(), [](const DecryptedColorGroup &it) {
        return it.color == ORANGE;
    });
    auto violetGroup = std::find_if(groups.begin(), groups.end(), [](const DecryptedColorGroup &it) {
        return it.color == VIOLET;
    });
    auto pinkGroup = std::find_if(groups.begin(), groups.end(), [](const DecryptedColorGroup &it) {
        return it.color == PINK;
    });

    const auto &notes = storage->notes();
    ASSERT_EQ(3, notes.size());

    auto note = storage->note(notes[0].id, TK2_GET_NOTE_INFO);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(orangeGroup->id, note->colorGroupId);
    ASSERT_EQ(1724597097, note->genTime);
    ASSERT_TRUE(note->passw.empty()) << "read without passw ";

    note = storage->note(notes[1].id, TK2_GET_NOTE_INFO);
    ASSERT_EQ("testget.cv", note->site);
    ASSERT_EQ("person@email.su", note->login);
    ASSERT_EQ("desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(violetGroup->id, note->colorGroupId);
    ASSERT_EQ(1724597100, note->genTime);
    ASSERT_TRUE(note->passw.empty()) << "read without passw ";
}

TEST(ReadStorage2, ReadOtpNotes) {
    // WHEN
    auto storage = thekey_v2::storage(storageV2CollectionFolder / "ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    const auto &groups = storage->colorGroups(TK2_GET_NOTE_INFO);
    auto pinkGroup = std::find_if(groups.begin(), groups.end(), [](const DecryptedColorGroup &it) {
        return it.color == PINK;
    });

    auto otpNotes = storage->otpNotes(TK2_GET_NOTE_INFO);
    ASSERT_EQ(3, otpNotes.size());

    auto otpNote = otpNotes[0];
    auto otpInfo = storage->exportOtpNote(otpNote.id);
    ASSERT_EQ("alice@google.com", otpNote.name);
    ASSERT_EQ("Example", otpNote.issuer);
    ASSERT_EQ("JBSWY3DPEHPK3PXP", base32::encode(otpInfo.secret, true));
    ASSERT_EQ(0, otpNote.colorGroupId);
    ASSERT_EQ(1724597103, otpNote.createTime);


    otpNote = otpNotes[1];
    otpInfo = storage->exportOtpNote(otpNote.id);
    ASSERT_EQ("simple@test.com", otpNote.name);
    ASSERT_EQ("sha1Issuer", otpNote.issuer);
    ASSERT_EQ("WDW2ZCDQYHFXYV4G7WB6FG2WNBXKEGUJRW3QLE634JP43J4TCGTCPCKAAVISY6A7BNKYULEUXQ5YC2JPG7QXFFMDRIRJMESQNYWZ72A",
              base32::encode(otpInfo.secret, true));
    ASSERT_EQ(pinkGroup->id, otpNote.colorGroupId);
    ASSERT_EQ(1724597105, otpNote.createTime);

}

TEST(ReadStorage2, ReadGenHistory) {
    // WHEN
    auto storage = thekey_v2::storage(storageV2CollectionFolder / "ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto genHist = storage->genPasswHistoryList(TK2_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(3, genHist.size());

    auto genHistIt = genHist.begin();
    ASSERT_EQ("i2IU1a", genHistIt->passw);
    ASSERT_EQ(1724597106, genHistIt->genTime);

    genHistIt++;
    ASSERT_EQ("|(E9Q-Q=", genHistIt->passw);
    ASSERT_EQ(1724597106, genHistIt->genTime);

    genHistIt++;
    ASSERT_EQ("?z/}m03e!p.`wl^\"", genHistIt->passw);
    ASSERT_EQ(1724597106, genHistIt->genTime);
}

TEST(ReadStorage2, YaotpGenTest) {
    // GIVEN
    auto storage = thekey_v2::storage(storageV2CollectionFolder / "ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    auto otpNotes = storage->otpNotes();
    ASSERT_EQ(3, otpNotes.size());
    auto yaotNote = storage->otpNote(otpNotes[2].id, TK2_GET_NOTE_FULL, 1641559648L);

    //THEN
    ASSERT_EQ("umozdicq", yaotNote->otpPassw);
}

TEST(ReadStorage2, totpGenTest) {
    // GIVEN
    auto storage = thekey_v2::storage(storageV2CollectionFolder / "ts_v2.ckey", "simpletest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    auto otpNotes = storage->otpNotes();
    ASSERT_EQ(3, otpNotes.size());
    auto yaotNote = storage->otpNote(otpNotes[1].id, TK2_GET_NOTE_FULL, 1707657186);

    //THEN
    ASSERT_EQ("970135", yaotNote->otpPassw);
}