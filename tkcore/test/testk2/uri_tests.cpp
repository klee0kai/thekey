//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include <regex>
#include "tools/uri.h"

using namespace std;


TEST(URITests, ShortUri) {
    // When
    url u("https://some.google.com");


    // Then
    ASSERT_EQ("https", u.scheme);
    ASSERT_EQ("some.google.com", u.host);
    ASSERT_EQ("", u.path);
    ASSERT_TRUE(u.query.empty());
}


TEST(URITests, PathUri) {
    // When
    url u("htt://google.com/some/path");


    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("google.com", u.host);
    ASSERT_EQ("some/path", u.path);
    ASSERT_TRUE(u.query.empty());
}


TEST(URITests, Query) {
    // When
    url u("htt://rr.com?q=2&r=QW");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("2", u.query["q"]);
    ASSERT_EQ("QW", u.query["r"]);
    ASSERT_EQ("", u.query["3"]);
}


TEST(URITests, ShortPathQuery) {
    // When
    url u("htt://rr.com/some?q=2&r=QW");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("some", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("2", u.query["q"]);
    ASSERT_EQ("QW", u.query["r"]);
    ASSERT_EQ("", u.query["3"]);
}

TEST(URITests, PathQuery) {
    // When
    url u("htt://rr.com/some/Path/?q=2&r=QW");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("some/Path/", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("2", u.query["q"]);
    ASSERT_EQ("QW", u.query["r"]);
    ASSERT_EQ("", u.query["3"]);
}

TEST(URITests, InvalidQuery) {
    // When
    url u("htt://rr.com/some/Path/?q=");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("some/Path/", u.path);
    ASSERT_EQ(1, u.query.size());
    ASSERT_EQ("", u.query["q"]);
    ASSERT_EQ("", u.query["3"]);
}