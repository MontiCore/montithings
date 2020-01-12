// (c) https://github.com/MontiCore/monticore
package portTest;

/*
 * Valid model.
 */
 #include <string>
 #include "test.h"

<<deploy>> component PortTest{
    resource port in  String resIn;
    resource port out String resOu("ipc://test2.ipc");

	component InComp ic;
	component OutComp oc;


	connect resIn -> oc.resIn;
	connect ic.resOut -> resOut;
	connect oc.outPort -> ic.inPort;
}
