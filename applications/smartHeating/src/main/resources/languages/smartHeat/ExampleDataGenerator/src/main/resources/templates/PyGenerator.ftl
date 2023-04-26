# (c) https://github.com/MontiCore/monticore
<#setting locale="en_US">
<#assign offsets=[64,59,55,50,45,40]>
<#assign totalLength=0>
<#assign beatsPerBar=4.0>

from ExampleDataGeneratorImplTOP import ExampleDataGeneratorImplTOP
from threading import Thread
from time import sleep

class ExampleDataGeneratorImpl(ExampleDataGeneratorImplTOP):
    
    time = ${ast.getClock().getTime()}
    day = "${ast.getClock().getDay().getDaysString()}"
    month = "${ast.getClock().getMonth().getMonthString()}"

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
            <#if !ast.getClock().isStop()>
            self.updateTime()
            </#if>
    
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
        
    <#list ast.getWindowSensors().getSensorsList() as sensor>
        <#if sensor.isClosed()>
        self._result.ports["window_sensor_state_${sensor.getRoomId()}"].state = True
        <#else>
        self._result.ports["window_sensor_state_${sensor.getRoomId()}"].state = False
        </#if>
        self._result.ports["window_sensor_state_${sensor.getRoomId()}"].sensor_id = ${sensor.getSensorId()}
        self.send_port_window_sensor_state_${sensor.getRoomId()}()
    </#list>
    <#list ast.getMovementSensors().getSensorsList() as sensor>
        self._result.ports["movement_sensor_value"].activation_value = ${sensor.getSensorValue()}
        self._result.ports["movement_sensor_value"].sensor_id = ${sensor.getRoomId()}
        self.send_port_movement_sensor_value()
    </#list>

    <#if ast.getKeypadInput().isPresentInput()>
        self._result.ports["key_pad_input"].value = ${ast.getKeypadInput().getInput()}
        self.send_port_key_pad_input()
    </#if>

    def compute(self, port) -> None:
        print("got value")