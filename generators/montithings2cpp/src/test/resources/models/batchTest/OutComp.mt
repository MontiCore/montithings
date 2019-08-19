package batchTest;

component OutComp{

control {
    batch on;
    update interval 20ms;
}

port out String outPort;
}
