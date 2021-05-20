<template>
  <div>
    <b-card class="mt-3 vh-100" header="Network Trace" v-if="internal_data">
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
        {{ internal_data }}
        {{ inputs }}
      </div>
      <div v-else>Please select a log entry.</div>
    </b-card>
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
    ]),
    inputs: function() {
      if(this.internal_data.inputs) {
        return JSON.parse(this.internal_data.inputs).value0;
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
    createTrace: function () {
      var data = {
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
      };

      var $flowchart = $("#flowchartworkspace");
      console.log($flowchart);
      //var $container = $flowchart.parent();

      $flowchart.flowchart({
        verticalConnection: true,
        canUserEditLinks: false,
        canUserMoveOperators: false,
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

