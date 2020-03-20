// (c) https://github.com/MontiCore/monticore
package cocoTest;

component DoubleUpdateInterval {

    component InComp ic;
    component OutComp oc;

    connect oc.outPort -> ic.inPort;

    control{
        update interval 50msec;
        update interval 29msec;
    }
}