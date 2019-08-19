package batchTest;

component InComp{
port in String inPort;

control {
    batch on;
    update interval 200ms;
}
}
