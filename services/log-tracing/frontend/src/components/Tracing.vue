<template>
  <div class="mt-3 vh-100">

    <!-- op name = {{ selected_trace_uuid }}_{{ selected_instance }} <br><br>
     {{ internal_data }}<br><br>
     inputs: {{ inputs }} <br><br>
     traces: {{ traces }} <br><br>
     vars: {{ var_assignments }}<br><br>
     <b-alert show  v-if="comp_does_not_log_anything">Seems like this component does not log much. Showing generated log entries for corresponding inputs instead.</b-alert>
     <br>-->
    <div v-if="selected_log_uuid.length" class="h-100">
      <div v-if="isFetchingInternalData">
        <b-spinner small label="Small Spinner"></b-spinner>
      </div>
      <div class="h-100" id="chart_container">
        <div
            class="h-100 flowchart-example-container"
            id="flowchartworkspace"
        ></div>
      </div>
    </div>
  </div>
</template>


<script>
import {mapFields} from "vuex-map-fields";
import Vue from "vue";
import store from "../store";

export default {
  name: "Trace",
  computed: {
    ...mapFields([
      "isFetchingInternalData",
      "selected_log_uuid",
      "internal_data",
      "selected_instance",
      "trace_data",
      "is_tracing",
      "selected_trace_uuid",
      "trace_tree_revision",
      'comp_does_not_log_anything'
    ]),
    var_assignments: function () {
      if (this.internal_data.var_snapshot) {
        let assignments = JSON.parse(this.internal_data.var_snapshot).value0;
        let res = "";

        for (let assignment of assignments) {
          res += "<samp>" + assignment.key + " = " + assignment.value + ";</samp><br>";
        }
        return res;
      } else {
        return "";
      }
    },
    inputs: function () {
      if (this.internal_data.inputs) {
        let jInput = JSON.parse(this.internal_data.inputs).value0;
        let res = {};
        for (const [key, value] of Object.entries(jInput)) {
          console.log([key, value]);
          if (!value.nullopt) {
            res[key] = value.data;
          }

        }
        return res;
      } else {
        return [];
      }
    },
    traces: function () {
      if (this.internal_data.traces) {
        var res = [];
        var tracesWithPortNames = JSON.parse(this.internal_data.traces).value0;
        for (let trace of tracesWithPortNames) {
          var trace_id = trace.key;
          var trace_portName = trace.value;

          res.push({
            "trace_uuid": trace_id,
            "target_port": trace_portName,
            "source": this.getSourceInstanceName(trace_portName),
            "source_port": this.getSourcePortName(trace_portName),
            "value": this.inputs[trace_portName]
          })
        }
        return res;
      } else {
        return [];
      }
    }
  },
  data() {
    return {
      msg: "",
    };
  },

  methods: {
    getSourceInstanceName: function (portName) {
      if (this.internal_data.sources_ports_map) {
        var map = JSON.parse(this.internal_data.sources_ports_map).value0;
        for (let item of map) {
          if (item.key === portName) {

            // split at last dot, as postfix is port name
            var lastIndex = item.value.lastIndexOf('.');
            return item.value.substr(0, lastIndex)
          }
        }

      }
      return "NOT_FOUND";
    },
    getSourcePortName: function (portName) {
      if (this.internal_data.sources_ports_map) {
        var map = JSON.parse(this.internal_data.sources_ports_map).value0;
        for (let item of map) {
          if (item.key === portName) {

            // split at last dot, as postfix is port name
            var lastIndex = item.value.lastIndexOf('.');
            return item.value.substr(lastIndex + 1, item.value.length)
          }
        }

      }
      return "NOT_FOUND";
    },
    updateVarSnapshots: function () {
      let name = store.state.selected_trace_uuid + "_" + store.state.selected_instance ;
      store.state.trace_data["operators"][name]["properties"]["body"] = this.var_assignments;

      var $flowchart = $("#flowchartworkspace");
      $flowchart.flowchart('setData', store.state.trace_data);
    },

    buildInitialTree: function () {
      let top = 20;
      let left = 0;
      let operators = {};
      let source_count = 0;
      // source components
      for (let trace of this.traces) {
        let name = trace.trace_uuid + "_" + trace.source;

        if (!operators[name]) {
          operators[name] = {
            top: top,
            left: left,
            properties: {
              class: "flowchart-operator-no-fix-width",
              title: trace.source,
              inputs: {},
              outputs: {},
            },
          }

          top += 80;
          left += 200;
          source_count++;
        }
        operators[name]["properties"]["outputs"]["out_" + trace.source_port] = {
          label: trace.source_port + "=" + trace.value,
        }
      }

      if (source_count < 2) {
        left = 0;
      } else {
        left = Math.max(0, left / 2 - 70);
      }

      top += 100;

      // target component
      operators[this.selected_instance] = {
        top: top,
        left: left,
        properties: {
          class: "flowchart-operator-no-fix-width",
          body: this.var_assignments,
          title: this.selected_instance,
          inputs: {},
          outputs: {},
        },
      }

      for (let inPort in this.inputs) {
        operators[this.selected_instance]["properties"]["inputs"]["in_" + inPort] = {
          label: inPort,
        }
      }

      store.state.trace_data["operators"] = operators;

      let links = {};
      for (let trace of this.traces) {
        let op_name = trace.trace_uuid + "_" + trace.source;
        links[op_name + "_" + trace.target_port] = {
          fromOperator: op_name,
          fromConnector: "out_" + trace.source_port,
          toOperator: this.selected_instance,
          toConnector: "in_" + trace.target_port,
        }
      }


      store.state.trace_data["links"] = links;

      var $flowchart = $("#flowchartworkspace");

      $flowchart.flowchart({
        verticalConnection: true,
        canUserEditLinks: false,
        //canUserMoveOperators: false,
        defaultLinkColor: "#888d91",
        defaultSelectedLinkColor: "#888d91",
        multipleLinksOnOutput: true,
        linkWidth: 3,
        data: store.state.trace_data,
        onOperatorSelect: function (operatorId) {
          let selected_uuid = operatorId.split("_")[0];
          let selected_instance = $flowchart.flowchart('getOperatorTitle', operatorId);

          store.state.selected_trace_uuid = selected_uuid;
          store.state.selected_instance = selected_instance;
          store.state.isFilterRelevantEntries = true;
          store.state.comp_does_not_log_anything = false;
          store.state.is_tracing = true;
          store.dispatch("getLogEntries", selected_instance);
          store.dispatch('getInternalDataTraced',
              {
                trace_uuid: selected_uuid,
                instance: store.state.selected_instance
              });
          return true;
        },
      });
      $flowchart.flowchart('setData', store.state.trace_data);
    },
    onNewInternalData: function () {
      if (store.state.is_tracing === false) {
        this.buildInitialTree();
        return true;
      } else {
        this.updateVarSnapshots();
      }
    },
    updateTree: function () {
      var $flowchart = $("#flowchartworkspace");
      let selected_operator = store.state.selected_trace_uuid + "_" + store.state.selected_instance;

      store.state.trace_data["operators"][selected_operator]["properties"]["body"] = this.var_assignments;

      // adjust selected operator (blue border)
      for (const op_name of Object.keys(store.state.trace_data["operators"])) {
        store.state.trace_data["operators"][op_name]["properties"]["class"] = "flowchart-operator-no-fix-width";
      }
      store.state.trace_data["operators"][selected_operator]["properties"]["class"] = "flowchart-operator-no-fix-width-selected";

      if(Object.keys(store.state.trace_data["operators"][selected_operator]["properties"]["inputs"]).length > 0) {
        $flowchart.flowchart('setData', store.state.trace_data);
        return;
      }

      for (let inPort in this.inputs) {
        store.state.trace_data["operators"][selected_operator]["properties"]["inputs"]["in_" + inPort] = {
          label: inPort,
        }
      }

      let top = 20;

      let left = 0;
      // update positions of previous operators before inserting
      if (this.traces.length > 0) {
        for (const op_name of Object.keys(store.state.trace_data["operators"])) {
          store.state.trace_data["operators"][op_name].top += 160;
        }
      }

      for (let trace of this.traces.reverse()) {
        let name = trace.trace_uuid + "_" + trace.source;

        if (!store.state.trace_data[name]) {
          store.state.trace_data["operators"][name] = {
            top: top,
            left: left,
            properties: {
              class: "flowchart-operator-no-fix-width",
              body: this.var_assignments,
              title: trace.source,
              inputs: {},
              outputs: {},
            },
          }
          left += 200;
        }

        store.state.trace_data["operators"][name]["properties"]["outputs"]["out_" + trace.source_port] = {
          label: trace.source_port + "=" + trace.value,
        }
      }

      for (let trace of this.traces) {
        let op_name = trace.trace_uuid + "_" + trace.source;
        let target_name = store.state.selected_trace_uuid + "_" + store.state.selected_instance;

        store.state.trace_data["links"][op_name + "_" + trace.target_port] = {
          fromOperator: op_name,
          fromConnector: "out_" + trace.source_port,
          toOperator: target_name,
          toConnector: "in_" + trace.target_port,
        }
      }

      $flowchart.flowchart('setData', store.state.trace_data);
    },
  },
  watch: {
    trace_tree_revision: {
      handler: function (newVal, oldVal) {
        console.log("trace_data value changed from " + oldVal + " to " + newVal);
        Vue.nextTick(function () {
          this.updateTree();
        }.bind(this));
        return true;
      },
      deep: true
    },
    internal_data: function (newVal, oldVal) {
      console.log("internal_data value changed from " + oldVal + " to " + newVal);
      Vue.nextTick(function () {
        this.onNewInternalData();
      }.bind(this));
    },
  }
};
</script>

