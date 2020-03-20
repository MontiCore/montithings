// (c) https://github.com/MontiCore/monticore
package portTest;

component InComp{
port in String inPort;

control {
    update interval 250msec;
}

behavior{
    if (inPort == "test") : compute() | prio = 10;
    if (inPort == "test") : compute2() | prio = 3;
    if (inPort == "test") : compute3();
    else compute4();
}

}
