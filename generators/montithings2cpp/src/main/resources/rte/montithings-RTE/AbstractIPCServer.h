//
// Created by JFuer on 27/07/2019.
//

#ifndef REQREP_ABSTRACTIPCSERVER_H
#define REQREP_ABSTRACTIPCSERVER_H


#include <string>
#include <nngpp/nngpp.h>
#include <nngpp/protocol/rep0.h>
#include <nngpp/protocol/req0.h>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"


template <typename T>
class AbstractIPCServer{
private:
    virtual T getData() = 0;
    std::string dataString = "";
    nng::socket sock;

public:
    explicit AbstractIPCServer(const char *uri) {
        sock = nng::rep::open();
        sock.listen(uri);
    }

    void run(){
        while (true){
            std::ostringstream stream;
            sock.recv();
            T data = getData();

            {
                cereal::JSONOutputArchive outputArchive(stream);
                outputArchive(data);
            }

            dataString = stream.str();
            if (dataString.find('[') == std::string::npos){
                nng::msg msg(strlen(dataString.c_str()) +1);
                msg.body().insert(nng::view(dataString.c_str(), strlen(dataString.c_str()) +1));
                sock.send(std::move(msg));
            }
            else {
                nng::msg msg(strlen(dataString.c_str()) * 2);
                msg.body().insert(nng::view(dataString.c_str(), strlen(dataString.c_str()) * 2));
                sock.send(std::move(msg));
            }

            std::cout << dataString << "\n";

            //sock.send(nng::view(dataString.c_str(), strlen(dataString.c_str()) + 1));
        }
    }
};


#endif //REQREP_ABSTRACTIPCSERVER_H
