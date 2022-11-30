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
        httplib::Client cli("https://jsonplaceholder.typicode.com");
        
        if (auto res = cli.Get("/posts")) {
            if (res->status == 200) {
            std::cout << res->body << std::endl;
            }
        } else {
            auto err = res.error();
            std::cout << "HTTP error: " << httplib::to_string(err) << std::endl;
        }

        return {};
    }

    }
}