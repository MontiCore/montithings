#include "SinkImpl.h"
#include <iostream>

namespace montithings {
    namespace hierarchy {

        SinkResult
            SinkImpl::getInitialValues()
        {
            return {};
        }

        SinkResult
            SinkImpl::compute(SinkInput input)
        {
            if (input.getValueAdap())
            {
                std::cout << *input.getValueAdap() << std::endl;
            }
            else
            {
                std::cout << "No data." << std::endl;
            }
            return {};
        }

    }
}