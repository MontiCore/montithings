#include "InPortServer.h"
#include <set>
vector<std::string> InPortServer::getData(){
	std::vector<std::string> set;
	std::string str = "test";
    set.push_back(str);
	return set;
}

