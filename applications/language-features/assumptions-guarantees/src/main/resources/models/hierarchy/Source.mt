package hierarchy;

component Source {

  port out int value;

  // Will cause an error once the output reaches three fail once
  guarantee : value < 3;
}
