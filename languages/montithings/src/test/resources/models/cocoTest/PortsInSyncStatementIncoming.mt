// (c) https://github.com/MontiCore/monticore
package cocoTest;

component PortsInSyncStatementIncoming{

    port out String outPort;

    control{
        sync port group Test (inPort , outPort);
    }
}