# Log Filtering

<img src="docs/LogFilteringExample.png" alt="drawing" height="200px"/>

A simple application to show off various tracing possibilities.
A source yields increasing integer values which are received by two sink components.
In between the values are transformed in two ways:
 
- sink1 receives the value doubled after going being processed by a decomposed component.
- sink2 receives a running sum.

## Deployment

In order to enable log filtering add `<logtracing>ON</logtracing>` in the `pom.xml`.
Start the application as usual (`./build.sh logFilteringApp.Example`, followed by `./build/bin/run.sh`).

The visualization is done by an external tool.
Refer to the [README.md](/services/log-tracing/README.md) of the middleware in order to build it.
The middleware communicates with the MontiThings instances and has to know what message broker/transport is used.
Note that in case of DDS make sure the `dcpsconfig.ini` and `--DCPSInfoRepo` configuration equals.

Start the middleware and frontend using `./dockerRunLocal.sh --message-broker MQTT` in `\services\log-tracing`.


## Usage

Head over to `http://localhost:3000`.
First, the frontend needs to know which application is running and what components are involved. 
Copy the content of `target/generated-sources/deployment-info.json` and past it into the "Load config" tab.
The sidebar menu should contain all component instance names of the running instance.

By clicking on the instances corresponding log entries will be requested and displayed.
On the left side each log entry is annotated by colors.
The outer left color groups entries belonging to the same output. 
Analogously, the inner color defines affiliation with the same input.
The colors allows to quickly distinguish which log entries are more strongly related than others.

By selecting a log entry a trace is started on the right side.
By clicking on components within the trace the tree will be extended with further traces if possible.


