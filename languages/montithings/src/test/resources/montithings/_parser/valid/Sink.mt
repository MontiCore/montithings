// (c) https://github.com/MontiCore/monticore
package valid;

component Sink {
  port in mm^2/kVA^2h value;

  mm^2/kVA^2h<int> lastValue = 20 km;
}
