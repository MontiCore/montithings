package resourceParameters;

/*
 * Valid model.
 */

<<deploy, timesync>> component ResourceParameters{

    resource port in String inPort(testParameter = "testValue");

	component InComp ic;
	component OutComp oc;

	connect oc.outPort -> ic.inPort;
}
