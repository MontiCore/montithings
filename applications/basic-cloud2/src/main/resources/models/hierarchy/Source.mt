// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out SampleData.SampleData value;

  int lastValue = 0;

  every 1s {
    lastValue++;
    String newText = "New value: " + lastValue;
    log("Source: " + newText);
    value = :SampleData.SampleData{
      value = newText;
    };
  }
}
