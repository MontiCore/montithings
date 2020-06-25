// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.implementationMissing.sensors;

interface component SmokeSensorInterface<T> {

  port
    out T value;
}
