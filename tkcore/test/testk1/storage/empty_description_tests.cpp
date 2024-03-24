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

static auto now = time(NULL);

bool utf8_check_is_valid(const std::string &str) {
    int n;
    auto len = str.length();
    for (int i = 0; i < len; ++i) {
        unsigned char c = (unsigned char) str[i];
        if (0x00 <= c && c <= 0x7f) {
            n = 0; // 0bbbbbbb
        } else if ((c & 0xE0) == 0xC0) {
            n = 1; // 110bbbbb
        } else if (c == 0xed && i < (len - 1) && ((unsigned char) str[i + 1] & 0xa0) == 0xa0) {
            return false; //U+d800 to U+dfff
        } else if ((c & 0xF0) == 0xE0) {
            n = 2; // 1110bbbb
        } else if ((c & 0xF8) == 0xF0) {
            n = 3; // 11110bbb
        } else {
            return false;
        }

        for (int j = 0; j < n && i < len; ++j) { // n bytes matching 10bbbbbb follow ?
            if ((++i == len) || (((unsigned char) str[i] & 0xC0) != 0x80)) {
                return false;
            }
        }
    }
    return true;
}

TEST(Storage1EmptyDescr, CreateStorage) {
    // GIVEN
    auto error = thekey_v1::createStorage(
            {
                    .file = "ts_v1_empty_desc.ckey",
                    .name = "empty_desc_tests",
                    .description ="empty_desc_tests"
            });
    ASSERT_FALSE(error);

    auto storage = thekey_v1::storage("ts_v1_empty_desc.ckey", "1234");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    // WHEN
    auto createNote = storage->createNote(
            {
                    .site = "somesite.com",
                    .login = "some_user_login",
                    .passw = "simpplepassw",
                    .description = "",
            }
    );

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(1, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_EQ("somesite.com", note->site);
    ASSERT_EQ("some_user_login", note->login);
    ASSERT_EQ("simpplepassw", note->passw);
    ASSERT_TRUE(utf8_check_is_valid(note->description)) << note->description;
    ASSERT_EQ("", note->description);
    ASSERT_TRUE(note->genTime - now < TIME_TOLERANCE);
    ASSERT_EQ(0, note->history.size());

}

// RUN AFTER Storage1EmptyDescr::CreateStorage
TEST(Storage1EmptyDescr, WrongPassw) {
    // WHEN
    auto storage = thekey_v1::storage("ts_v1_empty_desc.ckey", "test");
    ASSERT_TRUE(storage);
    auto error = storage->readAll();
    ASSERT_FALSE(error);

    // THEN
    auto notesPtrs = storage->notes();
    ASSERT_EQ(1, notesPtrs.size());

    auto note = storage->note(notesPtrs[0].notePtr, TK1_GET_NOTE_FULL);
    ASSERT_NE("somesite.com", note->site);
    ASSERT_TRUE(utf8_check_is_valid(note->site)) << note->site;
    ASSERT_NE("some_user_login", note->login);
    ASSERT_TRUE(utf8_check_is_valid(note->passw)) << note->passw;
    ASSERT_NE("new_passw", note->passw);
    ASSERT_TRUE(utf8_check_is_valid(note->description)) << note->description;
    ASSERT_NE("", note->description);
}
