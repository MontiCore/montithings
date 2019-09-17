package cocoTest;

component ExecutionWithoutElse{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute();

    }
}