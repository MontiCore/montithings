#pragma once
#include "Port.h"
#include "nngpp/nngpp.h"
#include "nngpp/protocol/req0.h"
#include <iostream>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"
using namespace std;

/*
 * The class IPCPort extends the standard MontiArc Port with ipc capabilities in order to request data from
 * other processes running on the same machine.
 */
template <class T>
class IncomingIPCPort : public Port<T> {

public:
	IncomingIPCPort(const char* uri) : Port<T>() {
	    this->uri = uri;
        //Open Socket in Request mode
        socket = nng::req::open();
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
        cout << "Connection established\n";
	};
	explicit IncomingIPCPort(T initialValue) : Port<T>::Port(initialValue) {}



    tl::optional<T> getCurrentValue(boost::uuids::uuid uuid) {
        ipcUpdate();
		T queueElement;
        if (queueMap[uuid].pop(queueElement)){
            tl::optional<T> currentValue = queueElement;
            return currentValue;
        } else{
            return tl::nullopt;
        }
	}

private:
    nng::socket socket;
    const char* uri;

    /**
     * Initialize the IPC Port
     */
	void initIPC(){

	}

    void ipcUpdate() {
		try
		{
			socket.dial(uri, nng::flag::alloc);

		}
		catch (const std::exception&)
		{
			cout << "Connection to" << uri << " could not be established!\n";
			currentValue = tl::nullopt;
			return;
		}
	    //Sending an empty request to initialize data transfer from the ipc port.
	    socket.send("");

	    //Receive Message and convert to target type T
        auto msg = socket.recv_msg();
		auto data = msg.body().data<char>();
		std::string receivedAnswer(msg.body().data<char>());
        std::stringstream inStream(receivedAnswer);
        cereal::JSONInputArchive inputArchive(inStream);
        T result;
        inputArchive(result);


		currentValue = result;
    }



};