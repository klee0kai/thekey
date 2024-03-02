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

using namespace std;
using namespace thekey_v1;
using namespace key_salt;

static fs::path storageV1CollectionFolder =
        fs::current_path()
                .parent_path()
                .parent_path()
                .parent_path()
                .parent_path()
        / "somedata/storage_v1";

TEST(ReadStorageV1, CheckFolder) {
    cout << "storageV1CollectionFolder = " << storageV1CollectionFolder << endl;
    ASSERT_FALSE(fs::is_empty(storageV1CollectionFolder))
                                << "storageV1CollectionFolder is empty "
                                << storageV1CollectionFolder << endl;
}

TEST(ReadStorage1, ReadStorage) {
    // WHEN
    auto storage = thekey_v1::storage(storageV1CollectionFolder / "ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    ASSERT_EQ("ts_v1", storage->info().name);
    ASSERT_EQ("test_storage_version_1", storage->info().description);

    auto notesPtrs = storage->notes();
    ASSERT_EQ(3, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(1, note->history.size());
    ASSERT_EQ("simpplepassw", note->history.front().passw);
    ASSERT_EQ(1709227406, note->genTime);
    ASSERT_EQ(1709227406, note->history.front().genTime);


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_PASSWORD | TK1_GET_NOTE_INFO);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_EQ(1709227406, note->genTime);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(3, genHist.size());

    auto genHistIt = genHist.begin();
    ASSERT_EQ("2296", genHistIt->passw);
    ASSERT_EQ(1709227406, genHistIt->genTime);

    genHistIt++;
    ASSERT_EQ("8719", genHistIt->passw);
    ASSERT_EQ(1709227406, genHistIt->genTime);

    genHistIt++;
    ASSERT_EQ("3020", genHistIt->passw);
    ASSERT_EQ(1709227406, genHistIt->genTime);
}


TEST(ReadStorage1, ReadStorageIcorrectPassw) {
    // WHEN
    auto storage = thekey_v1::storage(storageV1CollectionFolder / "ts_v1.ckey", "wrongpassw");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // THEN
    ASSERT_EQ("ts_v1", storage->info().name);
    ASSERT_EQ("test_storage_version_1", storage->info().description);

    auto notesPtrs = storage->notes();
    ASSERT_EQ(3, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("somesite.com", note->site);
    ASSERT_NE("some_user_login", note->login);
    ASSERT_NE("new_passw", note->passw);
    ASSERT_NE("somesite_desc", note->description);
    ASSERT_EQ(1, note->history.size());
    ASSERT_NE("simpplepassw", note->history.front().passw);
    ASSERT_EQ(1709227406, note->genTime);
    ASSERT_EQ(1709227406, note->history.front().genTime);


    note = storage->note(notesPtrs[1].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("site_2.vd.rv", note->site);
    ASSERT_NE("user_super_login", note->login);
    ASSERT_NE("@31!!12@", note->passw);
    ASSERT_NE("is a description @ about site", note->description);
    ASSERT_EQ(1709227406, note->genTime);
    ASSERT_EQ(0, note->history.size());

    auto genHist = storage->genPasswHistoryList(TK1_GET_NOTE_HISTORY_FULL);
    ASSERT_EQ(3, genHist.size());

    auto genHistIt = genHist.begin();
    ASSERT_NE("2296", genHistIt->passw);
    ASSERT_EQ(1709227406, genHistIt->genTime);

    genHistIt++;
    ASSERT_NE("8719", genHistIt->passw);
    ASSERT_EQ(1709227406, genHistIt->genTime);

    genHistIt++;
    ASSERT_NE("3020", genHistIt->passw);
    ASSERT_EQ(1709227406, genHistIt->genTime);
}
