// (c) https://github.com/MontiCore/monticore
package cocoTest;

component ExecutionBlockPriorities{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute() | prio = 4;
        if (inPort == "test") : compute() | prio = 4;
        if (inPort == "test") : compute();
        else compute();

    }
}