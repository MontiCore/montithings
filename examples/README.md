<!-- (c) https://github.com/MontiCore/monticore -->
# Example Applications

This folder contains many example projects that showcase MontiThings.
Each of them contains a README explaining you what it does and how to
execute it.
The [`language-features`](language-features) folder includes further
example projects that showcase specific language features.

## Where to start?

If you don't already know MontiThings, you'll probably want to look
at the [`basic-input-output`](basic-input-output) example first.
It shows how two components exchange messages with each other.
Once you're done with this project, you can have a look at the
[`mqtt-ports`](mqtt-ports) example that implements the same project
as [`basic-input-output`](basic-input-output) but generates a
distributed MQTT-based application.
The [`hierarchy`](hierarchy) project shows you how to hierarchically
compose components.

*Tip*: Many people also use the
[`basic-input-output`](basic-input-output) as a starting point for
experimenting and writing their own projects.
For example, try creating a project that calculates the prime numbers
up to 100 as an exercise.
You might want to look at the [`language-features`](language-features)
folder for some inspiration what's possible.

## More advanced examples

With this under your belt, you can advance to the more advanced
features showcased by the other examples.
For instance, learn how to connect sensors and actuators in the
[`sensor-actuator-access`](sensor-actuator-access) example, learn
to define behavior using Python with the
[`face-id-door-opener`](face-id-door-opener) example, or enable your
end-users to customize components using in the
[`end-user-development`](end-user-development) example.