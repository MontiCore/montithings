<!-- (c) https://github.com/MontiCore/monticore -->
# Preamble
This application is meant as Case Study for the bachelor thesis of Kai Welsing.
For instructions on using the end-user development (EUD) capabilities of MontiThings refere to the README of the application eud.

# SmartHeating
This project simulates a heating system with "smart" functionallities.
It consists of two thermostats, for which different temperatures can be set for different times in a day.
Also included is a component that manages motion sensor input and can be configured via EUD to trigger at different thresholds.
Furthermore there are two window sensors, that output signals depending on whether or not a window in a room is open or not.
There is a componentn called "Example Data generator", that would no exist, if this were a real system, but is needed for simualtion, as it provides fake sensor data and a clock for synchronisation. The values that are the output of this component can be adjusted using EUD.
Another component is the "Temperature Adjustment Unit", which utilises EUD to let the end-user define modes which overwrite thermostat settings for a certain time, when activated by a keypad.
Lastly there is the component "Exampel Data Printer" that manages the output to the log for this simulation and would also not be existent in a real system.

# Detailed description
A more detailed description of this application and its components is given inside of the EXPALIN.html files, that can be founf under 'src/main/resources/languages' and its subsequent folders.