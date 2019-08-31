package ipcTest;


<<deploy>> component IpcTest{

    component SubComp subComp;


    resource port in String inPort("ipc://inPort"),
                  out String outPort("ipc://outPort");

    connect inPort -> outPort;

}