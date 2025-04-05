<script setup>
import {onMounted, ref, watch} from 'vue';
import AdvertisingTable from "../utilsComponents/advertisingTable.vue";
import service from "../../utils/service.js";
import AdvertisingSearch from "../utilsComponents/advertisingSearch.vue";
import {InfoFilled} from "@element-plus/icons-vue";
import AdvertisingCreatedForm from "../utilsComponents/advertisingCreatedForm.vue";
import {ElMessage} from "element-plus";


const tableData = ref([]);
const filteredTableData = ref([]);

async function fetchTableData() {
  try {
    //
    const response = await service.post('/api/advertising-id-table-data', {
      jwt: localStorage.getItem('jwt')
    });
    if (Array.isArray(response.data.data)) {
      tableData.value = response.data.data;
    } else {
      ElMessage.error(response.data.message) // 如果数据不是数组，则初始化为空数组
    }
    //tableData.value = response.data.data;
    filterTableData();
  } catch (e) {
    ElMessage.error('获取表格数据失败:' + e.message);
  }
}

const selectedIds = ref([]);

const handleSelectionChange = (ids) => {
  selectedIds.value = ids; // 更新选中的 id
};

async function deleteRows() {
  const length = selectedIds.value.length
  const ids = [];
  for (let i = 0; i < length; i++) {
    ids.push(selectedIds.value[i].id);
  }
  for (let i = 0; i < ids.length; i++) {
    await deleteRow(ids[i]);
  }
}

async function deleteRow(index) {
  try {
    const response = await service.post('/api/delete-advertising',
        {
          id: index,
          jwt: localStorage.getItem('jwt')
        });
    const json = response.data;
    if (json.code === 200) {
      ElMessage.success("广告删除成功");
      tableData.value = tableData.value.filter(row => row.id !== index);
      filterTableData();
    } else {
      ElMessage.error('广告删除失败:' + response.data.message);
    }
  } catch (e) {
    ElMessage.error(e.message);
  }
}

const filterTableData = (searchText) => {
  if (searchText === "") {
    filteredTableData.value = tableData.value;
  } else {
    filteredTableData.value = tableData.value.filter(row => {
      return (
          row.id.toString().includes(searchText) ||
          row.tag.toLowerCase().includes(searchText) || // 根据广告类型搜索
          row.distributor.toLowerCase().includes(searchText))
    });
  }
};

const showForm = ref(false);

const onAddItem = () => {
  showForm.value = !showForm.value;
};

let intervalId = null;
onMounted(() => {
  fetchTableData();
  intervalId = setInterval(fetchTableData, 10000); // 每 10 秒拉取一次数据
});

watch(
    () => tableData.value,
    () => {
      filterTableData();
    }
);
</script>

<template>
  <el-card class="card">
    <div class="header-row">
      <AdvertisingSearch @search="filterTableData"/>
      <div class="header-row-button">
        <el-popconfirm
            v-if="showForm === false"
            confirm-button-text="确定"
            cancel-button-text="取消"
            :icon="InfoFilled"
            icon-color="#626AEF"
            title="确定使用批量删除?"
            @confirm="deleteRows"
        >
          <template #reference>
            <el-button class="button" type="danger" size="large">
              批量删除
            </el-button>
          </template>
        </el-popconfirm>
        <el-button class="button" type="primary" @click="onAddItem" size="large">
          <i class="fa-solid fa-plus" v-if="!showForm"/>
          <i class="fa-solid fa-minus" v-else/>
        </el-button>
      </div>
    </div>
    <AdvertisingCreatedForm v-if="showForm"/>
    <AdvertisingTable v-else
                      :data="filteredTableData"
                      :operation="true"
                      :is-request="false"
                      @deleteRow="deleteRow"
                      @selectionChange="handleSelectionChange"/>
  </el-card>

</template>

<style scoped>
.card {
  border-radius: 20px;
  margin-bottom: 10px;
  margin-top: 10px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>