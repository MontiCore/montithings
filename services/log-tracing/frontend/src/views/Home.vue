<template>
  <div class="d-flex" id="wrapper">
    <!-- Sidebar-->
    <div class="bg-light border-right" id="sidebar-wrapper">
      <div class="sidebar-heading">Log Tracer</div>
      <b-tabs>
        <b-tab title="Instances" active>
          <div class="list-group list-group-flush">
            <a
              class="list-group-item list-group-item-action bg-light text-right"
              href="#"
              v-for="instance in instance_list"
              :key="instance.instanceName"
              @click="onInstanceChange(instance.instanceName)"
              v-bind:class="{
                'menu-active': instance.instanceName === selected_instance,
              }">{{ instance.instanceName }}</a>
          </div>
        </b-tab>
        <b-tab title="Load config">
          <b-card-text>
            <b-form-textarea
              id="textarea-auto-height"
              v-model="instances_config"
              placeholder="Paste your json config here..."
              rows="30"
              max-rows="8"
            >
            </b-form-textarea>
          </b-card-text>
        </b-tab>
      </b-tabs>
    </div>

    <!-- Page Content-->
    <div id="page-content-wrapper">
      <div class="container-fluid" v-if="selected_instance !== ''">
        <b-row>
          <b-col>
            <h1 class="mt-4 text-left">
              Log Entries for
              <small class="text-muted">{{ selected_instance }}</small>
              <b-button v-if="is_tracing"
                        variant="outline-dark"
                        class="m-2"
                        @click="onInstanceChange(selected_instance)"><b-icon icon="circle-fill" animation="throb" font-scale="1"></b-icon> Reset Trace</b-button>
            </h1>
          </b-col>
        </b-row>
        <div class="row h-100">
          <div class="col-md-auto">
            <log-table class="p-2"></log-table>
          </div>
          <div class="col">
            <div class="d-flex flex-column h-100">

              <tracing></tracing>
            </div>
          </div>
        </div>
      </div>
      <div class="container-fluid" v-else>
        <h1 class="mt-4">Load config and select an instance.</h1>
      </div>
    </div>
  </div>
</template>

<script>
import store from "../store/index";
import { mapFields } from "vuex-map-fields";

export default {
  computed: {
    ...mapFields(["instances_config", "instances", "selected_instance", "is_tracing"]),
    instance_list: function() {
      console.log(this.instances_config);
      try {
        let config = JSON.parse(this.instances_config);
        if ('instances' in config) {
          return config.instances.sort();
        }
      }
      catch (e) {
        this.$bvToast.toast(`Could not parse config. Please paste a valid deployment-info.json.`, {
          title: 'Unable to parse JSON',
          autoHideDelay: 5000,
          appendToast: true
        })
      }
      return [];
    },
  },
  created() {},
  methods: {
    onInstanceChange: function (instanceName) {
      store.state.selected_instance = instanceName;
      store.state.isFetchingLogs = true;
      store.state.selected_log_uuid = "";
      store.state.internal_data = "";
      store.state.is_tracing = false;
      store.state.trace_data = {};
      store.state.isFilterRelevantEntries = false;
      store.dispatch("getLogEntries", instanceName);
    },
  },
};
</script>
