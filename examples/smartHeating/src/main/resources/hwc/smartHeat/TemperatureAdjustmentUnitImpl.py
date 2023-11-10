# (c) https://github.com/MontiCore/monticore

from TemperatureAdjustmentUnitImplTOP import TemperatureAdjustmentUnitImplTOP

class TemperatureAdjustmentUnitImpl(TemperatureAdjustmentUnitImplTOP):

    activeModes = []
    modeEndTimeDict = {}
    week = ["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]
    year = ["January","February","March","April","May","June","July","August","October","November","December"]
    time = 0
    day = "Monday"
    month = "January"

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["temp_overwrite_1"].temp = 0
        self._result.ports["temp_overwrite_1"].overwrite = False
        self._result.ports["temp_overwrite_2"].temp = 0
        self._result.ports["temp_overwrite_2"].overwrite = False
        conf = ""
        self._result.ports["configuration"].config = conf
        self.send_port_configuration()
    
    def compute(self, port) -> None:
        
        if port == "key_pad_input":
            key = self._input.ports["key_pad_input"].value
            self._result.ports["configuration"].config = f"KeypadInput: {key}"
            self.send_port_configuration()
            self.updateOverwrites()
        elif port == "clock":
            self.time = self._input.ports["clock"].time
            self.day = self._input.ports["clock"].day
            self.month = self._input.ports["clock"].month

            self.updateOverwrites()

            mETD = self.modeEndTimeDict.copy()
            for key in mETD:
                value = mETD[key]
                if self.month == value[0] and self.day == value[1] and self.time == value[2]:
                    self.activeModes.remove(key)
                    self.modeEndTimeDict.pop(key)
            
                    


    def calcEndTime(self,duration,modeName):
        endTime = self.time + duration
        endDay = self.day
        endMonth = self.month
        self._result.ports["configuration"].config = f" {self.month}, {self.day}, {self.time}"
        self.send_port_configuration()
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
        self._result.ports["configuration"].config = f" {endMonth}, {endDay}, {endTime}"
        self.send_port_configuration()
        self.modeEndTimeDict[modeName] = [endMonth, endDay, endTime]
        
    def updateOverwrites(self):
        self._result.ports["temp_overwrite_1"].temp = 0
        self._result.ports["temp_overwrite_1"].overwrite = False
        self._result.ports["temp_overwrite_2"].temp = 0
        self._result.ports["temp_overwrite_2"].overwrite = False
        conf = ""
        for mode in self.activeModes:
        
            conf = f"{conf} {mode},"
        self._result.ports["configuration"].config = conf
        self.send_port_configuration()
        self.send_port_temp_overwrite_1()
        self.send_port_temp_overwrite_2()