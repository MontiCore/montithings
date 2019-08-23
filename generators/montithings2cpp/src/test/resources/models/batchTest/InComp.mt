package batchTest;

component InComp{
port in String inPort;

control {
    batch inPort;
    update interval 200ms;
}
}
