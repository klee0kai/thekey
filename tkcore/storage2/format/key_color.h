//
// Created by panda on 24.02.24.
//

#ifndef THEKEY_KEY_COLOR_H
#define THEKEY_KEY_COLOR_H

#include <iostream>

namespace thekey_v2 {

    enum KeyColor {
        NOCOLOR = 0,
        VIOLET = 1,
        TURQUOISE = 2,
        PINK = 3,
        ORANGE = 4,
        CORAL = 5
    };

    std::string to_string(KeyColor color);

}

#endif //THEKEY_KEY_COLOR_H
