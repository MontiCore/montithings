// (c) https://github.com/MontiCore/monticore
package cocoTest;

component ExecutionWithTwoElse{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute();
        else compute();
        else compute();
    }
}