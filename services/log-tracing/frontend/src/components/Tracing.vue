<template>
  <div class="mt-3 vh-100">
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

export default {
  name: "Trace",
  computed: {
    ...mapFields([
      "isFetchingInternalData",
      "selected_log_uuid",
      "internal_data",
      'selected_instance',
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
            "port" : trace_portName,
            "source": this.getSourceInstanceName(trace_portName),
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
            return item.value
          }
        }

      }
      return "NOT_FOUND";
    },
    createTrace: function () {
      let top = 20;
      let left = 0;
      let operators = {};

      // source components
      for (let trace of this.traces){
        let name = trace.trace_uuid + "_" + trace.port;
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
        operators[name]["properties"]["outputs"][trace.trace_uuid + "_" + trace.port] = {
          label: trace.port + "=" + trace.value,
        }
        top += 80;
        left += 200;
      }

      left = Math.max(0, left/2-70);

        top += 50;


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

      var data = {};
      data["operators"] = operators;

      let links = {};
      for (let trace of this.traces){
        let op_name = trace.trace_uuid + "_" + trace.port;
        links[op_name] = {
          fromOperator: op_name,
          fromConnector: trace.trace_uuid + "_" + trace.port,
          toOperator: this.selected_instance,
          toConnector: this.selected_instance + "_" + trace.port,
        }
      }

      data["links"] = links;

      console.log(JSON.stringify(data, null, 2));
      /*var data = {
        operators: {
          trace1_1: {
            top: 20,
            left: 0,
            properties: {
              title: "Source1",
              inputs: {},
              outputs: {
                trace1_1_out1: {
                  label: "Out 1",
                },
              },
            },
          },
          trace1_2: {
            top: 20,
            left: 200,
            properties: {
              title: "Source2",
              inputs: {},
              outputs: {
                trace1_2_out1: {
                  label: "Out 1",
                },
              },
            },
          },
          target: {
            top: 200,
            left: 100,
            properties: {
              title: "Sink",
              inputs: {
                input_1: {
                  label: "Input 1 = 2",
                },
                input_2: {
                  label: "Input 2 = 55",
                },
              },
              outputs: {},
            },
          },
        },
        links: {
          link_1: {
            fromOperator: "trace1_1",
            fromConnector: "trace1_1_out1",
            toOperator: "target",
            toConnector: "input_1",
          },
          link_2: {
            fromOperator: "trace1_2",
            fromConnector: "trace1_2_out1",
            toOperator: "target",
            toConnector: "input_2",
          },
        },
      };*/

      var $flowchart = $("#flowchartworkspace");
      //var $container = $flowchart.parent();

      $flowchart.flowchart({
        verticalConnection: true,
        //canUserEditLinks: false,
        //canUserMoveOperators: false,
        defaultLinkColor: "#f0f0f0",
        defaultSelectedLinkColor: "#f0f0f0",
        linkWidth: 5,
        data: data,
      });
    },
  },
  watch: {
    internal_data: function (newVal, oldVal) {
      console.log("value changed from " + oldVal + " to " + newVal);
      Vue.nextTick(
        function () {
          this.createTrace();
        }.bind(this)
      );
    },
  }
};
</script>

