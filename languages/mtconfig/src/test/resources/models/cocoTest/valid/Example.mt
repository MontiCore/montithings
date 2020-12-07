// (c) https://github.com/MontiCore/monticore
package cocoTest.valid;

component Example {
    SpeedLimiter limiter;
    Sink sink;

    limiter.outPort -> sink.inPort;
    sink.outPort -> limiter.inPort;
}