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