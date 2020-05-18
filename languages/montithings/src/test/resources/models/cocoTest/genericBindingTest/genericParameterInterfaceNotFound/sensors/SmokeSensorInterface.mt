// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterInterfaceNotFound.sensors;

interface component SmokeSensorInterface<T> {

  port
    out T value;
}
