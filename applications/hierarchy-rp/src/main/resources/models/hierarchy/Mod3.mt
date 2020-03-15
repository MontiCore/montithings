package hierarchy;

component Mod3 {

  port in int inport;
  port out int outport;

  behavior {
    if ((inport % 3) == 0) : tru() | prio = 2;
    if ((inport % 3) != 0) : fals() | prio = 1;
    else logData();
  }

  /*
  automaton Modulo {
      state Zero, One;
      state Two;
      initial Zero;

      Zero -> One [(inport % 3) == 1] / {outport = 1};
      Zero -> Two [(inport % 3) == 2] / {outport = 2};
      One -> Two  [(inport % 3) == 1] / {outport = 2};
      One -> Zero [(inport % 3) == 2] / {outport = 0};
      Two -> Zero [(inport % 3) == 1] / {outport = 0};
      Two -> One  [(inport % 3) == 2] / {outport = 1};

      Zero -> Zero [(inport % 3) == 0] / {outport = 0};
      One -> One   [(inport % 3) == 0] / {outport = 1};
      Two -> Two   [(inport % 3) == 0] / {outport = 2};
    }
    */
}