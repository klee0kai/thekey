# TheKey

[![](https://github.com/klee0kai/thekey/actions/workflows/deploy_dev.yml/badge.svg)](https://github.com/klee0kai/thekey/actions/workflows/deploy_dev.yml)
[![](https://img.shields.io/badge/license-GNU_GPLv3-blue.svg?style=flat-square)](./LICENCE.md)

TheKey - Passwords master storage manager.

## How is it work 

Each storage of passwords encrypted by AES256.
To view all your passwords and logins, you use only one master password.
Master password is always correct:
    - each you new master password decrypt your storage in new case
    - you can use a false bottom for hiding you super secrets passwords in single storage with different passwords.
    - brut never find out, when brut can be stopped. We no have flag, that storage decrypted incorrect 
    - compromised encryption mechanism not compromise your storage.


## License
```
Copyright (c) 2022 Andrey Kuzubov
```
