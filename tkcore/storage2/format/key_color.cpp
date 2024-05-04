//
// Created by panda on 24.02.24.
//

#include "key_color.h"
#include "key_core.h"

using namespace std;

std::string thekey_v2::to_string(thekey_v2::KeyColor color) {
    switch (color) {
        case VIOLET:
            return "violet";
        case TURQUOISE:
            return "turquoise";
        case PINK:
            return "pink";
        case ORANGE:
            return "orange";
        case CORAL:
            return "coral";

        default:
        case NOCOLOR:
            return "no_color";
    }
}