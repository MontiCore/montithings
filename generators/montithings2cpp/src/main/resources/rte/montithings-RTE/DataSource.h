#pragma once

#include "tl/optional.hpp"
#include "boost/lockfree/spsc_queue.hpp"
#include "string"
#include <map>
#include <boost/uuid/uuid.hpp>


template <class T>
class DataSource
{

    typedef std::map<boost::uuids::uuid ,  boost::lockfree::spsc_queue<T,boost::lockfree::capacity<1024> > > map_type;
protected:
    map_type queueMap;

    virtual void pushToAll(T nextVal){
        for (auto& x : queueMap){
            x.second.push(nextVal);
        }
    }
public:
    DataSource() {};
    DataSource(T initialValue) {
    }

    virtual void registerPort(boost::uuids::uuid uuid){
        queueMap[uuid];
    }

    virtual tl::optional<T> getCurrentValue(boost::uuids::uuid uuid) {
        T queueElement;
        if (queueMap[uuid].pop(queueElement)){
            tl::optional<T> currentValue = queueElement;
            return currentValue;
        } else{
            return tl::nullopt;
        }
    }


    void setNextValue(T nextVal) {
        pushToAll(nextVal);
    }

    void setNextValue(tl::optional<T> nextVal) {
        if (nextVal){
            pushToAll(nextVal.value());
        }
    }

    virtual bool hasValue(boost::uuids::uuid uuid){
        return (queueMap[uuid].read_available() > 0);
    }

    void update() {
    }
};
