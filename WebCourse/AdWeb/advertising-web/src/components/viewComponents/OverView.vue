<script setup>
import StyleBoard from "../utilsComponents/StyleBoard.vue";
import {ref} from 'vue'

const activeName = ref('1')
const imagItems = ref([
  {id: 0, title: "轮播图1", url: 'collapseImages/【哲风壁纸】下雨-商店-绿色护眼.png'},
  {id: 1, title: "轮播图2", url: 'collapseImages/【哲风壁纸】动漫女孩-夹克-常设.png'},
  {id: 2, title: "轮播图3", url: 'collapseImages/【哲风壁纸】漫画风大草原-绿色护眼.png'}
])
</script>

<template>
  <div class="overview-page">
    <h2>概览</h2>
    <div class="styleBoard-container">
      <StyleBoard/>
      <p>网站访问人数面板</p>
    </div>
    <div class="overView-description-container">
      <el-collapse class="collapse-column" v-model="activeName" accordion>
        <el-collapse-item title="仪表盘" name="1">
          <div class="collapse-item">
            广告的数据分析页面展示了广告的详细数据，包括用户行为分析、广告效果趋势图以及不同广告位的对比分析。
          </div>
        </el-collapse-item>
        <el-collapse-item class="collapse-column" title="广告管理" name="2">
          <div class="collapse-item">
            广告管理页面允许用户创建、编辑和删除广告活动，支持批量操作和广告状态的实时更新。
          </div>
          <div class="collapse-item">
            发布广告页面提供了广告素材的上传功能，支持多种广告格式（如图片、视频、文字链），并可设置广告的投放时间和目标受众。
          </div>
          <div class="collapse-item">
            用户申请广告页面展示了用户提交的广告申请，管理员可以审核申请并决定是否批准广告投放。
          </div>
        </el-collapse-item>
        <el-collapse-item class="collapse-column" title="用户管理" name="3">
          <div class="collapse-item">
            用户管理页面用于展示用户信息。
          </div>
        </el-collapse-item>
        <el-collapse-item class="collapse-column" title="关于" name="4">
          <div class="collapse-item">
            关于我们页面提供了平台的背景信息、团队介绍以及联系方式，帮助用户更好地了解我们的服务和愿景。
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <el-carousel height="400px" motion-blur
                 style="border-radius: 10px;
                        margin-top: 20px">
      <el-carousel-item v-for="item in imagItems" :key="item.id">
        <img :src="item.url" style="object-fit: cover; height:100%;width:100%;" alt/><span>{{ item.title }}</span>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<style scoped>
.overview-page {
  text-align: center;
  height: fit-content;
  padding: 50px;
  background-color: #f0f0f0;
  margin: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h1 {
  color: #333;
  margin-bottom: 20px;
}

p {
  color: #666;
}

/* 容器样式 */
.overView-description-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}


/* 折叠项标题样式 */
.collapse-column {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  background-color: #f0f0f0;
  transition: background-color 0.3s ease;
}


/* 折叠项内容文本样式 */
.collapse-item {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 10px;
}

.collapse-item:last-child {
  margin-bottom: 0;
}

</style>