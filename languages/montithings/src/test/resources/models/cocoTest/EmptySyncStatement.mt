package cocoTest;

component EmptySyncStatement{

    port out String outPort;

    control{
        sync port group Test ( );
    }
}