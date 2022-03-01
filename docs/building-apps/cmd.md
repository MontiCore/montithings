## Building and Running an Application using the Command Line

*Note: If you're using Windows, please use PowerShell, not the default command line*

After building the project with `mvn clean install` and thus generating
the C++ code, go to the `target/generated-sources` folder.
There you can execute the build script:
```bash
./build.sh hierarchy
```

`hierarchy` refers to the folder name in which the generated C++ classes
can be found.
The folder name is the package name of the outermost component
(if the generator is set to splitting mode `OFF`) or the
fully qualified name of the outermost component (if the
generator is set to splitting mode  that is not `OFF`).

After building the project you can go to the new folder `build/bin`.
There you will find the generated binaries.
Execute them like this:
```bash
./hierarchy.Example -n hierarchy.Example
```

The `-n hierarchy.Example` defines the instance name of the component you'll instantiate by executing the application.
You can stop the application by pressing CTRL+C.

Every generated MontiThings component also comes with a generated command line interface that can tell you more about the available parameters, if you use the `-h` option.
In this case, it looks like this:
```
$ ./hierarchy.Example -h

USAGE: 

   hierarchy.Example  [-h] [--version] -n <string>


Where: 

   -n <string>,  --name <string>
     (required) Fully qualified instance name of the component

   --,  --ignore_rest
     Ignores the rest of the labeled arguments following this flag.

   --version
     Displays version information and exits.

   -h,  --help
     Displays usage information and exits.

   Example MontiThings component
```

Depending on the generator configuration, other options will be available.
For example, if you're using MQTT as message broker, you'll also get options like `--brokerHostname` to set the IP address or hostname of the broker.

In case you've configured the generator with a splitting mode other than `OFF`, you will find multiple binaries in this folder.
In this case, you will also find two scripts `run.sh` and `kill.sh` in the `build/bin` folder which will start and stop all of the generated components so that you dont have to open a huge number of terminal windows.
You can inspect their outputs by looking at the log files, that have the components instance name followed by the file extension `.log` (e.g. `hierarchy.Example.log`).