<template>
  <div class="mt-3 vh-100">

    op name = {{ selected_trace_uuid }}_{{ selected_instance }} <br><br>
    {{ internal_data }}<br><br>
    inputs: {{ inputs }} <br><br>
    external_ports: {{ external_ports }} <br><br>
    traces: {{ traces }} <br><br>
    traces dec: {{ traces_decomposed }} <br><br>
    vars: {{ var_assignments }}<br><br>-
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
          if (!value.nullopt) {
            res[key] = value.payload.data;
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
        var tracesWithPortNames = JSON.parse(this.internal_data.traces)["value0"];
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
    },
    external_ports: function () {
      if (this.internal_data.external_ports) {
        return this.internal_data.external_ports
      } else {
        return [];
      }
    },
    traces_decomposed: function () {
      if (this.internal_data.traces_decomposed) {
        let res = [];
        let tracesWithPortNames = JSON.parse(this.internal_data.traces_decomposed)["value0"];
        for (let trace of tracesWithPortNames) {
          let trace_id = trace.key;
          let trace_portName = trace.value;

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
      let name = store.state.selected_trace_uuid + "_" + store.state.selected_instance;
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
        // if trace points to an external port simply annotate the corresponding port, but do not create a new operator
        if (this.external_ports.includes(trace.target_port)) {
          continue;
        }

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
        if (this.external_ports.includes(trace.target_port)) {
          // if trace points to an external port simply annotate the corresponding port, but do not create a link
          operators[this.selected_instance]["properties"]["inputs"]["in_" + trace.target_port]["label"] = trace.target_port + "=" + trace.value;
        } else {
          let op_name = trace.trace_uuid + "_" + trace.source;
          links[op_name + "_" + trace.target_port] = {
            fromOperator: op_name,
            fromConnector: "out_" + trace.source_port,
            toOperator: this.selected_instance,
            toConnector: "in_" + trace.target_port,
          }
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
        multipleLinksOnInput: true,
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
      } else {
        this.updateTree();
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

      //if (Object.keys(store.state.trace_data["operators"][selected_operator]["properties"]["inputs"]).length > 0) {
      //  $flowchart.flowchart('setData', store.state.trace_data);
      //  return;
     // }

      for (let inPort in this.inputs) {
        if (this.traces)
          store.state.trace_data["operators"][selected_operator]["properties"]["inputs"]["in_" + inPort] = {
            label: inPort,
          }
      }

      let top = 20;
      let left = 0;

      if (this.traces_decomposed.length) {
        left = 200;
      }

      for (let trace of this.traces_decomposed.reverse()) {
        let name = trace.trace_uuid + "_" + trace.source;

        if (!store.state.trace_data["operators"][name]) {
          store.state.trace_data["operators"][name] = {
            top: top,
            left: left,
            properties: {
              class: "flowchart-operator-no-fix-width",
              body: this.var_assignments,
              title: trace.source,
              is_decomposed: true,
              inputs: {},
              outputs: {},
            },
          }
          left += 200;
        }

        store.state.trace_data["operators"][name]["properties"]["outputs"]["out_" + trace.source_port] = {
          label: trace.source_port,
        }

        let target_name = store.state.selected_trace_uuid + "_" + store.state.selected_instance;

        let toOp = "";
        let toConnector = "";
        for (const link_key of Object.keys(store.state.trace_data["links"])) {
          let link = store.state.trace_data["links"][link_key];
          let selected_op = target_name;
          if (link["fromOperator"].startsWith(selected_op) && link["fromConnector"] === "out_" + trace.target_port) {
            toOp = link["toOperator"];
            toConnector = link["toConnector"];
          }
        }
        if (toOp !== "" && toConnector !== "") {

          store.state.trace_data["links"][name + "_" + trace.target_port] = {
            fromOperator: name,
            fromConnector: "out_" + trace.source_port,
            toOperator: toOp,
            toConnector: toConnector,
            color: "#166fc8"
          };
        }
      }

      left = 0;

      for (let trace of this.traces.reverse()) {
        let shouldMergeDecomposedTrace = false;
        // if the current selected operator is decomposed we have to merge the flow back to the main branch at some point
        if(store.state.trace_data["operators"][selected_operator]["properties"]["is_decomposed"]) {
          // the decomposition ends when the trace points to an operator with a different prefix in the title
          // or, more precisely, if there exists an operator marked with the same trace
          for (const op_name of Object.keys(store.state.trace_data["operators"])) {
            if (op_name.startsWith(store.state.selected_trace_uuid) &&
                op_name !== selected_operator) {
              shouldMergeDecomposedTrace = true;
              break;
            }
          }
        }
        if (shouldMergeDecomposedTrace) {
          for (const link_key of Object.keys(store.state.trace_data["links"])) {
            let link = store.state.trace_data["links"][link_key];
            if (link["toOperator"] !== selected_operator &&
                link["toOperator"].startsWith(store.state.selected_trace_uuid) &&
                link["toConnector"] === "in_" + trace.source_port) {

              store.state.trace_data["links"][name + "_" + trace.target_port] = {
                fromOperator: link["fromOperator"],
                fromConnector: link["fromConnector"],
                toOperator: selected_operator,
                toConnector: "in_" + trace.target_port,
                color: "#166fc8"
              }
            }
          }
        } else {
          let name = trace.trace_uuid + "_" + trace.source;
          if (!store.state.trace_data["operators"][name] && !this.external_ports.includes(trace.target_port)) {

            for (const op_name of Object.keys(store.state.trace_data["operators"])) {
              store.state.trace_data["operators"][op_name].top += 160;
            }

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

          if (store.state.trace_data["operators"][name]) {
            store.state.trace_data["operators"][name]["properties"]["outputs"]["out_" + trace.source_port] = {
              label: trace.source_port + "=" + trace.value,
            }
          }

          let target_name = store.state.selected_trace_uuid + "_" + store.state.selected_instance;

          if (this.external_ports.includes(trace.target_port)) {
            // if trace points to an external port simply annotate the corresponding port, but do not create a link
            store.state.trace_data["operators"][target_name]["properties"]["inputs"]["in_" + trace.target_port]["label"] =
                trace.target_port + "=" + trace.value;
          } else {
            store.state.trace_data["links"][name + "_" + trace.target_port] = {
              fromOperator: name,
              fromConnector: "out_" + trace.source_port,
              toOperator: target_name,
              toConnector: "in_" + trace.target_port,
            }
          }
        }
      }
/*
      // update positions of previous operators
      if (newOpCounter > 0) {
        for (const op_name of Object.keys(store.state.trace_data["operators"])) {
          store.state.trace_data["operators"][op_name].top += 160;
        }
      }
*/
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

