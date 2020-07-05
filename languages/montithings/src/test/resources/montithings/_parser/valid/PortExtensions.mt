package valid;

component PortExtensions {
  port in int a, b, c;
  buffer port in int x, y;

  sync a, b;
}
