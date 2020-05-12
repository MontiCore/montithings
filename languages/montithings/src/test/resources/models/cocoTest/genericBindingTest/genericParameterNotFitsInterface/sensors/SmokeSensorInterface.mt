// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors;

interface component SmokeSensorInterface<T> {

  port
    out T value;
}
