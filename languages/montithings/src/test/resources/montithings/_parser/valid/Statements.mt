package valid;

component Statements {
  port in int diameter;
  port out String pizzaSize;

  behavior {
    if (diameter <= 26) { pizzaSize = "small"; }
    else if (diameter <= 30) { pizzaSize = "medium"; }
    else pizzaSize = "large";
  }

}
