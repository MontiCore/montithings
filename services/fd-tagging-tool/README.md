<!-- (c) https://github.com/MontiCore/monticore -->

# Feature Diagram Tagging Tool

Uses Port 4220. Requires IoT Manager to run.

## Build

```
gradle build
docker build -t montithings/fd-tagging-tool .
```

## Execute

```
docker run --rm -p 4220:4220 montithings/fd-tagging-tool
```

