/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

namespace montithings {
    namespace collections {
        template<typename key>
        class list
        {
        private:
            std::list<key> internalList;

        public:

            list(const std::list<key> &internalList) : internalList(internalList) {}


            const std::list<key> &
            getInternalList () const
            {
                return internalList;
            }

        public:
            bool
            add (key e)
            {
                internalList.push_back(e);
                return true;
            };

            void
            add (int index, key e)
            {
                auto it = internalList.begin();
                it = std::next(it, index);
                internalList.insert(it, e);
            };

            bool
            addAll (list<key> c)
            {
                for (auto elem : c.getInternalList())
                {
                    add(elem);
                }
                return !c.isEmpty();
            };

            bool
            addAll (int index, list<key> c)
            {
                auto it = internalList.begin();
                it = std::next(it, index);
                for (auto elem : c.getInternalList())
                {
                    internalList.insert(it, elem);
                }
                return !c.isEmpty();
            };

            void
            clear ()
            {
                internalList.clear();
            };

            bool
            contains (key e)
            {
                for (key elem : internalList) {
                    if (elem == e) {
                        return true;
                    }
                }
                return false;
            };

            bool
            containsAll (list<key> c)
            {
                for (auto elem : c.getInternalList())
                {
                    if (!contains(elem)) {
                        return false;
                    }
                }
                return true;
            };

            bool
            equals (list<key> c)
            {
                return internalList == c.getInternalList();
            };

            key
            get (int index)
            {
                auto it = internalList.begin();
                it = std::next(it, index);
                return *it;
            };

            int
            indexOf(key e)
            {
                int i = 0;
                auto it = internalList.begin();
                while (it != internalList.end()) {
                    if (*it == e){
                        return i;
                    }
                    it = std::next(it, 1);
                    i++;
                }
                return -1;
            };

            bool
            isEmpty ()
            {
                return internalList.empty();
            };

            int
            lastIndexOf(key e)
            {
                int i = 0;
                int currIndex = -1;
                auto it = internalList.begin();
                while (it != internalList.end()) {
                    if (*it == e){
                        currIndex = i;
                    }
                    it = std::next(it, 1);
                    i++;
                }
                return currIndex;
            };

            bool
            removeAtIndex (int index)
            {
                auto it = internalList.begin();
                it = std::next(it, index);
                internalList.erase(it);
                return true;
            };

            key
            remove (key e)
            {
                int index = indexOf(e);
                auto it = internalList.begin();
                it = std::next(it, index);
                return *internalList.erase(it);
            };

            bool
            removeAll (list<key> c)
            {
                for (auto elem : c.getInternalList())
                {
                    remove(elem);
                }
                return true;
            }

            bool
            retainAll (list<key> c)
            {
                list<key> l(std::list<key> ({}));
                for (auto elem : internalList)
                {
                    if (!c.contains(elem)) {
                        l.add(elem);
                    }
                }
                removeAll(l);
                return true;
            };

            key
            set (int index, key e)
            {
                auto it = internalList.begin();
                it = std::next(it, index);
                *it = e;
                return e;
            };

            int
            size ()
            {
                return internalList.size();
            };

            list<key>
            sublist(int fromIndex, int toIndex)
            {
                for (int i = size()-1; i >= toIndex; i--) {
                    removeAtIndex(i);
                }
                for (int i = 0; i < fromIndex; i++) {
                    removeAtIndex(0);
                }
                return *this;
            };

            friend std::ostream &operator<<(std::ostream &os, const list &list) {
                os << "[";
                bool first = true;
                for (auto elem : list.getInternalList()) {
                    if (first) {
                        first = false;
                        os << elem;
                    }
                    else {
                        os << ", " << elem;
                    }
                }
                return os << "]";
            }
        };
    }
}