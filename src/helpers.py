# %%
import os
import os.path
import io
import time
import math
import config
import json
import threading
from functools import partial
import subprocess

class DockerCompose:

    def __init__(self,cfile : str):
        self.cfile = cfile

    def getCommand(self, args : str):
        return f"{config.DOCKER_COMPOSE_EXECUTABLE} --file {self.cfile} {args}"

    def run(self, args : str):
        # execute docker-compose with compose file and given arguments
        return os.system(self.getCommand(args)) == 0

    def up(self):
        return self.run("up -d")

    def down(self):
        return self.run("down")

class DockerComposeListener:
    proc = None
    def __init__(self,compose : DockerCompose):
        self.compose = compose

    def listen(self, callback):
        self.proc = subprocess.Popen(self.compose.getCommand("events --json").split(" "))
        print(self.proc.stdout)
        for line in self.proc.stdout:
            event = json.loads(line)
            callback(event)

    def stopListening(self):
        if not self.proc is None:
            self.proc.kill()


            

class ComposeManager:

    compose : DockerCompose = None
    listener : DockerComposeListener = None

    def __init__(self, basePath : str):
        self.basePath = basePath

    def updateComposeFile(self, ymlContent : str) -> str:
        """Stores the compose-content and returns the created file path."""
        #dirName = format(math.floor(time.time()*1000),'x')
        dirPath = self.basePath# + os.path.sep + dirName
        filePath = dirPath + os.path.sep + "docker-compose.yml"
        os.makedirs(dirPath, exist_ok=True)

        with open(filePath, mode="w") as file:
            file.write(ymlContent)
        
        return filePath

    def undeploy(self):
        if not self.compose is None:
            self.compose.down()

    def pushCompose(self, ymlContent):
        # store compose file
        newComposeFile = self.updateComposeFile(ymlContent)

        # shut down previous compose
        if not self.compose is None:
            self.compose.down()

        self.compose = DockerCompose(newComposeFile)

        # register event listeners
        # self.listener = DockerComposeListener(self.compose)
        # eventThread = threading.Thread(target=partial(DockerComposeListener.listen,self.listener,print))
        # eventThread.setDaemon(True)
        # eventThread.start()

        # start new compose
        self.compose.up()
# %%
if __name__ == "__main__":
    mngr = ComposeManager("../run/deployment")
    mngr.updateComposeFile("test")
# %%
