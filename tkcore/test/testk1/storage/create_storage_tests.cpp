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

static std::list<string> expectedPasswHist{};
static auto now = time(NULL);

TEST(Storage1, CreateStorage) {
    // GIVEN
    auto error = thekey_v1::createStorage(
            {
                    .file = "ts_v1.ckey",
                    .name = "ts_v1",
                    .description ="test_storage_version_1"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    auto createNote = storage->createNote();
    storage->setNote(
            {
                    .notePtr = createNote->notePtr,
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
            }
    );

    expectedPasswHist.push_back(storage->genPassw(4));
    expectedPasswHist.push_back(storage->genPassw(6, ENC_EN_NUM_SPEC_SYMBOLS));
    expectedPasswHist.push_back(storage->genPassw(16, ENC_EN_NUM_SPEC_SYMBOLS_SPACE));
    expectedPasswHist.push_back(storage->genPassw(16, ENC_EN_NUM_SPEC_SYMBOLS_SPACE));
    expectedPasswHist.push_back(storage->genPassw(16, ENC_EN_NUM_SPEC_SYMBOLS_SPACE));

    // THEN
    ASSERT_EQ("ts_v1", storage->info().name);
    ASSERT_EQ("test_storage_version_1", storage->info().description);

    auto notes = storage->notes(TK1_GET_NOTE_FULL);
    auto note = notes.begin();
    ASSERT_EQ(2, notes.size());

    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());


    note++;
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPasswHist.size(), genHist.size());
    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPasswHist.begin();
    for (int i = 0; i < expectedPasswHist.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index" << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index" << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }
}

// RUN AFTER Storage1::CreateStorage
TEST(Storage1, EditPassw) {
    // GIVEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    {
        auto notesPtrs = storage->notes();
        auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
        note->passw = "new_passw";
        storage->setNote(*note);
    }

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->history.size());
    ASSERT_EQ("simpplepassw", note->history.front().passw);
    ASSERT_TRUE(note->history.front().genTime - now < TIME_TOLERANCE);


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_PASSWORD | TK1_GET_NOTE_INFO);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPasswHist.size(), genHist.size());
    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPasswHist.begin();
    for (int i = 0; i < expectedPasswHist.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index" << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index" << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }
}


// RUN AFTER Storage1::EditPassw
TEST(Storage1, ReadStorage) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    ASSERT_EQ("ts_v1", storage->info().name);
    ASSERT_EQ("test_storage_version_1", storage->info().description);

    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->history.size());
    ASSERT_EQ("simpplepassw", note->history.front().passw);
    ASSERT_TRUE(note->history.front().genTime - now < TIME_TOLERANCE);


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPasswHist.size(), genHist.size());
    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPasswHist.begin();
    for (int i = 0; i < expectedPasswHist.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index" << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index" << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }
}


// RUN AFTER Storage1::EditPassw
TEST(Storage1, ReadStorageIcorrectPassw) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "wrongpassw");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("somesite.com", note->site);
    ASSERT_NE("some_user_login", note->login);
    ASSERT_NE("new_passw", note->passw);
    ASSERT_NE("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->history.size());
    ASSERT_NE("simpplepassw", note->history.front().passw);
    ASSERT_TRUE(note->history.front().genTime - now < TIME_TOLERANCE);

    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("site_2.vd.rv", note->site);
    ASSERT_NE("user_super_login", note->login);
    ASSERT_NE("@31!!12@", note->passw);
    ASSERT_NE("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPasswHist.size(), genHist.size());
    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPasswHist.begin();
    for (int i = 0; i < expectedPasswHist.size(); ++i) {
        ASSERT_NE(*expectGenPasswIt, genHistIt->passw) << "index" << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index" << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }
}

// RUN AFTER Storage1::EditPassw
TEST(Storage1, EditStorage) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // WHEN
    storage->createNote(
            {
                    .site = "new_site.su",
                    .login = "l@g1n",
                    .passw = "12#Q21!",
                    .description = "unic spec user",
            });


    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(3, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->history.size());
    ASSERT_EQ("simpplepassw", note->history.front().passw);
    ASSERT_TRUE(note->history.front().genTime - now < TIME_TOLERANCE);

    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    note = storage->note(notesPtrs[2].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("new_site.su", note->site);
    ASSERT_EQ("l@g1n", note->login);
    ASSERT_EQ("12#Q21!", note->passw);
    ASSERT_EQ("unic spec user", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(expectedPasswHist.size(), genHist.size());
    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = expectedPasswHist.begin();
    for (int i = 0; i < expectedPasswHist.size(); ++i) {
        ASSERT_EQ(*expectGenPasswIt, genHistIt->passw) << "index" << i << endl;;
        ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE) << "index" << i << endl;

        genHistIt++;
        expectGenPasswIt++;
    }
}