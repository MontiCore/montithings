// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNeedsInterface.sensors;

interface component SmokeSensorInterface<T> {

  port
    out T value;
}
