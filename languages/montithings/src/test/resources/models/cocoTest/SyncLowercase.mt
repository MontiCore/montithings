// (c) https://github.com/MontiCore/monticore
package cocoTest;

component SyncLowercase{

    port in String outPort;
    port in String inPort;

    control{
        sync port group test (outPort);
    }
}