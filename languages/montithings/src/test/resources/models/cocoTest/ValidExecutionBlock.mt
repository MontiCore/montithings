package cocoTest;

component ValidExecutionBlock{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute();
        else compute();
    }
}