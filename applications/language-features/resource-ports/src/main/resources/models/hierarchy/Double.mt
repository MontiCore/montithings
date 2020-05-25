package hierarchy;

component Double {
  port in int x;
  port out int y;

  component Sum;

  //guarantee : y < 3;

  connect x -> sum.in1;
  connect x -> sum.in2;
  connect sum.result -> y;
}