// (c) https://github.com/MontiCore/monticore
package cocoTest;

component NegativeUpdateInterval {

    component InComp ic;
    component OutComp oc;

    connect oc.outPort -> ic.inPort;

    control{
        update interval 0msec;
    }
}