<script setup>
import {onMounted, ref} from 'vue';
import {ElButton, ElCard, ElMessage, ElTable, ElTableColumn} from 'element-plus';
import service from "../../utils/service.js";

const tableData = ref([]);

const fetchData = async () => {
  try {
    //
    const response = await service.post('/api/advertising-review-data', {
      jwt: localStorage.getItem('jwt')
    });
    if (response.data.code === 200) {
      tableData.value = response.data.data;
    }
  } catch (error) {
    ElMessage.error('获取数据失败' + error.message);
  }
};

const refuseItem = async (id) =>{
  try {
    const response = await service.post('/api/advertising-review-data-false',
        {
          id: id,
          jwt: localStorage.getItem('jwt')
        });
    if (response.data.code === 200) {
      tableData.value = tableData.value.filter(row => row.id !== response.data.id);
    }
  } catch (error) {
    ElMessage.error('获取数据失败' + error.message);
  }
}

const approveItem = async (id) => {
  try {
    const response = await service.post('/api/advertising-review-data-ok',
        {
          id: id,
          jwt: localStorage.getItem('jwt')
        });
    if (response.data.code === 200) {
      tableData.value = tableData.value.filter(row => row.id !== response.data.id);
    }
  } catch (error) {
    ElMessage.error('获取数据失败' + error.message);
  }
};

onMounted(() => {
  fetchData();
});
</script>

<template>
  <el-card class="card">
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="id" label="ID" width="100"/>
      <el-table-column prop="tag" label="标签" width="100"/>
      <el-table-column prop="title" label="标题" width="100"/>
      <el-table-column prop="description" label="描述"/>
      <el-table-column prop="distributor" label="发布商" width="150"/>
      <el-table-column prop="cost" label="价格" width="100"/>
      <el-table-column label="广告资源" width="150">
        <template #default="scope">
          <a :href="scope.row.url" target="_blank">{{ scope.row.url }}</a>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="success" @click="approveItem(scope.row.id)">允许</el-button>
          <el-button type="danger" @click="refuseItem(scope.row.id)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<style scoped>
.card {
  border-radius: 20px;
  margin-bottom: 10px;
  margin-top: 10px;
}
</style>