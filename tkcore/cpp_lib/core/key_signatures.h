//
// Created by panda on 27.01.24.
//

#ifndef THEKEY_KEY_SIGNATURES_H
#define THEKEY_KEY_SIGNATURES_H

#define SIGNATURE_LEN 7

/**
 * unsigned char rawStorageVersion; without htonl support
 */
#define TKEY_SIGNATURE_V1 {'t', 'k', 'e', 'y', 0x2, 0x47, 0x00}
/**
 * unsigned int rawStorageVersion; with htonl support
 */
#define TKEY_SIGNATURE_V2 {'t', 'k', 'e', 'y', 0x2, 0x47, 0x02}

#endif //THEKEY_KEY_SIGNATURES_H
