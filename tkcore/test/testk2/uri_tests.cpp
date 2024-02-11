//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include <regex>
#include "tools/uri.h"

using namespace std;


TEST(URITests, ShortUri) {
    // When
    uri u("https://some.google.com");


    // Then
    ASSERT_EQ("https", u.scheme);
    ASSERT_EQ("", u.issuer);
    ASSERT_EQ("", u.accountName);
    ASSERT_EQ("some.google.com", u.host);
    ASSERT_EQ("", u.path);
    ASSERT_TRUE(u.query.empty());
}


TEST(URITests, PathUri) {
    // When
    uri u("htt://google.com/some/path");


    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("", u.issuer);
    ASSERT_EQ("", u.accountName);
    ASSERT_EQ("google.com", u.host);
    ASSERT_EQ("some/path", u.path);
    ASSERT_TRUE(u.query.empty());
}


TEST(URITests, Query) {
    // When
    uri u("htt://rr.com?q=2&r=QW");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("", u.issuer);
    ASSERT_EQ("", u.accountName);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("2", u.query["q"]);
    ASSERT_EQ("QW", u.query["r"]);
    ASSERT_EQ("", u.query["3"]);
}


TEST(URITests, ShortPathQuery) {
    // When
    uri u("htt://rr.com/some?q=2&r=QW");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("", u.issuer);
    ASSERT_EQ("", u.accountName);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("some", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("2", u.query["q"]);
    ASSERT_EQ("QW", u.query["r"]);
    ASSERT_EQ("", u.query["3"]);
}

TEST(URITests, PathQuery) {
    // When
    uri u("htt://rr.com/some/Path/?q=2&r=QW");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("", u.issuer);
    ASSERT_EQ("", u.accountName);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("some/Path/", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("2", u.query["q"]);
    ASSERT_EQ("QW", u.query["r"]);
    ASSERT_EQ("", u.query["3"]);
}

TEST(URITests, InvalidQuery) {
    // When
    uri u("htt://rr.com/some/Path/?q=");

    // Then
    ASSERT_EQ("htt", u.scheme);
    ASSERT_EQ("", u.issuer);
    ASSERT_EQ("", u.accountName);
    ASSERT_EQ("rr.com", u.host);
    ASSERT_EQ("some/Path/", u.path);
    ASSERT_EQ(1, u.query.size());
    ASSERT_EQ("", u.query["q"]);
    ASSERT_EQ("", u.query["3"]);
}


TEST(URITests, otpauthExampe) {
    // When
    uri u("otpauth://totp/Example:alice@google.com?secretBase32=JBSWY3DPEHPK3PXP&issuer=Example");

    // Then
    ASSERT_EQ("otpauth", u.scheme);
    ASSERT_EQ("totp", u.type);
    ASSERT_EQ("Example", u.issuer);
    ASSERT_EQ("alice", u.accountName);
    ASSERT_EQ("google.com", u.host);
    ASSERT_EQ("", u.path);
    ASSERT_EQ(2, u.query.size());
    ASSERT_EQ("JBSWY3DPEHPK3PXP", u.query["secretBase32"]);
    ASSERT_EQ("Example", u.query["issuer"]);
}


TEST(URITests, htopTest) {
    // When
    uri u("otpauth://totp/someIssuers%3Asome%40mail.rd?secretBase32=AWL27BVMCJRD6CF5H6NLC7EAGH52TZKCZU6QCYG7UZQPVL3C3FOY5R7NIXIXVA6CRQCZXC3XMWVW7A3X36LDYWG2WME7HVKSVFYD6PI&issuer=someIssuers&period=40");

    // Then
    ASSERT_EQ("otpauth", u.scheme);
    ASSERT_EQ("totp", u.type);
    ASSERT_EQ("someIssuers", u.issuer);
    ASSERT_EQ("some", u.accountName);
    ASSERT_EQ("mail.rd", u.host);
    ASSERT_EQ("", u.path);
    ASSERT_EQ(3, u.query.size());
    ASSERT_EQ("AWL27BVMCJRD6CF5H6NLC7EAGH52TZKCZU6QCYG7UZQPVL3C3FOY5R7NIXIXVA6CRQCZXC3XMWVW7A3X36LDYWG2WME7HVKSVFYD6PI", u.query["secretBase32"]);
    ASSERT_EQ("someIssuers", u.query["issuer"]);
    ASSERT_EQ("40", u.query["period"]);
}
