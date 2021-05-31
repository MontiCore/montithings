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
        isFetchingInternalData: false,
        trace_data: {},
        is_tracing: false,
        selected_trace_uuid: "",
        isFilterRelevantEntries: false,
        trace_tree_revision:0
    },
    getters: {
        getField,
    },
    mutations: {
        updateField,
        update_log_entries(state, data) {
            console.log("update_log_entries");

            let filteredData = [];
            if(data) {
                data.sort(function(a,b){
                    if(a.index < b.index) return -1;
                    if(a.index > b.index) return 1;
                    return 0;
                });

                var outputCorrColors = ["bg-color1","bg-color2"];
                var inputCorrColors = ["bg-color4","bg-color5","bg-color3"];
                var lastOutputUuid = "";
                var lastInputUuid = "";
                var outputIndex = 0;
                var inputIndex = 0;

                if (data.length > 0) {
                    lastOutputUuid = data[0].output_uuid;
                    lastInputUuid = data[0].input_uuid;
                }

                let stop = false;
                var index;

                for (index = 0; index < data.length; ++index) {
                    if (state.selected_trace_uuid !== lastOutputUuid && stop) {
                        break;
                    }
                    if (lastInputUuid !== data[index].input_uuid) {
                        inputIndex = (inputIndex + 1) % inputCorrColors.length;
                    }
                    if (lastOutputUuid !== data[index].output_uuid) {
                        outputIndex = (outputIndex + 1) % outputCorrColors.length;
                    }
                    data[index].input_corr_color = inputCorrColors[inputIndex];
                    lastInputUuid = data[index].input_uuid;
                    data[index].output_corr_color = outputCorrColors[outputIndex];
                    lastOutputUuid = data[index].output_uuid;

                    if(state.is_tracing) {
                        if (state.selected_trace_uuid === lastOutputUuid) {
                            stop = true;
                            data[index].is_related_to_selected_uuid = true;
                        }
                    }
                    filteredData.push(data[index]);
                }
            }
            state.log_entries = filteredData;
            state.isFetchingLogs = false;
        },
        update_internal_data(state, data) {
            state.internal_data = data;
            state.isFetchingInternalData = false

            if(state.is_tracing) {
                state.trace_tree_revision++;
            }
        },
    },
    actions: {
        async getLogEntries(state, instanceName) {
            console.log("getLogEntries " + instanceName);
            axios.get(`http://localhost:8080/logs/${instanceName}`)
                .then((response) => {
                    console.log("getLogEntries");
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
        },
        async getInternalDataTraced(state, payload) {
            console.log("getInternalDataTraced " + payload.trace_uuid);
            axios.get(`http://localhost:8080/trace/${payload.instance}/${payload.trace_uuid}`)
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
