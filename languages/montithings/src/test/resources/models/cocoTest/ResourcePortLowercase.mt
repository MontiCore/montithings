// (c) https://github.com/MontiCore/monticore
package cocoTest;

/*
 * Valid model.
 */

 <<deploy>> component ResourcePortLowercase{
    resource port in  String ResIn;
    resource port out String ResOu("ipc://test2.ipc");

	component InComp ic;
	component OutComp oc;


	connect resIn -> oc.resIn;
	connect ic.resOut -> resOut;
	connect oc.outPort -> ic.inPort;
}
