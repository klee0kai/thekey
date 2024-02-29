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


TEST(Storage1, CreateStorage) {
    // GIVEN
    auto error = thekey_v1::createStorage({
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
    auto now = time(NULL);
    auto notePtr = storage->createNote();
    storage->setNote(notePtr,
                     {
                             .site = "somesite.com",
                             .login = "some_user_login",
                             .passw = "simpplepassw",
                             .description = "somesite_desc",
                     });

    notePtr = storage->createNote();
    storage->setNote(notePtr,
                     {
                             .site = "site_2.vd.rv",
                             .login = "user_super_login",
                             .passw = "@31!!12@",
                             .description = "is a description @ about site",
                     });


    storage->genPassw(4);
    storage->genPassw(4);
    storage->genPassw(4);

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());
    auto note = storage->note(notesPtrs[0], 1);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->histLen);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_EQ(0, note->histLen);

    ASSERT_EQ(3, storage->genPasswHist().size());
}


// RUN AFTER Storage1::CreateStorage
TEST(Storage1, ReadStorage) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());
    auto note = storage->note(notesPtrs[0], 1);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->histLen);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_EQ(0, note->histLen);

    ASSERT_EQ(3, storage->genPasswHist().size());
}


// RUN AFTER Storage1::CreateStorage
TEST(Storage1, ReadStorageIcorrectPassw) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "wrongpassw");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());
    auto note = storage->note(notesPtrs[0], 1);
    ASSERT_NE("somesite.com", note->site);
    ASSERT_NE("some_user_login", note->login);
    ASSERT_NE("simpplepassw", note->passw);
    ASSERT_NE("somesite_desc", note->description);
    ASSERT_EQ(0, note->histLen);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_NE("site_2.vd.rv", note->site);
    ASSERT_NE("user_super_login", note->login);
    ASSERT_NE("@31!!12@", note->passw);
    ASSERT_NE("is a description @ about site", note->description);
    ASSERT_EQ(0, note->histLen);

    ASSERT_EQ(3, storage->genPasswHist().size());
}



// RUN AFTER Storage1::CreateStorage
TEST(Storage1, EditStorage) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // WHEN
    auto now = time(NULL);
    auto notePtr = storage->createNote();
    storage->setNote(notePtr,
                     {
                             .site = "new_site.su",
                             .login = "l@g1n",
                             .passw = "12#Q21!",
                             .description = "unic spec user",
                     });


    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(3, notesPtrs.size());
    auto note = storage->note(notesPtrs[0], 1);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->histLen);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_EQ(0, note->histLen);

    note = storage->note(notesPtrs[2], 1);
    ASSERT_EQ("new_site.su", note->site);
    ASSERT_EQ("l@g1n", note->login);
    ASSERT_EQ("12#Q21!", note->passw);
    ASSERT_EQ("unic spec user", note->description);
    ASSERT_EQ(0, note->histLen);

    ASSERT_EQ(3, storage->genPasswHist().size());
}