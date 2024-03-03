//
// Created by panda on 29.02.24.
//

#include <gtest/gtest.h>
#include "key1.h"
#include <regex>
#include <memory>

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif
#define TIME_TOLERANCE 60

using namespace std;
using namespace thekey_v1;
using namespace key_salt;

static std::list<string> expectedPassw{};
static auto now = time(NULL);

TEST(ChangePasswStorage1, ChangePassw) {
    // GIVEN
    auto error = thekey_v1::createStorage({
                                                  .file = "ts_ch_v1.ckey",
                                                  .name = "ts_ch_v1",
                                                  .description ="change passw storage test"
                                          });
    ASSERT_FALSE(error);
    auto storage = thekey_v1::storage("ts_ch_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    storage->createNote(
            {
                    .site = "somesite.com",
                    .login = "some_user_login",
                    .passw = "simpplepassw",
                    .description = "somesite_desc",
            });

    storage->createNote(
            {
                    .site = "site_2.vd.rv",
                    .login = "user_super_login",
                    .passw = "@31!!12@",
                    .description = "is a description @ about site",
            });

    expectedPassw.push_back(storage->genPassw(4));
    expectedPassw.push_back(storage->genPassw(4));
    expectedPassw.push_back(storage->genPassw(4));

    storage.reset();

    storage = thekey_v1::storage("ts_ch_v1.ckey", "somepsws");
    storage->readAll();
    storage->saveNewPassw("ts_ch_v1.ckey", "new_passw");
    storage.reset();

    storage = thekey_v1::storage("ts_ch_v1.ckey", "new_passw");
    storage->readAll();


    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
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


// RUN AFTER ChangePasswStorage1, ChangePassw
TEST(ChangePasswStorage1, NewPassw) {
    auto storage = thekey_v1::storage("ts_ch_v1.ckey", "new_passw");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
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

// RUN AFTER ChangePasswStorage1, ChangePassw
TEST(ChangePasswStorage1, OldPassw) {
    auto storage = thekey_v1::storage("ts_ch_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("somesite.com", note->site);
    ASSERT_NE("some_user_login", note->login);
    ASSERT_NE("simpplepassw", note->passw);
    ASSERT_NE("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("site_2.vd.rv", note->site);
    ASSERT_NE("user_super_login", note->login);
    ASSERT_NE("@31!!12@", note->passw);
    ASSERT_NE("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
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
