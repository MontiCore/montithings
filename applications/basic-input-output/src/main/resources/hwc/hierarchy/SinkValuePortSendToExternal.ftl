if (nextVal)
{
if (!isConnected)
{
isConnected = openConnection ();
if (isConnected)
{
return;
}
}
auto dataString = dataToJson (nextVal);
socket.send (nng::buffer (nng_strdup (dataString.c_str ()), dataString.length () + 1),
nng::flag::alloc);

std::cout << dataString << "\n";
}