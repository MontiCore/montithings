## Building and Running an Application using Docker

In order to run an application using Docker, make sure that Docker is installed and running first.

Next, open a terminal in the *generated-sources* folder and use the following command in order to build the application:

```bash
./dockerBuild.sh
```

After, run the application using:

```bash
./dockerRun.sh
```

The application is now running inside of a docker container with the name `hierarchy.example:latest`. In order to verify that the container is instantiated correctly, you can run:

```bash
docker ps
```

In the resulting list, there should be a container with the correct name.

You can also look at the current logs of any instance. For our example, this would be done using the following command:

```bash
docker logs -f hierarchy.example
```

In this case, `hierarchy.example` is the fully qualified instance name. If your application is split (by using the `Splitting = LOCAL` mode in the `pom.xml` of the application), the fully qualified instance names would be `hierarchy.Example.sink`, for example.

After running the command above, the output should look similar to this:

<img src="../../docs/DockerRunScreenshot.png" alt="drawing" width="700px" />

As you can see, the Source component is sending a new value every second, which is then received by the Sink component.

If you want to stop the application you can do the following:

```bash
./dockerStop.sh
```