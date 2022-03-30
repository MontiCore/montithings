/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

namespace montithings {
    namespace collections {
        template<typename key>
        class set
        {
        private:
            std::set<key> internalSet;

        public:

            set(const std::set<key> &internalSet) : internalSet(internalSet) {}

            const std::set<key> &
            getInternalSet () const
            {
                return internalSet;
            }

        public:
            bool
            add (key e)
            {
                return internalSet.insert(e).second;
            };

            bool
            addAll (set<key> c)
            {
                bool changed = false;
                for (auto elem : c.getInternalSet())
                {
                    if (add(elem)) {
                        changed = true;
                    }
                }
                return changed;
            };

            void
            clear ()
            {
                internalSet.clear();
            };

            bool
            contains (key e)
            {
                return internalSet.find(e) != internalSet.end();
            };

            bool
            containsAll (set<key> c)
            {
                for (auto elem : c.getInternalSet())
                {
                    if (!contains(elem)) {
                        return false;
                    }
                }
                return true;
            };

            bool
            equals (set<key> c)
            {
                return internalSet == c.getInternalSet();
            };

            bool
            isEmpty ()
            {
                return internalSet.empty();
            };

            bool
            remove (key e)
            {
                return internalSet.erase(e);
            };

            bool
            removeAll (set<key> c)
            {
                bool changed = false;
                for (auto elem : c.getInternalSet())
                {
                    if (remove(elem)) {
                        changed = true;
                    }
                }
                return changed;
            }

            bool
            retainAll (set<key> c)
            {
                set<key> l(std::set<key> ({}));
                for (auto elem : internalSet)
                {
                    if (!c.contains(elem)) {
                        l.add(elem);
                    }
                }
                removeAll(l);
                return true;
            };

            int
            size ()
            {
                return internalSet.size();
            };

            friend std::ostream &operator<<(std::ostream &os, const set &set) {
                os << "{";
                bool first = true;
                for (auto elem : set.getInternalSet()) {
                    if (first) {
                        first = false;
                        os << elem;
                    }
                    else {
                        os << ", " << elem;
                    }
                }
                return os << "}";
            }
        };
    }
}