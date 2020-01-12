// (c) https://github.com/MontiCore/monticore
package batchTest;

/*
 * Valid model.
 */

<<deploy, timesync>> component BatchTest{

	component InComp ic;
	component OutComp oc;

	connect oc.outPort -> ic.inPort;
}
