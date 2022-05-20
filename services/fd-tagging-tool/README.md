Uses Port 4220. Requires IoT Manager to run.


## Build

```
gradle build
docker build -t montithings/tagging-tool-server .
```

## Execute

```
docker run --rm -p 4220:4220 montithings/tagging-tool-server
```

