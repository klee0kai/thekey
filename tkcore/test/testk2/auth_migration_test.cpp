//
// Created by panda on 11.02.24.
//


#include <gtest/gtest.h>
#include <regex>
#include "otp.h"

using namespace std;
using namespace key_otp;

TEST(GoogleAuthMigration, ParseGoogleMigration) {
    // Given
    string gUri = "otpauth-migration://offline?data=CnAKQNBWsFbqalcmeYcNPbUzdQ4%2BQgdPoy67EaRDeucojYGD9a6KpY7C"
                  "DZHPFjHy%2B%2FHFYyXDgmZTYl4rl4Q3UN8qJmgSGnNoYTFJc3N1ZXI6c2ltcGxlQHRlc3QuY29tGgpzaGExSXNzdWV"
                  "yIAEoATACCl4KQITkL%2BREEBv3HKYyRJZ7SbmzwSIhjxkyZnPelhlrsksUjjZ4buWi3YYXSBmojC4E8pss9Vo6KueN"
                  "9djYlaB%2B3TMSFGVtcGxveWVlQGNvbXBhbnkuY29tIAEoATACCnQKQHq3x3Fr3Qvf2MDeoTQPnqi0Zx0NbB8PUVhAW"
                  "YNDDURsULZsFMM7MQjageJ2lmzLRXvK3VObd6UvOaypWvdVReUSHHNoYTUxMklzc3VlcjpzdHJvbmdAZ2VuLnNlbGYa"
                  "DHNoYTUxMklzc3VlciABKAEwAhABGAEgACj8hvKV%2F%2F%2F%2F%2F%2F8B";

    // When
    list<OtpInfo> otpNotes = parseFullUri(gUri);
    auto it = otpNotes.begin();

    // Then
    ASSERT_EQ(3, otpNotes.size());

    auto otp = *(it++);
    ASSERT_EQ("sha1Issuer", otp.issuer);
    ASSERT_EQ("simple@test.com", otp.name);
    ASSERT_EQ(TOTP, otp.method);
    ASSERT_EQ(SHA1, otp.algorithm);
    ASSERT_EQ(30, otp.interval);
    ASSERT_EQ(6, otp.digits);

    otp = *(it++);
    ASSERT_EQ("", otp.issuer);
    ASSERT_EQ("employee@company.com", otp.name);
    ASSERT_EQ(TOTP, otp.method);
    ASSERT_EQ(SHA1, otp.algorithm);
    ASSERT_EQ(30, otp.interval);
    ASSERT_EQ(6, otp.digits);

    otp = *(it++);
    ASSERT_EQ("sha512Issuer", otp.issuer);
    ASSERT_EQ("strong@gen.self", otp.name);
    ASSERT_EQ(TOTP, otp.method);
    ASSERT_EQ(SHA1, otp.algorithm);
    ASSERT_EQ(30, otp.interval);
    ASSERT_EQ(6, otp.digits);
}


TEST(GoogleAuthMigration, GoogleAuthGenTest) {
    // Given
    string gUri = "otpauth://totp/employee%40company.com?secretBase32=QTSC7ZCECAN7OHFGGJCJM62JXGZ4CIRBR4MTEZTT32LB"
                  "S25SJMKI4NTYN3S2FXMGC5EBTKEMFYCPFGZM6VNDUKXHRX25RWEVUB7N2MY";
    list<OtpInfo> otpNotes = parseFullUri(gUri);
    auto otp = *otpNotes.begin();

    // When
    auto now = time(NULL);
    cout << "acc " << otp.name << endl;
    cout << "now " << now << endl;
    cout << "passw " << generate(otp, now) << endl;

}


TEST(GoogleAuthMigration, GoogleMigrationGenTest) {
    // Given
    string gUri = "otpauth-migration://offline?data=Cl4KQITkL%2BREEBv3HKYyRJZ7SbmzwSIhjxkyZnPelhlrsksUjjZ4buWi3YYX"
                  "SBmojC4E8pss9Vo6KueN9djYlaB%2B3TMSFGVtcGxveWVlQGNvbXBhbnkuY29tIAEoATACCnAKQNBWsFbqalcmeYcNPbUzd"
                  "Q4%2BQgdPoy67EaRDeucojYGD9a6KpY7CDZHPFjHy%2B%2FHFYyXDgmZTYl4rl4Q3UN8qJmgSGnNoYTFJc3N1ZXI6c2ltcG"
                  "xlQHRlc3QuY29tGgpzaGExSXNzdWVyIAEoATACEAEYASAAKN7XvdAB";
    list<OtpInfo> otpNotes = parseFullUri(gUri);
    auto otp = *otpNotes.begin();

    // When
    auto now1 = 1707663940;
    auto now2 = 1707663965;
    auto now3 = 1707663994;

    // then
    ASSERT_EQ("045680", generate(otp, now1));
    ASSERT_EQ("716066", generate(otp, now2));
    ASSERT_EQ("503903", generate(otp, now3));
    ASSERT_EQ(
            "QTSC7ZCECAN7OHFGGJCJM62JXGZ4CIRBR4MTEZTT32LBS25SJMKI4NTYN3S2FXMGC5EBTKEMFYCPFGZM6VNDUKXHRX25RWEVUB7N2MY",
            otp.secretBase32
    );
}