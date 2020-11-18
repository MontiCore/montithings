if (!isConnected)
{
    isConnected = openConnection ();
    if (isConnected)
        {return;}
    }
// Sending an empty request to initialize data transfer from the ipc port.
socket.send ("");

// Receive Message and convert to target type T
auto msg = socket.recv_msg ();
auto data = msg.body ().template data<char> ();
    T result = jsonToData<T> (data);
        this->setNextValue (result);