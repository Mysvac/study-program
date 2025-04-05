<script setup>
import AdvertisingPie from "../utilsComponents/advertisingPie.vue";
import { onMounted, ref, onUnmounted } from "vue";
import service from "../../utils/service.js";

const chartTitle = ref('广告种类与分布');
const chartData = ref([]);
const advertisingCounts = ref(0);
const advertisingDescriptions = ref([]);
let intervalId = null; // 用于存储定时器 ID

// 获取图表数据
async function fetchCharData() {
  try {
    const response = await service.post('/api/advertising-chart-data', {
      jwt: localStorage.getItem('jwt')
    });
    chartData.value = response.data.data;
    updateDescriptions();
    advertisingCounts.value = response.data.data.reduce(
        (sum, item) => sum + item.value, 0);
  } catch (error) {
    console.error('获取数据失败: ' + error.message);
  }
}

// 静态的类别基本描述
const categoryDescriptions = {
  '电子产品': '电子产品广告通常包括手机、电脑、平板等高科技产品。',
  '家居用品': '家居用品广告涵盖家具、厨具、装饰品等日常生活用品。',
  '服装服饰': '服装服饰广告包括男装、女装、童装、鞋帽等时尚单品。',
  '美妆护肤': '美妆护肤广告涉及化妆品、护肤品、香水等美容产品。',
  '食品饮料': '食品饮料广告包括零食、饮料、保健品等食品相关产品。',
  '汽车交通': '汽车交通广告涵盖汽车、摩托车、电动车等交通工具。',
  '旅游出行': '旅游出行广告包括旅游景点、酒店、机票等旅游相关服务。',
};

// 计算每个广告种类的描述信息
const updateDescriptions = () => {
  advertisingDescriptions.value = chartData.value.map(item => {
    const distributed = item.distributed;
    const isHot = distributed > 4 * item.value ? '热门' : '普通';
    return {
      name: item.name,
      value: item.value,
      distributed: distributed,
      isHot: isHot,
      description: categoryDescriptions[item.name] || '暂无描述', // 获取静态描述
    };
  });
};

// 组件挂载时初始化数据并启动定时器
onMounted(() => {
  fetchCharData(); // 初始化数据
  intervalId = setInterval(fetchCharData, 10000); // 每 10 秒请求一次数据
});

// 组件卸载时清除定时器
onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId); // 清除定时器
  }
});

// 根据状态获取颜色
const getStatusColor = (isHot) => {
  return isHot === '热门' ? 'red' : 'gray';
};
</script>

<template>
  <div class="analysis-page">
    <el-card class="card" id="chart-container" style="width: 100%">
      <h1>本网站总计广告数量：{{ advertisingCounts }} </h1>
      <AdvertisingPie :title="chartTitle" :data="chartData"></AdvertisingPie>
    </el-card>
    <el-card class="card">
      <el-carousel height="150px" motion-blur>
        <el-carousel-item class="advertising-description"
                          v-for="item in advertisingDescriptions" :key="item.name">
          <el-descriptions :title="item.name">
            <el-descriptions-item label="广告数量">{{ item.value }}</el-descriptions-item>
            <el-descriptions-item label="目前发布数量">{{ item.distributed }}</el-descriptions-item>
            <el-descriptions-item label="标记">
              <el-tag size="large" :style="{
                background: getStatusColor(item.isHot),
                color:'white'
              }">{{ item.isHot }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="基本描述">
              {{ item.description }}
            </el-descriptions-item>
          </el-descriptions>
        </el-carousel-item>
      </el-carousel>
    </el-card>
  </div>
</template>

<style scoped>
.analysis-page {
  text-align: center;
  padding: 50px;
  background-color: #f0f0f0;
  margin: 20px;
  height: 100%;
  border-radius: 15px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  gap: 10px;
}

.card {
  border-radius: 20px;
  margin-bottom: 10px;
  margin-top: 10px;
}

#chart-container {
  width: 600px;
  height: 400px;
  margin: auto;
}
</style>