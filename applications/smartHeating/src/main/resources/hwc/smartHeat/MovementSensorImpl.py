# (c) https://github.com/MontiCore/monticore

from MovementSensorImplTOP import MovementSensorImplTOP

class MovementSensorImpl(MovementSensorImplTOP):
    endTimeDict = {}
    activeSensors = []
    week = ["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]
    year = ["January","February","March","April","May","June","July","August","October","November","December"]
    time = 0
    day = "Monday"
    month = "January"

    def __init__(self, instanceName):
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["activation_room_1"].state = False
        self._result.ports["activation_room_2"].state = False

    def compute(self, port) -> None:
        if port == "clock":
            self.time = self._input.ports["clock"].time
            self.day = self._input.ports["clock"].day
            self.month = self._input.ports["clock"].month
            
            self.updateOutput()

            eTD = self.endTimeDict.copy()
            for key in eTD:
                value = eTD[key]
                if self.month == value[0] and self.day == value[1] and self.time == value[2]:
                    self.activeSensors.remove(key)
                    self.endTimeDict.pop(key)
            

        elif port == "movement_sensor_value":
            sensor_id = self._input.ports["movement_sensor_value"].sensor_id
            activation_value = self._input.ports["movement_sensor_value"].activation_value

            if sensor_id == 2 and activation_value >= 0.5:
                self.activeSensors.insert(0,2)
                self.calcEndTime(1,2)
            if sensor_id == 1 and activation_value >= 0.5:
                self.activeSensors.insert(0,1)
                self.calcEndTime(1,1)


    def calcEndTime(self,duration,port):
        endTime = self.time + duration
        endDay = self.day
        endMonth = self.month
        while endTime >= 24:
            endTime = endTime - 24
            indexWeek = self.week.index(endDay)
            if indexWeek + 1 < len(self.week):
                endDay = self.week[indexWeek + 1]
            else:
                endDay = self.week[0]
                indexYear = self.year.index(endMonth)
                if indexYear + 1 < len(self.year):
                    endMonth = self.year[indexYear +1]
                else:
                    endMonth = self.year[0]
        self.endTimeDict[port] = [endMonth, endDay, endTime]
    
    def updateOutput(self):
        self._result.ports["activation_room_1"].state = False
        self._result.ports["activation_room_2"].state = False
        for sensor in self.activeSensors:
            self._result.ports[f"activation_room_{sensor}"].state = True
        self.send_port_activation_room_1()
        self.send_port_activation_room_2()