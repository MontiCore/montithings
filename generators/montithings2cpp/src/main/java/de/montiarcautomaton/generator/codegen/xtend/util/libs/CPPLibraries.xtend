package de.montiarcautomaton.generator.codegen.xtend.util.libs

class CPPLibraries {
	
	def static getDataSourceString(){
	return '''
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
	'''
	}
	
	def static getIComponentString(){
		return '''
		#pragma once
		class IComponent
		{
		
			virtual void setUp() = 0;
			virtual void init() = 0;
			virtual void compute() = 0;
			virtual void update() = 0;
		};
		
		'''
	}
	
	def static getIComputableString(){
		return'''
		#pragma once
		
		template <typename  T, typename  Y>
		class IComputable
		{
		public:
			virtual Y getInitialValues() = 0;
			virtual Y compute(T input) = 0;
		};
		
		'''
	}
	
	def static getPortString(){
		return '''
		#pragma once
		#include "DataSource.h"
		
		
		template <class T>
		class Port : public DataSource<T>
		{
		public:
			Port() : DataSource<T>() {};
			Port(T initialValue) : DataSource<T>::DataSource(initialValue) {}
			
		};
		'''
	}
}