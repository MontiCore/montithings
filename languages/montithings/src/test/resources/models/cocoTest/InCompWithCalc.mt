package cocoTest;

component InCompWithCalc{
    port in String inPort;
    control{
        update interval 20ms;
    }
}
