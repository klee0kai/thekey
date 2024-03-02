//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key1.h"
#include "key2.h"
#include "k1tok2.h"
#include <regex>
#include <memory>

#define TIME_TOLERANCE 30

using namespace std;
using namespace thekey_v2;
using namespace key_salt;


TEST(MigrateK1toK2, SimpleMigrate) {
    // Given
    std::list<string> expectPassws{};
    auto now = time(NULL);

    auto error = thekey_v1::createStorage(
            {
                    .file = "ts_migrate_v1.ckey",
                    .name = "test_storage_migrate",
                    .description ="migrate from version 1 to version 2"
            });
    ASSERT_FALSE(error);

    auto srcStorage = thekey_v1::storage("ts_migrate_v1.ckey", "12#23Q1!@");
    ASSERT_TRUE(srcStorage);
    error = srcStorage->readAll();
    ASSERT_FALSE(error);

    srcStorage->createNote(
            {
                    .site = "my.old.site.rd",
                    .login = "loginUn1c",
                    .passw = "@31!@3R2",
                    .description = "first note description",
            });

    auto createNote = srcStorage->createNote(
            {
                    .site = "my.second.site",
                    .login = "secLogR",
                    .passw = "1234",
                    .description = "This is @Desc",
            });

    createNote->passw = "QWERTY";
    srcStorage->setNote(*createNote);

    createNote->passw = "!@#$QWERASDFZCXV";
    srcStorage->setNote(*createNote);


    expectPassws.push_back(srcStorage->genPassw(8));
    expectPassws.push_back(srcStorage->genPassw(10));
    expectPassws.push_back(srcStorage->genPassw(6, ENC_EN_NUM_SPEC_SYMBOLS));
    expectPassws.push_back(srcStorage->genPassw(12, ENC_EN_NUM_SPEC_SYMBOLS_SPACE));
    srcStorage->save();

    // When
    thekey_v1::migrateK1toK2(
            "ts_migrate_v1.ckey",
            "ts_migrate_v2.ckey",
            "12#23Q1!@"
    );


    // Then
    auto dstStorage = thekey_v2::storage("ts_migrate_v2.ckey", "12#23Q1!@");
    ASSERT_TRUE(dstStorage);
    error = dstStorage->readAll();
    ASSERT_FALSE(error);

    ASSERT_EQ("test_storage_migrate", dstStorage->info().name);
    ASSERT_EQ("migrate from version 1 to version 2", dstStorage->info().description);
    ASSERT_EQ(STORAGE_VER_SECOND, dstStorage->info().storageVersion);

    auto notes = dstStorage->notes(TK2_GET_NOTE_FULL);
    auto noteIt = notes.begin();
    ASSERT_EQ(2, notes.size());

    ASSERT_EQ("my.old.site.rd", noteIt->site);
    ASSERT_EQ("loginUn1c", noteIt->login);
    ASSERT_EQ("@31!@3R2", noteIt->passw);
    ASSERT_EQ("first note description", noteIt->description);
    ASSERT_TRUE(noteIt->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, noteIt->history.size());
    ASSERT_EQ(NOCOLOR, noteIt->color);

    noteIt++;
    ASSERT_EQ("my.second.site", noteIt->site);
    ASSERT_EQ("secLogR", noteIt->login);
    ASSERT_EQ("!@#$QWERASDFZCXV", noteIt->passw);
    ASSERT_EQ("This is @Desc", noteIt->description);
    ASSERT_TRUE(noteIt->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(2, noteIt->history.size());
    ASSERT_EQ(NOCOLOR, noteIt->color);
    auto histIt = noteIt->history.begin();
    ASSERT_EQ("QWERTY", histIt->passw);
    ASSERT_TRUE(histIt->genTime - now < TIME_TOLERANCE);
    histIt++;
    ASSERT_EQ("1234", histIt->passw);
    ASSERT_TRUE(histIt->genTime - now < TIME_TOLERANCE);


    auto passwHist = dstStorage->passwordsHistory(TK2_GET_NOTE_HISTORY_FULL);
    auto actualHistIt = passwHist.begin();
    auto expectHistIt = expectPassws.begin();
    ASSERT_EQ(expectPassws.size(), passwHist.size());

    for (int i = 0; i < expectPassws.size(); ++i) {
        ASSERT_EQ(*expectHistIt, actualHistIt->passw) << "index = " << i << endl;
        ASSERT_TRUE(histIt->genTime - now < TIME_TOLERANCE) << "index = " << i << endl;
        actualHistIt++;
        expectHistIt++;
    }

}


TEST(MigrateK1toK2, MigrateStoragesDirectly) {
    // Given
    std::list<string> expectPassws{};
    auto now = time(NULL);
    auto error = thekey_v1::createStorage(
            {
                    .file = "ts2_migrate_v1.ckey",
                    .name = "test_storage_migrate",
                    .description ="migrate from version 1 to version 2"
            });
    ASSERT_FALSE(error);

    auto srcStorage = thekey_v1::storage("ts2_migrate_v1.ckey", "12#23Q1!@");
    ASSERT_TRUE(srcStorage);
    error = srcStorage->readAll();
    ASSERT_FALSE(error);

    srcStorage->createNote(
            {
                    .site = "my.old.site.rd",
                    .login = "loginUn1c",
                    .passw = "@31!@3R2",
                    .description = "first note description",
            });

    auto createNote = srcStorage->createNote(
            {
                    .site = "my.second.site",
                    .login = "secLogR",
                    .passw = "1234",
                    .description = "This is @Desc",
            });

    createNote->passw = "QWERTY";
    srcStorage->setNote(*createNote);

    createNote->passw = "!@#$QWERASDFZCXV";
    srcStorage->setNote(*createNote);


    expectPassws.push_back(srcStorage->genPassw(8));
    expectPassws.push_back(srcStorage->genPassw(10));
    expectPassws.push_back(srcStorage->genPassw(6, ENC_EN_NUM_SPEC_SYMBOLS));
    expectPassws.push_back(srcStorage->genPassw(12, ENC_EN_NUM_SPEC_SYMBOLS_SPACE));
    srcStorage->save();

    thekey_v2::createStorage(
            {
                    .file = "ts2_migrate_v2.ckey",
                    .name = "test_storage_v2",
                    .description ="after migrate 2"
            });
    auto dstStorage = thekey_v2::storage("ts2_migrate_v2.ckey", "newpassw");
    ASSERT_TRUE(dstStorage);
    error = dstStorage->readAll();
    ASSERT_FALSE(error);

    // When
    thekey_v1::migrateK1toK2(*srcStorage, *dstStorage);

    srcStorage.reset();
    dstStorage.reset();

    // Then
    dstStorage = thekey_v2::storage("ts2_migrate_v2.ckey", "newpassw");
    ASSERT_TRUE(dstStorage);
    error = dstStorage->readAll();
    ASSERT_FALSE(error);

    ASSERT_EQ("test_storage_v2", dstStorage->info().name);
    ASSERT_EQ("after migrate 2", dstStorage->info().description);
    ASSERT_EQ(STORAGE_VER_SECOND, dstStorage->info().storageVersion);

    auto notes = dstStorage->notes(TK2_GET_NOTE_FULL);
    auto noteIt = notes.begin();
    ASSERT_EQ(2, notes.size());

    ASSERT_EQ("my.old.site.rd", noteIt->site);
    ASSERT_EQ("loginUn1c", noteIt->login);
    ASSERT_EQ("@31!@3R2", noteIt->passw);
    ASSERT_EQ("first note description", noteIt->description);
    ASSERT_TRUE(noteIt->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, noteIt->history.size());
    ASSERT_EQ(NOCOLOR, noteIt->color);

    noteIt++;
    ASSERT_EQ("my.second.site", noteIt->site);
    ASSERT_EQ("secLogR", noteIt->login);
    ASSERT_EQ("!@#$QWERASDFZCXV", noteIt->passw);
    ASSERT_EQ("This is @Desc", noteIt->description);
    ASSERT_TRUE(noteIt->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(2, noteIt->history.size());
    ASSERT_EQ(NOCOLOR, noteIt->color);
    auto histIt = noteIt->history.begin();
    ASSERT_EQ("QWERTY", histIt->passw);
    ASSERT_TRUE(histIt->genTime - now < TIME_TOLERANCE);
    histIt++;
    ASSERT_EQ("1234", histIt->passw);
    ASSERT_TRUE(histIt->genTime - now < TIME_TOLERANCE);


    auto passwHist = dstStorage->passwordsHistory(TK2_GET_NOTE_HISTORY_FULL);
    auto actualHistIt = passwHist.begin();
    auto expectHistIt = expectPassws.begin();
    ASSERT_EQ(expectPassws.size(), passwHist.size());

    for (int i = 0; i < expectPassws.size(); ++i) {
        ASSERT_EQ(*expectHistIt, actualHistIt->passw) << "index = " << i << endl;
        ASSERT_TRUE(histIt->genTime - now < TIME_TOLERANCE) << "index = " << i << endl;
        actualHistIt++;
        expectHistIt++;
    }

}