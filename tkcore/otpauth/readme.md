# TOTP

One-time passcode generators

# Uri format

URI:
`otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30`

# Refs

- [RFC2104: HMAC: Keyed-Hashing for Message Authentication](https://datatracker.ietf.org/doc/html/rfc2104)
- [RFC4226: HMAC-Based One-time Password (HOTP)](https://datatracker.ietf.org/doc/html/rfc4226)
- [RFC6238: Time-Based One-Time Password Algorithm (TOTP)](https://datatracker.ietf.org/doc/html/rfc6238)
- [google-authenticator uri format](https://github.com/google/google-authenticator/wiki/Key-Uri-Format)
- [TOTP tests](https://github.com/google/google-authenticator-android/blob/master/javatests/com/google/android/apps/authenticator/otp/OtpProviderTest.java)