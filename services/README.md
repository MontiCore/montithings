                                                       # Services

This folder contains services that mostly run alongside the generated
MontiThings applications.
If you don't already know what you're looking for, you'll probably
want to start by looking at the IoT Manager and IoT Client first and
then extend the system as you need more extra features.

## FD Tagging Tool

The feature diagram tagging tool serves as a backend for relating
components to features of a feature diagram.
It provides analyses such as what is the largest set of features that
can be deployed with the available hardware.


<img src="../docs/FDTagging.png" alt="drawing" width="600px"/>

More information can be found in Section 5 of [[BKK+22]](https://www.se-rwth.de/publications/Model-Driven-IoT-App-Stores-Deploying-Customizable-Software-Products-to-Heterogeneous-Devices.pdf).

## IoT Client

This is the base software that is installed on IoT devices.
It provides communication with the IoT manager.
We offer two clients for devices managed either via Azure IoT Hub or
via our own Python client.

## IoT Manager

The manager orchestrates the services that are executed alongside the
IoT applications.
It communicates with IoT clients and sends them commands which
container images to execute.
In publications, it is often referred to as *Deployment Manager*.

## Log Tracing

This service enables you to track log files and find the root cause of
problems.
It provides a web tool that communicates with components to collect
their logs, relate them to each other and find logs message that
led to the log messages you are interested in.
Also, it provides a kind of visual stacktrace of how your messages
went through the system.
The details are explained in [[KMM+22]](https://www.se-rwth.de/publications/Web-Based-Tracing-for-Model-Driven-Applications.pdf).
You can try it using the `log-filtering` example project.

<img src="../docs/LogTracingScreenshot.png" alt="drawing" width="600px"/>


## Prolog Generator & Prolog Server

The Prolog Generator generates Prolog code from facts about the
IoT devices and requirements.
Its input is provided in JSON format.
The Prolog Server wraps the Prolog Generator using a REST API.
The details of the algorithm are explained in
[[KKR+22]](https://www.se-rwth.de/publications/Model-driven-Self-adaptive-Deployment-of-Internet-of-Things-Applications-with-Automated-Modification-Proposals.pdf).

## Recorder

This service records messages exchanged via DDS and allows to replay
them for further analysis.

<img src="../docs/ReplayConcept.png" alt="drawing" width="600px"/>

The details are explained in [[KMR21]](https://www.se-rwth.de/publications/Understanding-and-Improving-Model-Driven-IoT-Systems-through-Accompanying-Digital-Twins.pdf).
You can try it using the `record-transform-replay` example project.

## Replay Messages

If hardware devices fail, we need to be able to recover the state
they were in before they failed.
This service can replay messages received by components.
To avoid an O(n) complexity of replaying messages, this service can
also store the serialized state of components in regular intervals and
only replay the messages that occurred after the state was last stored.

<img src="../docs/Replayer.png" alt="drawing" width="600px"/>

## Terraform Deployer

Some components need cloud services.
Developers can provide Terraform files to specify which cloud
resources a component needs.
This service is responsible for instantiating the cloud resources
defined by those Terraform files as soon as a component is
instantiated.