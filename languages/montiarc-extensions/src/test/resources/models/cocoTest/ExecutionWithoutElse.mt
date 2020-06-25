// (c) https://github.com/MontiCore/monticore
package cocoTest;

component ExecutionWithoutElse{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute();

    }
}