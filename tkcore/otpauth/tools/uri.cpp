//
// Created by panda on 10.02.24.
//

#include "key_core.h"
#include "uri.h"
#include <regex>

using namespace std;

template<typename ITERATOR>
struct FoundSymbol {
    ITERATOR it;
    string symbol;
    int length;
};

template<typename ITERATOR>
static FoundSymbol<ITERATOR> findSymbol(ITERATOR first, ITERATOR last, vector<string> variants) {
    for (const auto &symbol: variants) {
        auto index = search(first, last, symbol.begin(), symbol.end());
        if (index != last) {
            return {
                    .it = index,
                    .symbol = symbol,
                    .length = int(symbol.length())
            };
        }
    }
    return {.it = last};
}

uri::uri(const std::string &url_s) {
    auto prot = findSymbol(url_s.begin(), url_s.end(), {"://"});
    if (prot.it == url_s.end()) return;
    auto index = prot.it;
    scheme.assign(url_s.begin(), index);
    index += prot.symbol.length();

    auto atSign = findSymbol(index, url_s.end(), {"@", "%40"});
    if (atSign.it != url_s.end()) {
        auto typeLast = find(index, atSign.it, '/');
        auto issuerLast = findSymbol(typeLast, atSign.it, {":", "%3A"});
        if (typeLast > index) {
            type.assign(index, typeLast);
            type = decodeURIComponent(type);
        }
        if (issuerLast.it != atSign.it && issuerLast.it > typeLast + 1) {
            issuer.assign(typeLast + 1, issuerLast.it);
            issuer = decodeURIComponent(issuer);
        }

        if (issuerLast.it == atSign.it) {
            accountName.assign(typeLast + 1, atSign.it);
            accountName = decodeURIComponent(accountName);
        } else if (atSign.it > issuerLast.it + issuerLast.length) {
            accountName.assign(issuerLast.it + issuerLast.length, atSign.it);
            accountName = decodeURIComponent(accountName);
        }
        index = atSign.it + atSign.length;
    }

    auto pathFirst = find(index, url_s.end(), '/');
    auto queryFirst = find(index, url_s.end(), '?');
    auto domainLast = min({pathFirst, queryFirst, url_s.end()});
    host.assign(index, domainLast);
    host = decodeURIComponent(host);

    auto pathLast = min({queryFirst, url_s.end()});
    if (domainLast == url_s.end()) return;
    index = domainLast + 1;
    if (index == url_s.end()) return;
    if (index < pathLast) {
        path.assign(index, pathLast);
        path = decodeURIComponent(path);
    }

    index = queryFirst;
    while (index != url_s.end()) {
        index += 1;
        auto queryLast = find(index, url_s.end(), '&');

        auto queryEqIndex = find(index, queryLast, '=');
        string key;
        string value;
        key.assign(index, queryEqIndex);
        value.assign(queryEqIndex + 1, queryLast);
        query.insert({decodeURIComponent(key), decodeURIComponent(value)});
        index = queryLast;
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