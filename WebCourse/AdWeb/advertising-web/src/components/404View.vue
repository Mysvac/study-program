<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
const goToHome = async () => {
  await router.replace('/'); // 假设首页的路由名称为 'home'
};
</script>

<template>
  <div class="not-found-container">
    <el-result
        icon="error"
        title="404"
        sub-title="抱歉，您访问的页面不存在"
    >
      <template #extra>
        <el-button type="primary" @click="goToHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
</style>