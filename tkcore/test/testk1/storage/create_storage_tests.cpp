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
#define TIME_TOLERANCE 30

using namespace std;
using namespace thekey_v1;
using namespace key_salt;

static std::list<string> generatedPassws{};
static auto now = time(NULL);

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

    generatedPassws.push_back(storage->genPassw(4));
    generatedPassws.push_back(storage->genPassw(4));
    generatedPassws.push_back(storage->genPassw(4));

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0], 1);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);

    auto genHist = storage->genPasswHist();
    ASSERT_EQ(3, genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = generatedPassws.begin();
    ASSERT_EQ(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);

    genHistIt++;
    expectGenPasswIt++;
    ASSERT_EQ(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);

    genHistIt++;
    expectGenPasswIt++;
    ASSERT_EQ(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);
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
        auto note = storage->note(notesPtrs[0], 1);
        note->passw = "new_passw";
        storage->setNote(notesPtrs[0], *note);
    }

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(2, notesPtrs.size());

    auto note = storage->note(notesPtrs[0], 1);
    auto noteHist = storage->noteHist(notesPtrs[0]);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->histLen);
    ASSERT_EQ("simpplepassw", noteHist.front().passw);
    ASSERT_TRUE(noteHist.front().genTime - now < TIME_TOLERANCE);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);

    auto genHist = storage->genPasswHist();
    ASSERT_EQ(3, genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = generatedPassws.begin();
    ASSERT_EQ(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);

    genHistIt++;
    expectGenPasswIt++;
    ASSERT_EQ(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);

    genHistIt++;
    expectGenPasswIt++;
    ASSERT_EQ(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);
}


// RUN AFTER Storage1::EditPassw
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
    auto noteHist = storage->noteHist(notesPtrs[0]);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->histLen);
    ASSERT_EQ("simpplepassw", noteHist.front().passw);
    ASSERT_TRUE(noteHist.front().genTime - now < TIME_TOLERANCE);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);

    ASSERT_EQ(3, storage->genPasswHist().size());
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

    auto note = storage->note(notesPtrs[0], 1);
    auto noteHist = storage->noteHist(notesPtrs[0]);
    ASSERT_NE("somesite.com", note->site);
    ASSERT_NE("some_user_login", note->login);
    ASSERT_NE("new_passw", note->passw);
    ASSERT_NE("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->histLen);
    ASSERT_NE("simpplepassw", noteHist.front().passw);
    ASSERT_TRUE(noteHist.front().genTime - now < TIME_TOLERANCE);


    note = storage->note(notesPtrs[1], 1);
    ASSERT_NE("site_2.vd.rv", note->site);
    ASSERT_NE("user_super_login", note->login);
    ASSERT_NE("@31!!12@", note->passw);
    ASSERT_NE("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);

    auto genHist = storage->genPasswHist();
    ASSERT_EQ(3, genHist.size());

    auto genHistIt = genHist.begin();
    auto expectGenPasswIt = generatedPassws.begin();
    ASSERT_NE(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);

    genHistIt++;
    expectGenPasswIt++;
    ASSERT_NE(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);

    genHistIt++;
    expectGenPasswIt++;
    ASSERT_NE(*expectGenPasswIt, genHistIt->passw);
    ASSERT_TRUE(genHistIt->genTime - now < TIME_TOLERANCE);
}



// RUN AFTER Storage1::EditPassw
TEST(Storage1, EditStorage) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1.ckey", "somepsws");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);


    // WHEN
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
    auto noteHist = storage->noteHist(notesPtrs[0]);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("new_passw", note->passw);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(1, note->histLen);
    ASSERT_EQ("simpplepassw", noteHist.front().passw);
    ASSERT_TRUE(noteHist.front().genTime - now < TIME_TOLERANCE);

    note = storage->note(notesPtrs[1], 1);
    ASSERT_EQ("site_2.vd.rv", note->site);
    ASSERT_EQ("user_super_login", note->login);
    ASSERT_EQ("@31!!12@", note->passw);
    ASSERT_EQ("is a description @ about site", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);

    note = storage->note(notesPtrs[2], 1);
    ASSERT_EQ("new_site.su", note->site);
    ASSERT_EQ("l@g1n", note->login);
    ASSERT_EQ("12#Q21!", note->passw);
    ASSERT_EQ("unic spec user", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);

    ASSERT_EQ(3, storage->genPasswHist().size());
}