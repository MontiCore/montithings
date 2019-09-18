package cocoTest;


component MultipleControlBlocks {

    component InComp ic;
    component OutComp oc;

    connect oc.outPort -> ic.inPort;

    control{
        batch inPort;
    }
    control{
        batch inPort;
    }

}