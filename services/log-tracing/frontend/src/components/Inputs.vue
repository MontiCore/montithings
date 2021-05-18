<template>
  <b-card class="mt-3" header="Inputs">
    <div v-if="selected_log_uuid.length">
      <div v-if="isFetchingInternalData">
        <b-spinner small label="Small Spinner"></b-spinner>
      </div>
      <div v-else>
        <vue-json-pretty :path="'res'" :data="internal_data.inputs" @click="handleClick"> </vue-json-pretty>
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
  name: 'Inputs',
  computed: {
    ...mapFields([
      'isFetchingInternalData',
      'selected_log_uuid',
      'internal_data',
    ]),
  }
};
</script>
