// (c) https://github.com/MontiCore/monticore
#define CPPHTTPLIB_OPENSSL_SUPPORT
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
        httplib::Client2 cli("https://api.cognitive.microsofttranslator.com");

        httplib::Headers headers = {
            { "Ocp-Apim-Subscription-Key", "1dfcb98ff33b41f9bf375d25d574bc47" },
            { "Ocp-Apim-Subscription-Region", "germanywestcentral" }
        };

        auto res = cli.Post("/translate?api-version=3.0&from=de&to=en", headers, "[{'Text':'Hallo'}]", "application/json");
        
        if (res && (res->status >= 200 || res->status <= 299)) {
            std::cout << res->body << std::endl;
        } else {
            std::cout << "HTTP ERROR" << std::endl;
        }

        return {};
    }

    }
}