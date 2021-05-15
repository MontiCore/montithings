<template>
  <div>
    <b-table borderless small hover selectable show-empty
             :items="log_entries"
             :fields="fields"
             :busy="isFetchingLogs"
             :sort-by.sync="sortBy"
             :sort-desc.sync="sortDesc"
             :select-mode.sync="selectMode"
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
      'isFetchingInternalData'
    ]),
  },
  methods: {
    // eslint-disable-next-line no-unused-vars
    onRowClick(record, index) {
      store.state.selected_log_uuid = record.log_uuid;
      store.state.isFetchingInternalData = true;
      store.dispatch('getInternalData',
          { log_uuid: record.log_uuid,
            input_uuid: record.input_uuid,
            output_uuid: record.output_uuid  })
    }
  },
  data() {
    return {
      sortBy: 'time',
      sortDesc: false,
      selectMode: 'single',
      fields: [
        {
          key: 'time',
          sortable: true,
          sortByFormatted: false,
          formatter: ts => {
            return moment.unix(ts).format("DD.MM.YYYY HH:mm:ss");
          },
          thStyle: {
            display: 'none'
          },
          tdClass: ["text-nowrap", "text-right", "text-monospace", "text-secondary"],
        },
        {
          key: 'message',
          sortable: false,
          thStyle: {
            display: 'none'
          },
          tdClass: ["w-100", "text-left", "text-monospace", "text-dark"],
        }
      ]
    }
  }
};
</script>
