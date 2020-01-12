// (c) https://github.com/MontiCore/monticore
package cocoTest;

component PortsInBatchStatementIncoming{

    port out String outPort;

    control{
        batch inPort;
        batch outPort;
    }
}