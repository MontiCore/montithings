package hierarchy;

component Source {

  port out int value;

  // Will cause an error once the output reaches three fail once
  post value < 3;
  catch { value = 4; };
}
