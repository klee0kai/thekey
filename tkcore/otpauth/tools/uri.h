//
// Created by panda on 10.02.24.
//

#ifndef THEKEY_URI_H
#define THEKEY_URI_H

#include <string>
#include <map>

struct url {
public:
    url(const std::string &url_s);

    std::string scheme, host, path;
    std::map<std::string, std::string> query;
};


std::string encodeURIComponent(const std::string &decoded);

std::string decodeURIComponent(const std::string &encoded);

#endif //THEKEY_URI_H
