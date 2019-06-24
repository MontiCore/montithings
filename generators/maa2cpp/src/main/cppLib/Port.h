#pragma once
#include "DataSource.h"


template <class T>
class Port : public DataSource<T>
{
public:
	Port() : DataSource<T>() {};
	Port(T initialValue) : DataSource<T>::DataSource(initialValue) {}
	
};

