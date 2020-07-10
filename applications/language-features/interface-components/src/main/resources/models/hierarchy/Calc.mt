package hierarchy;

component Calc<T extends MathExpression> {
  port in int x;
  port out int y;

  T t;

  x -> t.x;
  t.y -> y;
}