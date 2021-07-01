// (c) https://github.com/MontiCore/monticore
package valid;

/*
 * Valid model.
 */
component Composed {
  InComp ic ("test");
	OutComp oc;

	oc.outPort -> ic.inPort;

	timing sync;
}
