<template>
<div>
  <b-card class="mt-3" header="Variable Assignments" v-if="data.length > 0">
    
      <div v-if="isFetchingInternalData">
        <b-spinner small label="Small Spinner"></b-spinner>
      </div>
      <div v-else>
        <samp v-for="v in data"
               :key="v">{{v.key}} = {{v.value}}; </samp>
      </div>

  </b-card>
</div>
</template>

<script>

import {mapFields} from "vuex-map-fields";

export default {
  name: 'Variable-Assignments',
  computed: {
    ...mapFields([
      'isFetchingInternalData',
      'selected_log_uuid',
      'internal_data',
    ]),
    data: function() {
      if(this.internal_data.var_snapshot) {
        return JSON.parse(this.internal_data.var_snapshot).value0;
      } else {
        return [];
      }
    }
  }
};
</script>
