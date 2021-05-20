import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import {getField, updateField} from 'vuex-map-fields';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        instances: ["hierarchy.Example",
            "hierarchy.Example.source",
            "hierarchy.Example.lpf",
            "hierarchy.Example.d.sum",
            "hierarchy.Example.d",
            "hierarchy.Example.c",
            "hierarchy.Example.sink",
            "smartHome.SmartHome.airConditioner.aqa",
            "smartHome.SmartHome.airConditioner",
            "smartHome.SmartHome.airConditioner.wc",
            "smartHome.SmartHome.heater.evaluator",
            "smartHome.SmartHome.heater",
            "smartHome.SmartHome.heater.poller",
            "smartHome.SmartHome.homeCtrl",
            "smartHome.SmartHome",
            "smartHome.SmartHome.thermostat.arbiter",
            "smartHome.SmartHome.thermostat",
            "smartHome.SmartHome.thermostat.modeTimer",
            "smartHome.SmartHome.thermostat.ts",
            "smartHome.SmartHome.thermostat.ui",
            "smartHome.SmartHome.voiceCtrl",
            "smartHome.SmartHome.window",],
        selected_instance: "",
        selected_log_uuid: "",
        log_entries: [],
        internal_data: {},
        isFetchingLogs: false,
        isFetchingInternalData: false
    },
    getters: {
        getField,
    },
    mutations: {
        updateField,
        update_log_entries(state, data) {
            if(data) {
                data.sort(function(a,b){
                    if(a.time < b.time) return -1;
                    if(a.time > b.time) return 1;
                    return 0;
                });


                var outputCorrColors = ["bg-color1","bg-color2"];
                var inputCorrColors = ["bg-color4","bg-color5","bg-color3"];
                var lastOutputUuid = "";
                var lastInputUuid = "";
                var outputIndex = 0;
                var inputIndex = 0;
                data.forEach(function (logEntry) {
                    if (lastInputUuid !== logEntry.input_uuid) {
                        inputIndex = (inputIndex + 1) % inputCorrColors.length;
                    }
                    if (lastOutputUuid !== logEntry.output_uuid) {
                        outputIndex = (outputIndex + 1) % outputCorrColors.length;
                    }
                    logEntry.input_corr_color = inputCorrColors[inputIndex];
                    lastInputUuid = logEntry.input_uuid;
                    logEntry.output_corr_color = outputCorrColors[outputIndex];
                    lastOutputUuid = logEntry.output_uuid;
                });
            }

            state.log_entries = data;
            state.isFetchingLogs = false;
        },
        update_internal_data(state, data) {
            state.internal_data = data;
            state.isFetchingInternalData = false
        },
    },
    actions: {
        async getLogEntries(state, instanceName) {
            axios.get(`http://localhost:8080/logs/${instanceName}`)
                .then((response) => {
                    this.commit('update_log_entries', response.data);
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        async getInternalData(state, payload) {
            axios.get(`http://localhost:8080/logs/${this.state.selected_instance}/${payload.log_uuid}/${payload.input_uuid}/${payload.output_uuid}`)
                .then((response) => {
                    this.commit('update_internal_data', response.data);
                })
                .catch((error) => {
                    console.log(error);
                });
        }
    },
    modules: {},
});
