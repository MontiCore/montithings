from ExampleDataGeneratorImplTOP import ExampleDataGeneratorImplTOP
from threading import Thread
from time import sleep

class ExampleDataGeneratorImpl(ExampleDataGeneratorImplTOP):

    def clock_function(self):
        print("thread_start")
        while True:
            print("thread_loop")
            self._result.ports["clock"].time = 0
            self._result.ports["clock"].month = "January"
            self._result.ports["clock"].day = "Monday"
            self.send_port_clock()
            sleep(2)

    def __init__(self,instanceName):
        print("init")
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )
        thread = Thread(target = self.clock_function)
        thread.start()
        print("thread_joined")

    def getInitialValues(self) -> None:
        self._result.ports["clock"].time = 0
        self._result.ports["clock"].month = "January"
        self._result.ports["clock"].day = "Monday"
        self._result.ports["key_pad_input"].value = 1
        self._result.ports["window_sensor_state_1"].state = False
        self._result.ports["window_sensor_state_1"].sensor_id = 0
        self._result.ports["window_sensor_state_2"].state = False
        self._result.ports["window_sensor_state_2"].sensor_id = 1
        self._result.ports["movement_sensor_value"].activation_value = 0
        self._result.ports["movement_sensor_value"].sensor_id = 0

    def compute(self, port) -> None:
        print("got value")