package portTest;

component InComp{
port in String inPort;

control {
    update interval 250ms;
}

behavior{
    if (inPort == "test") : compute() | prio = 10;
    if (inPort == "test") : compute2() | prio = 3;
    if (inPort == "test") : compute3();
    else compute4();
}

}
