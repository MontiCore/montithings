// (c) https://github.com/MontiCore/monticore
#pragma once

#include "tl/optional.hpp"
#include "rigtorp/SPSCQueue.h"
#include "string"
#include <map>
#include <boost/uuid/uuid.hpp>

template <class T>
class DataSource
{

    typedef std::map<boost::uuids::uuid ,  rigtorp::SPSCQueue<T> > map_type;
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
        if (queueMap[uuid].front()){
            queueElement = *(queueMap[uuid].front());
            queueMap[uuid].pop();
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
        return queueMap[uuid].front ();
    }

    void update() {
    }
};
