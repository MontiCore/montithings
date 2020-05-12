// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.valid.sensors;

interface component SmokeSensorInterface<T> {

  port
    out T value;
}
