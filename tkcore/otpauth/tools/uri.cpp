//
// Created by panda on 10.02.24.
//

#include "uri.h"
#include <regex>

using namespace std;

url::url(const std::string &url_s) {
    const string protEnd("://");
    auto protLast = search(url_s.begin(), url_s.end(), protEnd.begin(), protEnd.end());
    if (protLast == url_s.end())
        return;
    scheme.assign(url_s.begin(), protLast);
    protLast += protEnd.length();

    auto pathFirst = find(protLast, url_s.end(), '/');
    auto queryFirst = find(protLast, url_s.end(), '?');
    auto domainLast = min({pathFirst, queryFirst, url_s.end()});
    host.assign(protLast, domainLast);


    auto pathLast = min({queryFirst, url_s.end()});
    if (domainLast == url_s.end()) return;
    domainLast += 1;
    if (domainLast == url_s.end()) return;
    if (domainLast < pathLast) path.assign(domainLast, pathLast);

    while (queryFirst != url_s.end()) {
        queryFirst += 1;
        auto queryLast = find(queryFirst, url_s.end(), '&');

        auto queryEqIndex = find(queryFirst, queryLast, '=');
        string key;
        string value;
        key.assign(queryFirst, queryEqIndex);
        value.assign(queryEqIndex + 1, queryLast);
        query.insert({
                             decodeURIComponent(key),
                             decodeURIComponent(value)
                     });
        queryFirst = queryLast;
    }

}


std::string decodeURIComponent(const string &encoded) {

    string decoded = encoded;
    smatch sm;
    string haystack;
    auto r = regex("%[0-9A-F]{2}");
    auto dynamicLength = decoded.size() - 2;
    if (decoded.size() < 3) return decoded;

    for (int i = 0; i < dynamicLength; i++) {
        haystack = decoded.substr(i, 3);
        if (regex_match(haystack, sm, r)) {
            haystack = haystack.replace(0, 1, "0x");
            string rc = {(char) stoi(haystack, nullptr, 16)};
            decoded = decoded.replace(decoded.begin() + i, decoded.begin() + i + 3, rc);
        }

        dynamicLength = decoded.size() - 2;
    }

    return decoded;
}

std::string encodeURIComponent(const string &decoded) {

    ostringstream oss;
    regex r("[!'\\(\\)*-.0-9A-Za-z_~]");

    for (const char &c: decoded) {
        if (regex_match((string) {c}, r)) {
            oss << c;
        } else {
            oss << "%" << std::uppercase << std::hex << (0xff & c);
        }
    }
    return oss.str();
}