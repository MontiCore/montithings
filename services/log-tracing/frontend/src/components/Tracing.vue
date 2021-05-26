<template>
  <div class="mt-3 vh-100">

    <!--{{traces}} <br><br>
    {{ internal_data }}-->
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
import { mapFields } from "vuex-map-fields";
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
      "trace_tree_revision"
    ]),
    var_assignments: function() {
      if(this.internal_data.var_snapshot) {
        let assignments = JSON.parse(this.internal_data.var_snapshot).value0;
        let res = "";
        console.log(assignments);
        for (let assignment of assignments) {
          res += "<samp>" + assignment.key + " = " + assignment.value + ";</samp><br>";
        }
        return res;
      } else {
        return "";
      }
    },
    inputs: function() {
      if(this.internal_data.inputs) {
        return JSON.parse(this.internal_data.inputs).value0;
      } else {
        return [];
      }
    },
    traces: function() {
      if(this.internal_data.traces) {
        var res = [];
        var tracesWithPortNames = JSON.parse(this.internal_data.traces).value0;
        for (let trace of tracesWithPortNames) {
          var trace_id = trace.key;
          var trace_portName = trace.value;

          res.push({
            "trace_uuid": trace_id,
            "target_port" : trace_portName,
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
    getSourceInstanceName: function(portName) {
      if(this.internal_data.sources_ports_map) {
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
    getSourcePortName: function(portName) {
      if(this.internal_data.sources_ports_map) {
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
    buildInitialTree: function() {
      let top = 20;
      let left = 0;
      let operators = {};
      let source_count = 0;
      // source components
      for (let trace of this.traces.reverse()){
        let name = trace.trace_uuid;

        if(!operators[name]) {
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
        operators[name]["properties"]["outputs"][trace.trace_uuid + "_" + trace.source_port] = {
          label: trace.source_port + "=" + trace.value,
        }
      }

      if(source_count < 2) {
        left = 0;
      } else {
        left = Math.max(0, left/2-70);
      }

      top += 100;

      // target components
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
        operators[this.selected_instance]["properties"]["inputs"][this.selected_instance + "_" + inPort] = {
          label: inPort,
        }
      }

      store.state.trace_data["operators"] = operators;

      let links = {};
      for (let trace of this.traces){
        let op_name = trace.trace_uuid;
        links[op_name  + "_" + trace.target_port] = {
          fromOperator: op_name,
          fromConnector: trace.trace_uuid + "_" + trace.source_port,
          toOperator: this.selected_instance,
          toConnector: this.selected_instance + "_" + trace.target_port,
        }
      }

      store.state.trace_data["links"] = links;

      console.log(JSON.stringify(store.state.trace_data, null, 2));

      var $flowchart = $("#flowchartworkspace");
      //var $container = $flowchart.parent();

      $flowchart.flowchart({
        verticalConnection: true,
        //canUserEditLinks: false,
        //canUserMoveOperators: false,
        defaultLinkColor: "#888d91",
        defaultSelectedLinkColor: "#888d91",
        multipleLinksOnOutput: true,
        linkWidth: 3,
        data: store.state.trace_data,
        onOperatorSelect: function(operatorId) {
          console.log('Operator "' + operatorId + '" selected. Title: ' + $flowchart.flowchart('getOperatorTitle', operatorId) + '.');
          let selected_uuid = operatorId.split("_")[0];
          let selected_instance =  $flowchart.flowchart('getOperatorTitle', operatorId);

          store.state.selected_trace_uuid = selected_uuid;
          store.state.selected_instance = selected_instance;
          store.state.isFilterRelevantEntries = true;
          store.state.is_tracing = true;
          store.dispatch("getLogEntries", selected_instance);
          store.dispatch('getInternalDataTraced',
              { trace_uuid: selected_uuid,
                instance: selected_instance });
          return true;
        },
      });
      $flowchart.flowchart('setData', store.state.trace_data);
    },
    createTrace: function () {
      if (store.state.is_tracing === false) {
        this.buildInitialTree();
        return true;
      } else {
        this.updateTree();
      }
    },
    updateTree: function () {
      store.state.trace_data["operators"][store.state.selected_trace_uuid]["properties"]["body"] = this.var_assignments;
      store.state.trace_data["operators"][store.state.selected_trace_uuid]["properties"]["class"] = "flowchart-operator-no-fix-width-selected";

      var $flowchart = $("#flowchartworkspace");
      $flowchart.flowchart('setData', store.state.trace_data);
    },
  },
  watch: {
    trace_tree_revision: {
      handler: function(newVal, oldVal) {
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
          this.createTrace();
        }.bind(this));
    },
  }
};
</script>

