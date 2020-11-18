private:
nng::socket socket;
const char *uri;
bool isConnected = false;
Direction direction;

protected:
/**
* Tries to open a connection to an IPC port
* \return true iff connection was successfully opened; false otherwise
*/
bool
openConnection ()
{
try
{
// Dial specifies, that it connects to an already established socket (the server)
socket.dial (uri, nng::flag::alloc);
}
catch (const std::exception &e)
{
std::cout << "Connection to " << uri << " could not be established! (" << e.what ()
<< ")\n";
return false;
}
std::cout << "Connection to " << uri << " established\n";
return true;
}

public:
SinkValuePort (Direction direction, const char *uri) : direction (direction), uri (uri)
{
this->uri = uri;
if (direction == INCOMING)
// Open Socket in Request mode
{
socket = nng::req::open ();
}
else
{
socket = nng::push::open ();
}
isConnected = openConnection ();
}