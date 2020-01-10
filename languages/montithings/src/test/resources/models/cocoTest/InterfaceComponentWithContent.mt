package cocoTest;

#include <string>

interface component InterfaceComponentWithContent {

    component InComp ic;
    component OutComp oc;

    connect oc.outPort -> ic.inPort;
}