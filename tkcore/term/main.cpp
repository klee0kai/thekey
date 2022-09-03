//
// Created by panda on 06.06.2020.
//
#include "cmd_processing.h"
#include "def_header.h"

int main(int argc, char **argv) {
    const char *short_options = ":hf:l:";
    const struct option long_options[] = {
            {"help",  no_argument,       NULL, 'h'},
            {"find",  optional_argument, NULL, 'f'},
            {"list",  optional_argument, NULL, 'f'},
            {"login", required_argument, NULL, 'l'},
            {NULL, 0,                    NULL, 0}
    };


    if (argc <= 1) {
        cmd_pr::printHelp();
        return 0;
    }

    char ch = 0;
    while (ch = getopt_long(argc, argv, short_options, long_options, NULL), ch != -1) {
        switch (ch) {
            case 'h':
                cmd_pr::printHelp();
                return 0;
            case 'f':
                cmd_pr::findStorages(optarg);
                return 0;
            case 'l':
                cmd_pr::login(optarg);
                return 0;
            case ':':
                switch (optopt) {
                    case 'f':
                        cmd_pr::findStorages();
                        return 0;
                    default:
                        std::cerr << "invalid option: " << (char) optopt << std::endl;
                        return EXIT_FAILURE;

                }
            default:
                std::cerr << "invalid option: " << (char) optopt << std::endl;
                return EXIT_FAILURE;
        }
    }


    return 0;
}
