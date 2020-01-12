// (c) https://github.com/MontiCore/monticore
package cocoTest;

component SyncSubset{

    port in String outPort;
    port in String inPort;

    control{
        sync port group Test (outPort);
        sync port group Test2 (outPort, inPort);
    }
}