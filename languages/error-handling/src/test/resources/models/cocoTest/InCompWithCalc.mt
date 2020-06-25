// (c) https://github.com/MontiCore/monticore
package cocoTest;

component InCompWithCalc{
    port in String inPort;
    control{
        update interval 20msec;
    }
}
