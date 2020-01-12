// (c) https://github.com/MontiCore/monticore
#pragma once
class IComponent
{

	virtual void setUp() = 0;
	virtual void init() = 0;
	virtual void compute() = 0;
	virtual void start() = 0;
};

