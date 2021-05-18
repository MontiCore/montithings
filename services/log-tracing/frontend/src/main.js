import Vue from 'vue';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import App from './App.vue';
import router from './router';
import store from './store';
import VariableAssignmentsComponent from './components/VariableAssignments.vue';
import InputsComponent from './components/Inputs.vue';
import TracesComponent from './components/Traces.vue';
import LogTableComponent from './components/LogTable.vue';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import VueJsonPretty from 'vue-json-pretty';

Vue.use(BootstrapVue);
Vue.use(IconsPlugin);
Vue.component('variable-assignments', VariableAssignmentsComponent);
Vue.component('inputs', InputsComponent);
Vue.component('traces', TracesComponent);
Vue.component('log-table', LogTableComponent);
Vue.component('vue-json-pretty', VueJsonPretty);
Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
