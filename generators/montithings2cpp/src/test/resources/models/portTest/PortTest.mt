package portTest;

/*
 * Valid model.
 */

<<deploy>> component PortTest{
    resource port in  String resIn("ipc://test1.ipc");
    resource port out String resOut("ipc://test2.ipc");

	component InComp ic;
	component OutComp oc;


	connect resIn -> oc.resIn;
	connect ic.resOut -> resOut;
	connect oc.outPort -> ic.inPort;
}
