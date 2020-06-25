// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.notFitsInterface.sensors;

component SmokeSensor<T> {

  port
    in int fail,
    out T value;
}
