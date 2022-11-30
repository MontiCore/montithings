// (c) https://github.com/MontiCore/monticore
#include "RestImpl.h"
#include <iostream>
#include "cpp-httplib/httplib.h"

namespace montithings {
    namespace hierarchy {

    RestResult
    RestImpl::getInitialValues ()
    {
        return {};
    }

    RestResult
    RestImpl::compute (RestInput input)
    {
        httplib::Client cli("jsonplaceholder.typicode.com");

        auto res = cli.Get("/posts");
        
        if (res && res->status == 200) {
            std::cout << res->body << std::endl;
        } else {
            std::cout << "HTTP ERROR" << std::endl;
        }

        return {};
    }

    }
}