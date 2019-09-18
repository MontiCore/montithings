package cocoTest;

import org.junit.Test;

component JavaImport{

    port in String inPort;

    behavior {
        if (inPort == "test") : compute();
        else compute();
    }
}