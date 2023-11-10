<!-- (c) https://github.com/MontiCore/monticore -->
# Statecharts

This example shows an atomic component with a statechart to define its behavior. 
The example is again based on the Basic-Input-Ouput example.

<img src="../../../docs/Statecharts.png" alt="drawing" width="600px"/>

The `Three` component replaces the `Sink` component from the 
Basic-Input-Ouput example.
Instead of printing log messages for all incoming messages, the `Three`
component only prints a log message, if the incoming number is divisible 
by 3.
This behavior is implemented using an embedded statechart:
```
component Three {
  port in int input;

  statechart {
    initial state Dividable ;
    state NotDividable ;

    Dividable -> NotDividable [input % 3 != 0] ;
    NotDividable -> Dividable [input % 3 == 0] / { log("Three: " + input); };
  }
}
```

The component has two states, one of them being the initial state. 
The transitions define when the component may switch between the states.
A transition can only be executed if its guard is fulfilled (i.e. the expression
in the square brackets).
If the transition is triggered, the action (i.e. the code block after the slash)
is executed. 
In this case, the component prints a log message to the console.

As the `Three` component only prints numbers divisible by 3, 
the output fo the generated application looks like this:
```
INFO: 2021-06-30 21:01:09,281 Source: 1
INFO: 2021-06-30 21:01:10,281 Source: 2
INFO: 2021-06-30 21:01:11,283 Source: 3
INFO: 2021-06-30 21:01:11,283 Three: 3
INFO: 2021-06-30 21:01:12,284 Source: 4
INFO: 2021-06-30 21:01:13,285 Source: 5
INFO: 2021-06-30 21:01:14,286 Source: 6
INFO: 2021-06-30 21:01:14,286 Three: 6
...
```


# Further Information

- Statechart Language Project: 
[GitLab (internal)][sc-gitlab], 
[GitHub (public)][sc-github]


[sc-gitlab]: https://git.rwth-aachen.de/monticore/statechart/sc-language
[sc-github]: https://github.com/monticore/statecharts

