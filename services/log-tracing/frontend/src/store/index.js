import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import {getField, updateField} from 'vuex-map-fields';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        instances: ["hierarchy.Example", "hierarchy.Example.source", "hierarchy.Example.sink"],
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
        async getInternalData(state, traceUuid) {
            axios.get(`http://localhost:8080/logs/${this.state.selected_instance}/${traceUuid}`)
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
