#pragma once
#include "CellularInput.h"
#include "CellularResult.h"
#include "IComputable.h"
#include "curl.h"
#include <stdexcept>


class CellularImpl : IComputable<CellularInput, CellularResult>{

public:
    CellularImpl();
    virtual CellularResult getInitialValues() override;
    virtual CellularResult compute(CellularInput input) override;
};
