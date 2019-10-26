package ipcTest;


<<deploy, timesync>> component IpcTest{

    component SubComp subComp;


    resource port in String[] inPort requires Sensor: "Temperature",
                  out String outPort("ipc://outPort");

    connect inPort -> subComp.inPort;
    connect subComp.outPort -> outPort;

}