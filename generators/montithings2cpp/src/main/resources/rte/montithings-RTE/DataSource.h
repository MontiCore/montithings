#pragma once

#include "tl/optional.hpp"
#include "boost/lockfree/spsc_queue.hpp"
#include "string"

template <class T>
class DataSource
{
protected:
    boost::lockfree::spsc_queue<T, boost::lockfree::capacity<1024>> queue;

public:
    DataSource() {};
    DataSource(T initialValue) {
        queue.push(initialValue);
    }

    virtual tl::optional<T> getCurrentValue() {
        T queueElement;
        if (queue.pop(queueElement)){
            tl::optional<T> currentValue = queueElement;
            return currentValue;
        } else{
            return tl::nullopt;
        }
    }

    void setNextValue(T nextVal) {
        queue.push(nextVal);
    }

    void setNextValue(tl::optional<T> nextVal) {
        if (nextVal){
            queue.push(nextVal.value());
        }
    }

    bool hasValue(){
        return (queue.read_available() > 0);
    }

    void update() {
    }
};
