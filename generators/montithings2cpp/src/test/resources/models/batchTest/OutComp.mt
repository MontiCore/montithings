package batchTest;

component OutComp{

control {
    batch on;
    update interval 20ms;
    if (A == "5") : f();
}

port out String outPort;
}
