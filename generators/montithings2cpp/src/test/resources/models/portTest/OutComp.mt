package portTest;

component OutComp{

control {
    update interval 250s;
    if (A == "5") : f();
}

port out String outPort;
}
