<!-- (c) https://github.com/MontiCore/monticore -->
# Object Constraint Language (OCL)

This example shows how OCL can be used in MontiThings.
If you have not done already, please read the behavior and pre-postconditions 
guides first.

The `Example` component has three subcomponents. 
The `Source` component produces all values between 0 and 100 that can be divided
by 3.
The `RunningSum` sums up all incoming values and provides the total sum of all
values it receives.
The `Sink` component consumes these values and displays them on the 
terminal.

<img src="../../../docs/OCL.png" alt="drawing" height="200px"/>

Overall this architecture gives the sums of numbers which are dividable by three
and less then one hundered, or more formally (and simplified):
```math
C_j =  3 \cdot \sum_{\substack{0 \leq i \leq j}} i = 3 \cdot \frac{j(j+1)}{2}, \quad \text{for } j = 1, \dotsc, 33
```

Hence the output looks like this:
```
3
9
18
30
...
1488
1584
1683
^C
```

This architecture uses OCL in different places.
First of all, OCL can be used to define pre- and postconditions.
The implicit context that would be used in OCL is always the `compute()` method
of an atomic component, i.e. the `behavior` block in the model.

Using such postconditions, the `Source` component can assure it only outputs 
numbers that can be devided by three:
```
component Source {
  port out int value;
  // ...
  post value % 3 == 0;
}
```

Also the `RunningSum` can verify its computation:
```
component RunningSum {
  port in int in;
  port out int result;
  int cumulativeSum = 0;

  behavior {
    cumulativeSum += in;
    result = cumulativeSum;
  }

  post cumulativeSum == cumulativeSum@pre + in;
```

Notice the `@pre` that refers to the value of `cumulativeSum` _before_ executing
the component's behavior.

Furthermore, the OCL can also be used within the behavior block. 
This is shown for example in the `Source` component:
```
component Source {
  port out int value;
  int lastValue = 0;

  behavior {
    lastValue++;
    if (exists i in {x in {1:100} | x % 3 == 0}: i == lastValue) {
      value = lastValue;
    }
  }

  // ... postconditions ...
}
``` 

Here OCL (`exists`) is used together with set definitions in the `if` statement 
of the behavior block.
The set comprehensions of OCL allow math-like definitions of sets. 
More on the MontiCore's OCL language can be found here:
[OCL/P](https://git.rwth-aachen.de/monticore/languages/OCL)




