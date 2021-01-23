# SI Unit Support

This example shows how to components can uses SI units. 
The example is again based on the Basic-Input-Ouput example with a number of 
changes to demonstrate SI unit usage.

<img src="docs/SIUnitsMT.png" alt="drawing" height="200px"/>

The `Source` component now has a parameter whose type is defined as m/s:
```
component Source (m/s startSpeed) {
  // ...
}
```
However, as you can see in the figure above, the `Example` component provides
its instance of the `Source` component with `1 km/h`. 
MontiThings is smart enough to automatically convert that to about `0.28 m/s`. 

Similarly, the `Source` component provides a value of type `km/h` but the 
`Sink` expects a value of type `m/s` on its incoming port. 
Again, MontiThings converts that automatically for you.

SI Units can, of course, also be used inside the behavior block of a component:
```
component Source (m/s startSpeed) {
  port out km/h value;

  km/h lastValue = startSpeed;

  behavior {
    value = lastValue;
    lastValue = lastValue + 1 km/h;
  }

  update interval 1sec;
}
```

When assigning `startSpeed` to `lastValue` MontiThings again automatically 
handles the conversion for you. 
Inside the behavior block, you can see how SI values can be used in arithmethic
expressions.

By default, SI units are treated as `double` values on a code level. 
More theoretically speaking, a the type of a variable with an SI type has a 
pair as its type that consists of the SI unit and the data type of the numeric
value (e.g. `double` or `int`). 
In case you do not want MontiThings to use `double` as the type for your SI 
type, you can also specify the numeric type explicitly using angle brackets:
```
component Sink {
  port in m/s<double> value;
}
```

Sometimes, however, it is not possible to convert between SI units. 
For example, `km/h` cannot be converted to `dB` ("decibel" - a measure of 
sound pressure levels). 
In this case MontiThing will stop and give you an error telling you that the 
types are incompatible.


# Further Information

- SI Units Language Project: 
[GitLab (internal)][si-gitlab], 
[GitHub (public)][si-github]

- SI Units Explanation: [Wikipedia][si-wikipedia] 


[si-gitlab]: https://git.rwth-aachen.de/monticore/languages/siunits
[si-github]: https://github.com/MontiCore/siunits
[si-wikipedia]: https://en.wikipedia.org/wiki/International_System_of_Units

