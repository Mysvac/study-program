<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import service from "../utils/service.js";
import { ElMessage } from "element-plus";

// 获取路由参数中的广告 ID
const route = useRoute();
const adId = route.params.adId; // 从路由参数中获取 adId

// 广告数据
const adData = ref(null);

// 类型映射函数
function mapFileType(fileType) {
  if (fileType.startsWith("image/")) {
    return "image";
  } else if (fileType.startsWith("video/")) {
    return "video";
  } else {
    return "document";
  }
}

// 获取广告详情
async function fetchAdDetails() {
  try {
    const response = await service.get(`/api/show-ad`, {
      params: { adId }, // 传递广告 ID 作为查询参数
    });

    if (response.data.code === 200) {
      // 获取广告数据
      const data = response.data.data;

      // 映射文件类型
      data.fileType = mapFileType(data.fileType);

      // 更新广告数据
      adData.value = data;
    } else {
      ElMessage.error("Failed to fetch advertisement details");
    }
  } catch (error) {
    console.error("Error fetching advertisement details:", error);
    ElMessage.error("An error occurred while fetching advertisement details");
  }
}

// 在组件挂载时获取广告详情
onMounted(() => {
  if (!adId) {
    ElMessage.error("广告 ID 不存在");
    return;
  }
  fetchAdDetails();
});
</script>

<template>
  <div class="ad-detail-container">
    <el-card v-if="adData" class="ad-card">
      <!-- 广告标题 -->
      <h2 class="ad-title">{{ adData.title }}</h2>

      <!-- 广告标签 -->
      <p class="ad-tags">
         {{ adData.tags }}
      </p>

      <!-- 广告描述 -->
      <p class="ad-description">
        {{ adData.description }}
      </p>

      <!-- 广告资源展示 -->
      <div class="ad-resource">
        <strong>广告资源:</strong>
        <div v-if="adData.fileType === 'image'" class="resource-image">
          <img :src="adData.fileUrl" alt="广告图片" />
        </div>
        <div v-else-if="adData.fileType === 'video'" class="resource-video">
          <video controls :src="adData.fileUrl"></video>
        </div>
        <div v-else-if="adData.fileType === 'document'" class="resource-document">
          <a :href="adData.fileUrl" target="_blank">查看文档</a>
        </div>
        <div v-else>
          暂无资源
        </div>
      </div>
    </el-card>
    <el-card v-else class="loading-card">
      加载中...
    </el-card>
  </div>
</template>

<style scoped>
.ad-detail-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.ad-card {
  width: 600px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.ad-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.ad-tags,
.ad-description {
  font-size: 16px;
  margin-bottom: 10px;
}

.ad-resource {
  margin-top: 20px;
}

.resource-image img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.resource-video video {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.resource-document a {
  color: #409eff;
  text-decoration: underline;
}

.loading-card {
  width: 300px;
  text-align: center;
  font-size: 16px;
  color: #999;
}
</style>