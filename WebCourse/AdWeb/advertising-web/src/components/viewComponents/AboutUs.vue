<script setup>
import { ElCard, ElDescriptions, ElDescriptionsItem, ElIcon } from 'element-plus';
import { InfoFilled, Location, Phone, Message } from '@element-plus/icons-vue';
</script>

<template>
  <div class="about-us-container">
    <!-- 公司简介 -->
    <el-card class="company-intro">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>公司简介</span>
        </div>
      </template>
      <p>
        我们是一家致力于技术创新和用户体验的公司。自成立以来，我们始终秉持“客户至上，质量为本”的理念，为客户提供高效、可靠的解决方案。
      </p>
    </el-card>

    <!-- 公司信息 -->
    <el-card class="company-info">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>公司信息</span>
        </div>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="公司名称">
          <el-icon><InfoFilled /></el-icon>
          上海理工大学
        </el-descriptions-item>
        <el-descriptions-item label="地址">
          <el-icon><Location /></el-icon>
          上海市杨浦区军工路516号
        </el-descriptions-item>
        <el-descriptions-item label="电话">
          <el-icon><Phone /></el-icon>
          112233445566
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          <el-icon><Message /></el-icon>
          info@innovatech.com
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 团队介绍 -->
    <el-card class="team-intro">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>团队介绍</span>
        </div>
      </template>
      <p>
        广告网站@Asaki0019个人独立开发
      </p>
    </el-card>
  </div>
</template>

<style scoped>
.about-us-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
}

.page-title {
  font-size: 32px;
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.company-intro,
.company-info,
.team-intro {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 10px;
  color: #409EFF;
}

.el-descriptions {
  margin-top: 20px;
}

.el-descriptions-item__label .el-icon {
  margin-right: 5px;
  color: #409EFF;
}
</style>