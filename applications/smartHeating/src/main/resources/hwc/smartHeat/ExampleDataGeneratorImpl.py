# (c) https://github.com/MontiCore/monticore

from ExampleDataGeneratorImplTOP import ExampleDataGeneratorImplTOP
from threading import Thread
from time import sleep

class ExampleDataGeneratorImpl(ExampleDataGeneratorImplTOP):
    
    time = 0
    day = "Monday"
    month = "January"

    week = ["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]
    year = ["January","February","March","April","May","June","July","August","October","November","December"]

    def clock_function(self):
        sleep(2)
        self.updateTime()
        while True:
            self._result.ports["clock"].time = self.time
            self._result.ports["clock"].month = self.month
            self._result.ports["clock"].day = self.day
            self.send_port_clock()
            sleep(2)
            self.updateTime()
    
    def updateTime(self):
        self.time += 1
        if self.time >= 24:
            self.time = 0
            indexWeek = self.week.index(self.day)
            if indexWeek + 1 < len(self.week):
                self.day = self.week[indexWeek + 1]
            else:
                self.day = self.week[0]
                indexYear = self.year.index(self.month)
                if indexYear + 1 < len(self.year):
                    self.month = self.year[indexYear +1]
                else:
                    self.month = self.year[0]
                

            

    def __init__(self,instanceName):
        print("init")
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    

        thread = Thread(target = self.clock_function)
        thread.start()

        

    def getInitialValues(self) -> None:
        self._result.ports["clock"].time = self.time
        self._result.ports["clock"].month = self.month
        self._result.ports["clock"].day = self.day
        self.send_port_clock()
        


    def compute(self, port) -> None:
        pass