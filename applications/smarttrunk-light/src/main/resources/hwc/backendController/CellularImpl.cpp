#include "CellularImpl.h"
#include <iostream>
#include <string.h>

CellularResult CellularImpl::getInitialValues(){
	return CellularResult();
}

CellularResult CellularImpl::compute(CellularInput input){
    if (input.getInSpeed()) {
      std::cout << "Cellular in port json: " << input.getInSpeed().value() << "\n";
    }
    return CellularResult();
}
