package hierarchy;

component Calc<T extends MathExpression> {
  port in int x;
  port out int y;

  component T t;

  connect x -> t.x;
  connect t.y -> y;
}