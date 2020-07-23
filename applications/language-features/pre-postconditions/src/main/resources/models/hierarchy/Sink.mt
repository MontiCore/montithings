package hierarchy;

component Sink {

  port in int value;

  // Comment out the guarantee in the Sink component
  // to let this assumption trigger
  pre (value in ({1, 3, 5} union {format: "2*"}));
  catch { value = 0; };
}
