#pragma once

#include "tl/optional.hpp"

template <class T>
class DataSource
{
protected:
    tl::optional<T> currentValue;
    tl::optional<T> nextValue;


public:
    DataSource() {};
    DataSource(T initialValue) {
        currentValue = initialValue;
    }

    virtual tl::optional<T> getCurrentValue() {
        if (currentValue){
            auto temp = std::move(currentValue);
            currentValue = tl::nullopt;
            return temp;
        }
        return currentValue;
    }

    void setNextValue(T nextVal) {
        nextValue = nextVal;
    }

    void setNextValue(tl::optional<T> nextVal) {
        nextValue = nextVal;
    }

    tl::optional<T> getNextValue() {
        if (nextValue){
            auto temp = std::move(nextValue);
            nextValue = tl::nullopt;
            return temp;
        }
        return nextValue;
    }

    void update() {
        currentValue = nextValue;
    }
};
