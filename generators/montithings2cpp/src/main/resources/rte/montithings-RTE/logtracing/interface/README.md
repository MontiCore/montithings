# LogTracer

<img src="docs/CDLogTracer.png" alt="drawing" height="400px"/>

The LogTracer class is instantiated in all components and is the central instance which collects all trace data.
It provides methods which are used within components to collect data.  
It further serves as the interface which accepts requests from the middleware. 
Depending on the used message broker DDS or MQTT clients are used. 