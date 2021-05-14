<template>
  <b-card class="mt-3" header="Variable Assignments">
    <div v-if="selected_log_uuid.length">
      <div v-if="isFetchingInternalData">
        <b-spinner small label="Small Spinner"></b-spinner>
      </div>
      <div v-else>
        {{ selected_log_uuid }}
        {{ internal_data }}
      </div>
    </div>
    <div v-else>
      Please select a log entry.
    </div>
  </b-card>
</template>

<script>

import {mapFields} from "vuex-map-fields";

export default {
  name: 'Variable Assignments',
  computed: {
    ...mapFields([
      'isFetchingInternalData',
      'selected_log_uuid',
      'internal_data',
    ]),
  }
};
</script>
