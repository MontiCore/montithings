# (c) https://github.com/MontiCore/monticore
<#setting locale="en_US">
<#assign offsets=[64,59,55,50,45,40]>
<#assign totalLength=0>
<#assign beatsPerBar=4.0>

from ThermostatImplTOP import ThermostatImplTOP

class ThermostatImpl(ThermostatImplTOP):

    active = True
    overwrite = False
    overwrite_temp = 0
    movement_activation = False

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["temperature_setting"].temp = 0

    def compute(self, port) -> None:
        self.overwrite = self._input.ports["temp_overwrite"].overwrite
        self.overwrite_temp = self._input.ports["temp_overwrite"].temp

        if "activation_room_" in port:
            
            self.movement_activation = self._input.ports["movement_activation"].state
        elif port == "window_room_state":
            self.active = self._input.ports["window_room_state"].state
        elif port == "clock":
            month = self._input.ports["clock"].month
            day = self._input.ports["clock"].day
            time = self._input.ports["clock"].time

            temp = 0
            if self.active:
                
                if not self.overwrite:
                    active_month = False
                    <#list ast.getYear().getMonthList() as monthVar>
                    if month == "${monthVar.getMonthString()}":
                        active_month = True
                    </#list>
                    
                    if active_month:
                    <#list ast.getWeek().getDayList() as dayVar>
                        if day == "${dayVar.getDays().getDaysString()}":
                        <#list dayVar.getHeatSettingList() as heatSet>
                            <#if heatSet.isCondition()>
                            if self.movement_activation:
                                if time >= ${heatSet.getStartTime()} and time < ${heatSet.getEndTime()}:
                                    temp = ${heatSet.getTemp()}
                            <#else>
                            if time >= ${heatSet.getStartTime()} and time < ${heatSet.getEndTime()}:
                                    temp = ${heatSet.getTemp()}
                            </#if>

                        </#list>
                    </#list>
                else:
                    temp = self.overwrite_temp

            self._result.ports["temperature_setting"].temp = temp
            self.send_port_temperature_setting()