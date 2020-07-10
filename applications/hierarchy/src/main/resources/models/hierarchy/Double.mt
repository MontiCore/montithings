package hierarchy;

component Double {
  port in int x;
  port out int y;

  Sum;

  x -> sum.in1;
  x -> sum.in2;
  sum.result -> y;
}