package ipcTest;


<<deploy>> component IpcTest{

    component SubComp subComp;

    component SubComp{}

    resource port in String inPort("ipc://inPort"),
                  out String outPort("ipc://outPort");

    connect inPort -> outPort;

}