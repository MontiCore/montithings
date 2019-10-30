#pragma once
#include "Port.h"
#include "nngpp/nngpp.h"
#include "nngpp/protocol/push0.h"
#include <iostream>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"
#include <future>
#include "tl/optional.hpp"
#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_generators.hpp>
using namespace std;

/*
 * The class IPCPort extends the standard MontiArc Port with ipc capabilities in order to request data from
 * other processes running on the same machine.
 */
template <class T>
class OutgoingIPCPort {

public:
    explicit OutgoingIPCPort(const char* uri) {
        this->uri = uri;
        //Open Socket in Request mode
        socket = nng::push::open();
        //Dial specifies, that it connects to an already established socket (the server)

        try
        {
            socket.dial(uri , nng::flag::alloc);

        }
        catch (const std::exception&)
        {
            cout << "Connection to" << uri << " could not be established!\n";
            return;
        }
        printf("Connection established\n");
    };
    explicit OutgoingIPCPort(T initialValue) {}

    void setPort(Port<T>* port){
        portSet = true;
        port->registerPort(uuid);
        this->port = port;
        fut = std::async(std::launch::async, &OutgoingIPCPort::run, this);
    }

private:
    nng::socket socket;
    const char* uri;
    bool portSet = false;
    Port<T>* port;
    std::future<bool> fut;
    boost::uuids::uuid uuid = boost::uuids::random_generator()();


    bool run() {
        while (true) {
            tl::optional<T> dataOpt = port->getCurrentValue(uuid);
            if (dataOpt) {
                try {
                    socket.dial(uri, nng::flag::alloc);

                }
                catch (const std::exception &) {
                    cout << "Connection to" << uri << " could not be established!\n";
                    continue;
                }
                T data = dataOpt.value();
                std::ostringstream stream;
                {
                    cereal::JSONOutputArchive outputArchive(stream);
                    outputArchive(data);
                }
                auto dataString = stream.str();

                dataString = stream.str();
                socket.send(nng::buffer(_strdup(dataString.c_str()),dataString.length() + 1));

                std::cout << dataString << "\n";

            }
            else{
            	std::this_thread::yield();
                std::this_thread::sleep_for(std::chrono::milliseconds(50));
            	
            }
        }
        return true;
    }



};