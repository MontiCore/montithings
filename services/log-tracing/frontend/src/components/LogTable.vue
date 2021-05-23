<template>
  <div>
    <b-button block pill variant="outline-primary" v-if="isFilterRelevantEntries" @click="isFilterRelevantEntries=false">Show previous log entries</b-button>
    <br>
    <b-table borderless small hover selectable show-empty
             :items="log_entries"
             :fields="fields"
             :busy="isFetchingLogs"
             :select-mode.sync="selectMode"
             :filter=isFilterRelevantEntries
             :filter-function="filterOnlyRelevant"
             @row-clicked="onRowClick">

      <template #table-busy>
        <div class="text-center my-2">
          <b-spinner class="align-middle"></b-spinner>
          <strong> Requesting logs from instance...</strong>
        </div>
      </template>

      <template #empty="scope">
        <h4>{{ scope.emptyText }}</h4>
      </template>
    </b-table>
  </div>
</template>

<script>
import {mapFields} from 'vuex-map-fields';
import moment from 'moment'
import store from '../store/index';

export default {
  name: 'CardEditor',
  computed: {
    ...mapFields([
      'selected_instance',
      'selected_log_uuid',
      'log_entries',
      'isFetchingLogs',
      'isFetchingInternalData',
      'internal_data',
      'is_tracing',
      'isFilterRelevantEntries'
    ]),
  },
  methods: {
    // eslint-disable-next-line no-unused-vars
    onRowClick(record, index) {
      if(store.state.is_tracing) {
        return;
      }
      store.state.selected_log_uuid = record.log_uuid;
      store.state.isFetchingInternalData = true;
      store.state.internal_data = "";
      store.dispatch('getInternalData',
          { log_uuid: record.log_uuid,
            input_uuid: record.input_uuid,
            output_uuid: record.output_uuid  });
    },
    setOutputCorrColor(value, key, item) {
      return item.output_corr_color;
    },
    setInputCorrColor(value, key, item) {
      return item.input_corr_color;
    },
    setRelatedColor(value, key, item) {
      if (item.is_related_to_selected_uuid && !store.state.isFilterRelevantEntries) {
        return "bg-related";
      }
      return;
    },
    filterOnlyRelevant(row, isFilterEnabled) {
      if (!isFilterEnabled) {
        return true;
      }

      if (row.is_related_to_selected_uuid) {
        return true;
      } else {
        return false;
      }
    }
  },
  data() {
    return {
      selectMode: 'single',
      fields: [
         {
          key: 'outputCorrelation',
          sortable: false,
          thStyle: {
            display: 'none'
          },
          tdClass: 'setOutputCorrColor',
        },         {
          key: 'inputCorrelation',
          sortable: false,
          thStyle: {
            display: 'none'
          },
          tdClass: 'setInputCorrColor',
        },
        {
          key: 'time',
          formatter: ts => {
            return moment.unix(ts).format("DD.MM.YYYY HH:mm:ss");
          },
          thStyle: {
            display: 'none'
          },
          class: ["text-nowrap", "text-right", "text-monospace", "text-secondary"],
          tdClass: 'setRelatedColor',
        },
        {
          key: 'message',
          sortable: false,
          thStyle: {
            display: 'none'
          },
          class: ["w-100", "text-left", "text-monospace", "text-dark"],
          tdClass: 'setRelatedColor',
        }
      ]
    }
  }
};
</script>
