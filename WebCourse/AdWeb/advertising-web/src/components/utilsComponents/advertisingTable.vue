<script setup>
import {ref} from 'vue';

const propsTable = defineProps({
  data: {
    type: Array,
    required: true,
  },
  operation: {
    type: Boolean,
    required: true,
  },
  isRequest: {
    type: Boolean,
    required: true,
  }
});

const emit = defineEmits(['deleteRow', 'requestRow', 'unRequestRow', 'selectIds']);
const deleteRow = (id) => {
  emit('deleteRow', id); // 触发 deleteRow 事件，传递 id
};
const requestRow = (id) => {
  emit('requestRow', id);
}
const unRequestRow = (id) => {
  emit('unRequestRow', id);
}
const selectedIds = ref([]);

const handleSelectionChange = (selection) => {
  // 如果 isRequest 为 true，过滤掉“已申请”的行
  if (propsTable.isRequest) {
    const filteredSelection = selection.filter(row => row.isRequest !== '已申请');
    selectedIds.value = filteredSelection.map(row => row.id);
  } else {
    selectedIds.value = selection.map(row => row.id);
  }
  // 将过滤后的 selectedIds 传递给父组件
  emit('selectIds', selectedIds.value);
};

function isRequestSelectable(row) {
  return row.isRequest !== '已申请'; // 如果 isRequest 为 "已申请"，则禁用复选框
}

function isReview(row) {
  return row.isRequest !== '审核中';

}

const getRequestColor = (isRequest) => {
  switch (isRequest) {
    case "已申请":
      return "green";
    case "未申请":
      return "gray";
    case "审核中":
      return "red";
    default:
      return "black";
  }
};

const getStatusColor = (status) => {
  switch (status) {
    case "未发布":
      return "gray";
    case "已发布":
      return "green";
    case "审核中":
      return "red";
    default:
      return "black";
  }
};

// 定义 filterTag 函数
const filterTag = (value, row) => {
  return row.tag === value;
};
</script>

<template>
  <el-table
      class="table"
      :row-style="{'height':'100px'}"
      :data="propsTable.data"
      :type="propsTable.operation ? 'selection' : ''"
      @selection-change="handleSelectionChange"
  >
    <el-table-column type="selection" width="55" :selectable="isRequestSelectable"/>
    <el-table-column class="table-item" prop="id" label="广告序号" width="120px" sortable/>
    <el-table-column class="table-item" prop="isRequest" label="申请" width="80px" v-if="propsTable.isRequest">
      <template #default="scope">
        <span
            :style="{
            color: getRequestColor(scope.row.isRequest),
            fontSize: '16px',
            fontWeight: 'bold'
          }"
        >
          {{ scope.row.isRequest }}
        </span>
      </template>
    </el-table-column>
    <el-table-column class="table-item" prop="status" label="状态" width="120px" v-if="propsTable.operation">
      <template #default="scope">
        <span
            :style="{
            color: getStatusColor(scope.row.status),
            fontSize: '16px',
            fontWeight: 'bold'
          }"
        >
          {{ scope.row.status }}
        </span>
      </template>
    </el-table-column>
    <el-table-column class="table-item" prop="tag" label="广告类型" width="100px"
                     :filters="[
        { text: '电子产品', value: '电子产品' },
        { text: '家居用品', value: '家居用品' },
        { text: '服装服饰', value: '服装服饰' },
        { text: '美妆护肤', value: '美妆护肤' },
        { text: '食品饮料', value: '食品饮料' },
        { text: '汽车交通', value: '汽车交通' },
        { text: '旅游出行', value: '旅游出行' },
      ]"
                     :filter-method="filterTag"
                     filter-placement="bottom-end"
    />
    <el-table-column class="table-item" prop="title" label="广告标题" width="100px"/>
    <el-table-column class="table-item" prop="description" label="广告描述"/>
    <el-table-column class="table-item" prop="distributor" label="发布商" width="100px"/>
    <el-table-column class="table-item" prop="cost" label="广告价格" width="120px" sortable/>
    <el-table-column label="操作" width="100px">
      <template v-if="propsTable.operation" #default="scope">
        <el-button type="danger" size="default" @click="deleteRow(scope.row.id)">删除</el-button>
      </template>
      <template v-if="propsTable.isRequest " #default="scope">
        <div v-if="isReview(scope.row)"></div>
        <el-button v-if="isRequestSelectable(scope.row)"
                   type="primary" size="default" @click="requestRow(scope.row.id)">申请
        </el-button>
        <el-button v-else
                   type="danger" size="default" @click="unRequestRow(scope.row.id)">解除
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<style scoped>
.table {
  height: 80%;
  padding-bottom: 20px;
  overflow: hidden;
  background-color: #ffffff;
}
</style>