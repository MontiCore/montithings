#pragma once

#include "tl/optional.hpp"
#include "boost/lockfree/spsc_queue.hpp"
#include "string"

template <class T>
class DataSource
{
protected:
    tl::optional<T> currentValue;
    tl::optional<T> nextValue;
    boost::lockfree::spsc_queue<T, boost::lockfree::capacity<5>> queue;


public:
    DataSource() {};
    DataSource(T initialValue) {
        currentValue = initialValue;
    }

    virtual tl::optional<T> getCurrentValue() {
        T queueElement;
        if (queue.pop(queueElement)){
            currentValue = queueElement;
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
        currentValue = nextValue;
    }
};
