<script setup>
import {onMounted, ref} from 'vue';
import {ElCard, ElCarousel, ElCarouselItem, ElDescriptions, ElDescriptionsItem} from 'element-plus';

const imagItems = ref([
  {id: 0, title: "轮播图1", url: 'userInfoImages/【哲风壁纸】佐贺（明日方舟）-动漫女孩.png'},
  {id: 1, title: "轮播图2", url: 'userInfoImages/【哲风壁纸】凌（明日方舟）-动漫女孩.png'},
  {id: 2, title: "轮播图3", url: 'loginBack.png'},
  {id: 3, title: "轮播图4", url: 'userInfoImages/【哲风壁纸】秋天-蜡笔小新.png'}
])

const name = ref('');
const role = ref('');
const cookie = ref('');
onMounted(() => {
  // 从 localStorage 中获取用户信息
  name.value = localStorage.getItem('name') || '未知用户';
  role.value = localStorage.getItem('role') || '未知角色';
  cookie.value = localStorage.getItem('cookie') || '未知密钥';
});

// 获取当前时间
const currentTime = ref(new Date().toLocaleTimeString());
setInterval(() => {
  currentTime.value = new Date().toLocaleTimeString();
}, 1000);
</script>

<template>
  <!-- 走马灯独立展示 -->
  <el-card class="carousel-card">
    <el-carousel height="400px"
                 motion-blur
                 style="border-radius: 10px;
                        margin-top: 20px">
      <el-carousel-item v-for="item in imagItems" :key="item.id">
        <img :src="item.url" style="object-fit: cover; height:100%;width:100%;" alt/><span>{{ item.title }}</span>
      </el-carousel-item>
    </el-carousel>
  </el-card>

  <!-- 用户信息和时间日历模块 -->
  <el-card class="user-info-card">
    <div class="content-wrapper">
      <!-- 用户信息 -->
      <div class="user-info">
        <el-descriptions title="用户详情" direction="vertical" border>
          <el-descriptions-item label="用户名">
            {{ name }}
          </el-descriptions-item>
          <el-descriptions-item label="角色">
            {{ role }}
          </el-descriptions-item>
          <el-descriptions-item label="Cookie">
            {{ cookie }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

    </div>
  </el-card>
</template>

<style scoped>
.carousel-card {
  width: 100%;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-info-card {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.content-wrapper {
  display: flex;
  justify-content: space-between;
}

.user-info {
  flex: 1;
  margin-right: 20px;
}

.right-modules {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.time-module {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.time {
  font-weight: bold;
  color: #409EFF;
}

.calendar-module {
  width: 100%;
  max-width: 300px;
}

.carousel-text {
  color: #fff;
  font-size: 24px;
  line-height: 200px;
  text-align: center;
  background: #409EFF;
}

.el-carousel__item:nth-child(2n) {
  background-color: #409EFF;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #67C23A;
}
</style>