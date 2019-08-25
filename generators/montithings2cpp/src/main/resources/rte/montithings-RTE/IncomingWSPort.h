#pragma once
#include "Port.h"
#include "nngpp/nngpp.h"
#include "nngpp/protocol/sub0.h"
#include <iostream>
#include "cereal/archives/json.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/base_class.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/set.hpp"
#include "cereal/types/list.hpp"
#include "boost/lockfree/spsc_queue.hpp"
#include <future>
using namespace std;

/*
 * The class IPCPort extends the standard MontiArc Port with ipc capabilities in order to request data from
 * other processes running on the same machine.
 */
template <class T>
class IncomingWSPort : public Port<T> {

public:
    IncomingWSPort(const char* uri) : Port<T>() {
	    this->uri = uri;
        //Open Socket in Request mode
        socket = nng::sub::open();
        //Dial specifies, that it connects to an already established socket (the server)

        try
        {
            socket.listen(uri , nng::flag::alloc);

        }
        catch (const std::exception&)
        {
            cout << "Could not create listener for: " << uri << "\n";
            return;
        }
        cout << "Created listener for: " << uri << "\n";
        fut = std::async(std::launch::async, &IncomingWSPort::listen, this);
	};
	explicit IncomingWSPort(T initialValue) : Port<T>::Port(initialValue) {}




	tl::optional<T> getCurrentValue(boost::uuids::uuid uuid) {
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
    std::future<bool> fut;
    boost::lockfree::spsc_queue<T, boost::lockfree::capacity<5>> queue;

    /**
     * Initialize the IPC Port
     */
	void initIPC(){

	}

    bool listen() {
	    //Receive Message and convert to target type T
        while (true){
            auto msg = socket.recv_msg();
            auto data = msg.body().data<char>();
            std::string receivedAnswer(msg.body().data<char>());
            std::stringstream inStream(receivedAnswer);
            cereal::JSONInputArchive inputArchive(inStream);
            T result;
            inputArchive(result);

            queue.push(result);
            std::this_thread::yield();
            std::this_thread::sleep_for(std::chrono::milliseconds(1));
        }
        return true;
    }



};