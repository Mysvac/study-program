<script setup>
import {onMounted, onUnmounted, ref, watch} from 'vue';
import service from "../../utils/service.js";
import AdvertisingTable from "../utilsComponents/advertisingTable.vue";
import AdvertisingSearch from "../utilsComponents/advertisingSearch.vue";
import {InfoFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const tableData = ref([]);
const filteredTableData = ref([]);
const pagedTableData = ref([]); // 分页后的数据
const currentPage = ref(1); // 当前页码
const pageSize = ref(8); // 每页显示的条数
const adCost = ref(0);

function updateCost() {
  adCost.value = tableData.value.reduce(
      (sum, item) => sum +
          (item.isRequest === '未申请' ? 0 : item.cost), 0);
}

// 获取表格数据
async function fetchTableData() {
  try {
    //
    const response = await service.post('/api/advertising-table-data', {
      jwt: localStorage.getItem('jwt')
    });
    if (Array.isArray(response.data.data)) {
      tableData.value = response.data.data;
      updateCost();
    } else {
      console.error(response.data.message);
    }
    filterTableData(); // 初始化过滤数据
  } catch (e) {
    console.error('获取表格数据失败:', e.message);
  }
}

const selectedIds = ref([]);
const showForm = ref(false);
const handleSelectionChange = (ids) => {
  selectedIds.value = ids; // 更新选中的 id
};

async function conveyAdvertisingToRequest() {
  const length = selectedIds.value.length
  const ids = [];
  for (let i = 0; i < length; i++) {
    ids.push(selectedIds.value[i].id);
  }
  for (let i = 0; i < ids.length; i++) {
    await requestRow(ids[i]);
  }
}

async function unRequestRow(index) {
  try {
    const response = await service.post('/api/unRequest-advertising', {
      jwt: localStorage.getItem('jwt'),
      id: index
    });
    const json = response.data;
    if (json.code === 200) {
      const rowIndex = tableData.value.findIndex(row => row.id === index);
      if (rowIndex !== -1) {
        tableData.value[rowIndex].isRequest = '未申请'; // 更新状态
        updateCost()
      }
    } else {
      ElMessage.error('广告解除失败:' + response.data.message);
    }
  } catch (e) {
    ElMessage.error(e.message);
  }
}

async function requestRow(index) {
  try {
    const response = await service.post('/api/request-advertising',
        {
          id: index,
          jwt: localStorage.getItem('jwt')
        });
    const json = response.data;
    if (json.code === 200) {
      const rowIndex = tableData.value.findIndex(row => row.id === index);
      if (rowIndex !== -1) {
        tableData.value[rowIndex].isRequest = '已申请'; // 更新状态
        updateCost()
      }
    } else {
      ElMessage.error('广告申请失败:' + response.data.message);
    }
  } catch (e) {
    ElMessage.error(e.message);
  }
}

const filterTableData = (searchText = "") => {
  if (searchText === "") {
    filteredTableData.value = tableData.value;
  } else {
    filteredTableData.value = tableData.value.filter(row => {
      return (
          row.id.toString().includes(searchText) ||
          row.tag.toLowerCase().includes(searchText) || // 根据广告类型搜索
          row.distributor.toLowerCase().includes(searchText)
      );
    });
  }
  handlePageChange(); // 过滤后重新分页
};

// 处理分页逻辑
const handlePageChange = () => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  pagedTableData.value = filteredTableData.value.slice(start, end);
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
  handlePageChange();
};

// 处理每页显示条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  handlePageChange();
};

let intervalId = null;

onMounted(() => {
  fetchTableData(); // 初始化数据
  intervalId = setInterval(fetchTableData, 10000); // 每 10 秒拉取一次数据
});

onUnmounted(() => {
  clearInterval(intervalId); // 组件卸载时清除定时器
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
      <el-popconfirm
          v-if="showForm === false"
          confirm-button-text="确定"
          cancel-button-text="取消"
          :icon="InfoFilled"
          icon-color="#626AEF"
          title="确定申请?"
          @confirm="conveyAdvertisingToRequest"
      >
        <template #reference>
          <el-button class="button" type="primary" size="large">
            申请广告
          </el-button>
        </template>
      </el-popconfirm>
      <div class="demo-pagination-block">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            layout="prev, pager, next, jumper"
            :total="filteredTableData.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <h4>申请金额：{{ adCost }}</h4>
    <AdvertisingTable :data="pagedTableData"
                      :operation="false"
                      :is-request="true"
                      @requestRow="requestRow"
                      @unRequestRow="unRequestRow"
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