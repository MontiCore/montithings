// (c) https://github.com/MontiCore/monticore
package cocoTest;

component MultipleExecutionBlocks{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute();
        else compute();
    }
    behavior {
        if (inPort == "test") : compute();
        else compute();
    }
}