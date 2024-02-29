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

    storage.reset();

    storage = thekey_v1::storage("ts_ch_v1.ckey", "somepsws");
    storage->readAll();
    storage->saveToNewPassw("ts_ch_v1.ckey", "new_passw");
    storage.reset();

    storage = thekey_v1::storage("ts_ch_v1.ckey", "new_passw");
    storage->readAll();


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



// RUN AFTER ChangePasswStorage1, ChangePassw
TEST(ChangePasswStorage1, NewPassw) {
    auto storage = thekey_v1::storage("ts_ch_v1.ckey", "new_passw");
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

// RUN AFTER ChangePasswStorage1, ChangePassw
TEST(ChangePasswStorage1, OldPassw) {
    auto storage = thekey_v1::storage("ts_ch_v1.ckey", "somepsws");
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
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->histLen);


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
