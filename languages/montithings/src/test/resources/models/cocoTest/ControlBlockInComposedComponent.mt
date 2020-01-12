// (c) https://github.com/MontiCore/monticore
package cocoTest;

component ControlBlockInComposedComponent {

    component InComp ic;
    component OutComp oc;

    connect oc.outPort -> ic.inPort;

    control{
        batch inPort;
    }
}