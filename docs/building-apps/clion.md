## Building and Running an Application using CLion

It's also possible to option generated MontiThings Projects in IDEs.
Here, we'll show the process for CLion (which is the Intellij equivalent for C++).
First open the `target/generated-sources` folder as the root folder of a new project.
After CLion is done configuring, your window should look like this:

<img src="../../docs/Clion1.png" alt="Clion Screenshot" width="700px" />

Now, please open the `Edit configurations...` popup:

<img src="../../docs/Clion2.png" alt="Clion Screenshot" width="700px" />

In the popup, set the instance name of the component instance that will be instantiated by the application:

<img src="../../docs/Clion3.png" alt="Clion Screenshot" width="700px" />

Now, you just need to press the green play button and a window will show up that first compiles the code and then shows you the application's output:

<img src="../../docs/Clion4.png" alt="Clion Screenshot" width="700px" />