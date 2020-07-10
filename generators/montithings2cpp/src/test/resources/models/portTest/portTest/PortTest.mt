// (c) https://github.com/MontiCore/monticore
package portTest;

/*
 * Valid model.
 */
component PortTest {

  InComp ic;
	OutComp oc;

	oc.outPort -> ic.inPort;
}
