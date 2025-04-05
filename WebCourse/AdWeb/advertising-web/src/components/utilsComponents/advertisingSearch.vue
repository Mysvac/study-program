<script setup>
import {ref, watchEffect} from 'vue';
const emit = defineEmits(['search']);
const input = ref('');
watchEffect(() => {
  const searchText = input.value.trim().toLowerCase();
  emit('search', searchText);
})
</script>

<template>
  <div class="search">
    <i class="fa-solid fa-magnifying-glass"></i>
    <el-input
        v-model="input"
        style="width: 240px; margin-left: 10px"
        placeholder="Please input"
        clearable
    />
  </div>
</template>

<style scoped>
.search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>