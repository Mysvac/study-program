<script setup>
import {onMounted, ref} from "vue";
import service from "../../utils/service.js";
import {ElMessage} from "element-plus";

// 定义广告数据
const ads = ref([]);
const errorMessage = ref("");
const apiUrl = ref("/api/fetch-request-ads"); // 替换为实际的 API URL
const clickUrl = ref("/api/ad-click")
const fetchCode = ref("");
const localUserCookie = ref("");
localUserCookie.value = localStorage.getItem('cookie') || null;
// 获取广告数据的方法
const fetchAds = async () => {
  try {
    const response = await service.post("/api/fetch-request-ads", {
      userCookie: localUserCookie.value,
    });
    if (response.data.code === 200) {
      ads.value = response.data.data; // 将广告数据赋值给 ads
    } else {
      errorMessage.value = response.data.message; // 显示错误信息
    }
  } catch (error) {
    console.error("获取广告数据失败:", error);
    errorMessage.value = "网络请求失败，请稍后重试";
  }
};

// 生成 fetch 代码
const generateFetchCode = () => {
  fetchCode.value = `
fetch('http://10.100.164.22:8080${clickUrl.value}', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
   body: JSON.stringify({
    client_id: your_website_user_uuid,
    user_id: "${localUserCookie.value}",
    tag: click tags
  }),
})
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
`
  ;
};


// 复制 fetch 代码到剪贴板
const copyFetchCode = () => {
  navigator.clipboard
      .writeText(fetchCode.value)
      .then(() => {
        ElMessage.success("代码已复制到剪贴板！");
      })
      .catch(() => {
        ElMessage.error("复制失败，请手动复制代码。");
      });
};

// 下载 fetch 代码
const downloadFetchCode = () => {
  const blob = new Blob([fetchCode.value], {type: "text/plain"});
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "fetch_api_code.txt";
  a.click();
  URL.revokeObjectURL(url);
};

// 组件挂载时调用 fetchAds
onMounted(() => {
  fetchAds();
  generateFetchCode();
});
</script>

<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>广告申请数据</span>
        <el-button type="primary" @click="generateFetchCode">
          生成 API 调用代码
        </el-button>
      </div>
    </template>

    <!-- 错误信息提示 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- 广告数据表格 -->
    <el-table :data="ads" style="width: 100%">
      <el-table-column prop="id" label="ID" width="180"/>
      <el-table-column prop="tags" label="标签" width="80"/>
      <el-table-column prop="title" label="标题" width="120"/>
      <el-table-column prop="description" label="描述"/>
      <el-table-column prop="fileId" label="广告资源链接" width="280">
        <template #default="scope">
          <a :href="scope.row.fileId" target="_blank">{{ scope.row.fileId }}</a>
        </template>
      </el-table-column>
    </el-table>

    <!-- API 调用代码展示 -->
    <div class="api-code-section">
      <h3>API 调用代码</h3>
      <pre>{{ fetchCode }}</pre>
      <div class="buttons">
        <el-button type="primary" @click="copyFetchCode">复制代码</el-button>
        <el-button type="success" @click="downloadFetchCode">下载代码</el-button>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-bottom: 10px;
}

.api-code-section {
  margin-top: 20px;
  border: 1px solid #ccc;
  padding: 10px;
  background-color: #f9f9f9;
}

.api-code-section h3 {
  margin-bottom: 10px;
}

.api-code-section pre {
  white-space: pre-wrap;
  font-family: monospace;
  background-color: #eee;
  padding: 10px;
  border-radius: 4px;
}

.buttons {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}
</style>