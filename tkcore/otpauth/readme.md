# TOTP

One-time passcode generators

# Uri format

URI:
`otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30`

# Refs

- [google-authenticator uri format](https://github.com/google/google-authenticator/wiki/Key-Uri-Format)
- [HMAC-Based One-time Password (HOTP) rfc4226 ](https://datatracker.ietf.org/doc/html/rfc4226)
- [Time-Based One-Time Password Algorithm (TOTP)](https://datatracker.ietf.org/doc/html/rfc6238)
- [TOTP tests](https://github.com/google/google-authenticator-android/blob/master/javatests/com/google/android/apps/authenticator/otp/OtpProviderTest.java)