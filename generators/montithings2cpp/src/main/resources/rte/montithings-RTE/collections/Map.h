/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once
#include <map>

namespace montithings {
    namespace collections {
        template<typename key, typename value>
        class map
        {
        private:
            std::map<key,value> internalMap;

        public:

            map(const std::map<key, value> &internalMap) : internalMap(internalMap) {}

            const std::map<key, value> &
            getInternalMap () const
            {
                return internalMap;
            }

        public:
            void
            clear ()
            {
                internalMap.clear();
            };

            bool
            containsKey (key e)
            {
                return internalMap.find(e) != internalMap.end();
            };

            bool
            containsValue (value c)
            {
                for (auto elem : internalMap) {
                    if (elem.second == c) {
                        return true;
                    }
                }
                return false;
            };

            bool
            equals (map<key, value> c)
            {
                return internalMap == c.getInternalMap();
            };

            key
            get (key e)
            {
                return (*internalMap.find(e)).second;
            };

            bool
            isEmpty ()
            {
                return internalMap.empty();
            };


            value
            put (key k, value v)
            {
                internalMap.insert({k,v});
                return v;
            };

            void
            putAll (map<key, value> m)
            {
                for (auto elem : m.getInternalMap()) {
                    internalMap.insert(elem);
                }
            };

            value
            remove (key e)
            {
                value v = get(e);
                internalMap.erase(e);
                return v;
            };

            bool
            remove (key k, value v)
            {
                if (v != get(k)) {
                    return false;
                };
                internalMap.erase(k);
                return true;
            };

            int
            size ()
            {
                return internalMap.size();
            };

            friend std::ostream &operator<<(std::ostream &os, const map &map) {
                os << "{";
                bool first = true;
                for (auto n : map.getInternalMap()) {
                    if (first) {
                        first = false;
                        os << '"' << n.first << "\" : " << n.second;
                    }
                    else {
                        os << ", " << '"' << n.first << "\" : " << n.second;
                    }
                }
                return os << "}";
            }
        };
    }
}