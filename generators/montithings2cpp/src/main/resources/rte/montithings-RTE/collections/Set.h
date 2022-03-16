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
                return internalSet.find(e) != std::set<key>::end();
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
                return containsAll(c) && size() == c.size();
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
                bool changed = false;
                for (auto elem : internalSet)
                {
                    if (!c.contains(elem)) {
                        remove(elem);
                        changed = true;
                    }
                }
                return changed;
            };

            int
            size ()
            {
                return internalSet.size();
            };
        };
    }
}