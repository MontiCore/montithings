package portTest;

component InComp{
port in String inPort;
port in String inPort2;
port in String inPort3;

control {
    batch inPort2;
    sync port group Test1 (inPort, inPort2);
    update interval 250s;
    if (Test1 && (inPort == "test")) : compute();
}

}
