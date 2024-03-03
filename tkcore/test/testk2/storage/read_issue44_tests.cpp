//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

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


TEST(ReadIssue44, CheckFolder) {
    cout << "storageV2CollectionFolder = " << storageV2CollectionFolder << endl;
    ASSERT_FALSE(fs::is_empty(storageV2CollectionFolder))
                                << "storageV2CollectionFolder is empty "
                                << storageV2CollectionFolder << endl;
}

TEST(ReadIssue44, ReadIssue44Storage) {
    // Given
    auto storage = thekey_v2::storage(storageV2CollectionFolder / "ts_issue44.ckey", "supertest");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    const auto &notes = storage->notes();
    ASSERT_EQ(3, notes.size());

    auto note = storage->note(notes[0].notePtr, TK2_GET_NOTE_INFO);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("somesite_desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(ORANGE, note->color);
    ASSERT_TRUE(note->passw.empty()) << "read without passw";

    note = storage->note(notes[1].notePtr, TK2_GET_NOTE_INFO);
    ASSERT_EQ("testget.cv", note->site);
    ASSERT_EQ("person@email.su", note->login);
    ASSERT_EQ("desc", note->description);
    ASSERT_EQ(0, note->history.size());
    ASSERT_EQ(VIOLET, note->color);
    ASSERT_TRUE(note->passw.empty()) << "read without passw";
}
