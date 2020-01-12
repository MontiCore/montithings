// (c) https://github.com/MontiCore/monticore

#include "DDCReaderImpl.h"

#include <iostream>

DDCReaderResult DDCReaderImpl::getInitialValues(){
	double speed = 0.0;
	return DDCReaderResult(speed);
}

DDCReaderResult DDCReaderImpl::compute(DDCReaderInput input){
  DdcReader<int> reader;
  double speed = reader.readDouble(0x304);
  return DDCReaderResult(speed);
}