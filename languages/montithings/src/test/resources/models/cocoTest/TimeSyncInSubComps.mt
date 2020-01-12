// (c) https://github.com/MontiCore/monticore
package cocoTest;

/*
 * Valid model.
 */

<<deploy, timesync>> component TimeSyncInSubComps{

	component InCompWithCalc ic;
	component OutCompWithTimeSync oc;


	connect oc.outPort -> ic.inPort;
}
