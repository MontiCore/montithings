package portTest;

/*
 * Valid model.
 */

<<deploy, timesync>> component PortTest{

	component InComp ic;
	component OutComp oc;

	connect oc.outPort -> ic.inPort;
}
