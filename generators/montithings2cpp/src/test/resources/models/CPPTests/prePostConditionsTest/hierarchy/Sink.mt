// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sink {

  port in int value;

  // Comment out the guarantee in the Sink component
  // to let this assumption trigger
  pre (value isin ({1, 3, 5} union {format: "3[0-9]*"} union {format: "1[0-9]*"}));
  catch { value = 0; }
}
