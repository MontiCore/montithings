// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.notFitsInterface.sensors;

interface component SmokeSensorInterface<T> {

  port
    out T value;
}
