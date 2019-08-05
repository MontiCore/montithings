#pragma once

template <class T>
class DataSource
{
protected:
   T currentValue;
   T nextValue;


public:
	DataSource() {};
	DataSource(T initialValue) {
	currentValue = initialValue;
	}

	T getCurrentValue() {
		return currentValue;
	}

	void setNextValue(T nextVal) {
		nextValue = nextVal;
	}

	T getNextValue() {
		return nextValue;
	}

	void update() {
		currentValue = nextValue;
	}
};