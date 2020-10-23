# Hierarchy

<img src="docs/HierarchyExample.png" alt="drawing" height="200px"/>

The purpose of this example is to combine multiple of the features shown in the
other examples and thereby provide a starting point for trying out the features
yourself.

Basically this example generates integers, which are capped at 5 and then 
doubled. Overall this results in the output 2, 4, 6, 8, 0, 0, 0, ...

The name of this example comes from the fact that it demonstrates that 
components can be hierarchically composed. In this example the `Double` 
component used by the `Example` component is itself composed of subcomponents,
i.e., a `Sum` component which takes the input of `Double` and adds it to itself.

<img src="docs/HierarchyDouble.png" alt="drawing" height="200px"/>
