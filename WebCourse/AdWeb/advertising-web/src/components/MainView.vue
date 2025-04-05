<script setup>
import {onMounted, ref} from "vue";
import OverView from "./viewComponents/OverView.vue";
import DataAnalysis from "./viewComponents/DataAnalysis.vue";
import AdvertisementList from "./viewComponents/AdvertisementList.vue";
import YourAdvertisement from "./viewComponents/YourAdvertisement.vue";
import UserList from "./viewComponents/UserList.vue";
import UserAdvertisementRequest from "./viewComponents/UserAdvertisementRequest.vue";
import AboutUs from "./viewComponents/AboutUs.vue";
import router from "../router/index.js";
import AdvertisingReview from "./viewComponents/AdvertisingReview.vue";
import service from "../utils/service.js";
import {ElMessage} from "element-plus";
import printJsonToConsole from "../utils/printJsonToConsole.js";

const name = ref(localStorage.getItem('name'));
const role = ref(localStorage.getItem('role'));
const avatarUrl = ref('avatar.jpg');
const activeIndex = ref('1-1');
const components = {
  '1-1': OverView,
  '1-2': DataAnalysis,
  '2-1': AdvertisementList,
  '2-2': YourAdvertisement,
  '2-3': UserAdvertisementRequest,
  '2-4': AdvertisingReview,
  '3-1': UserList,
  '4-1': AboutUs
};

async function checkLoginStatus() {
  try {
    const response = await service.post('/api/verifiedUser', {
      jwt: localStorage.getItem('jwt')
    });
    printJsonToConsole(response.data);
    if (!response.data.code) {
      ElMessage.error("你似乎没有登录捏！！！");
      await router.replace('/');
    }
  } catch (error) {
    await router.replace('/404View');
  }
}

async function Logout() {
  try {
    const response = await service.get('/api/exit');
    printJsonToConsole(response.data)
    if (response.data.code === 200) {
      localStorage.clear();
    } else {
      ElMessage.error("error" + response.data.message);
    }
    await router.replace('/');
  } catch (e) {
    console.log(e);
    await router.replace('/');
  }
}

// 点击菜单项时更新激活的菜单项
function handleMenuClick(index) {
  activeIndex.value = index;
}

onMounted(() => {
  checkLoginStatus();
});
</script>

<template>
  <div class="common-layout">
    <el-container class="layout-container-main">
      <el-aside class="sidebar" width="200px">
        <el-scrollbar class="sidebar-scrollbar">
          <el-menu class="sidebar-menu" :default-active="activeIndex">
            <!-- 仪表盘 -->
            <el-sub-menu class="menu-item" index="1">
              <template #title>
                <el-icon><i class="fas fa-home"></i></el-icon>
                仪表盘
              </template>
              <el-menu-item-group>
                <el-menu-item class="menu-item" index="1-1"
                              @click="handleMenuClick('1-1')">概览
                </el-menu-item>
                <el-menu-item class="menu-item" index="1-2"
                              @click="handleMenuClick('1-2')">数据分析
                </el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>

            <!-- 广告管理 -->
            <el-sub-menu class="menu-item" index="2">
              <template #title>
                <el-icon><i class="fa-brands fa-algolia"></i></el-icon>
                广告管理
              </template>
              <el-menu-item-group>
                <el-menu-item class="menu-item" index="2-1"
                              @click="handleMenuClick('2-1')">广告列表
                </el-menu-item>
                <el-menu-item class="menu-item" index="2-2"
                              @click="handleMenuClick('2-2')">你的广告
                </el-menu-item>
                <el-menu-item class="menu-item" index="2-3"
                              @click="handleMenuClick('2-3')">广告申请
                </el-menu-item>
                <el-menu-item v-if="role === 'admin'"
                              class="menu-item" index="2-4"
                              @click="handleMenuClick('2-4')">广告审核
                </el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>

            <!-- 用户管理 -->
            <el-sub-menu class="menu-item" index="3">
              <template #title>
                <el-icon><i class="fa-solid fa-user"></i></el-icon>
                用户管理
              </template>
              <el-menu-item class="menu-item" index="3-1" @click="handleMenuClick('3-1')">
                用户
              </el-menu-item>
            </el-sub-menu>

            <!-- 关于 -->
            <el-sub-menu class="menu-item" index="4">
              <template #title>
                <el-icon><i class="fa-solid fa-bars"></i></el-icon>
                关于
              </template>
              <el-menu-item class="menu-item" index="4-1" @click="handleMenuClick('4-1')">关于我们</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <el-container>
        <el-header class="header" style="text-align: right; font-size: 12px">
          <div class="toolbar">
            <el-dropdown class="dropdown">
              <el-icon style="margin-right: 18px">
                <i class="fa-solid fa-gear"></i>
              </el-icon>
              <template #dropdown>
                <el-dropdown-menu class="dropdown-menu">
                  <el-dropdown-item class="dropdown-item">Edit</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <span>{{ name }}</span>
            <div class="block">
              <el-avatar :size="50" :src="avatarUrl"
                         style="margin-top: 20px; margin-left: 10px"/>
            </div>
          </div>
        </el-header>
        <el-main>
          <component :is="components[activeIndex]"></component>
        </el-main>
        <div class="float-button">
          <el-button type="primary" @click="Logout">
            <i class="fa-solid fa-right-from-bracket"></i>
          </el-button>
        </div>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.layout-container-main {
  position: relative;
  background-color: whitesmoke;
  color: gray;
  height: 100vh;
}

.sidebar {
  color: gray;
  background: whitesmoke;
}

.sidebar-scrollbar {
  overflow-y: auto;
}

.toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
}

.sidebar-menu {
  border-right: none;
}

.menu-item {
  color: #333;
  font-size: 14px;
  transition: background-color 0.3s;
}

.header {
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  line-height: 60px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.dropdown {
  cursor: pointer;
}

.dropdown-menu {
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dropdown-item {
  color: #333;
}

.dropdown-item:hover {
  background-color: #f0f0f0;
}

.float-button {
  position: fixed;
  bottom: 30px;
  right: 50px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out; /* 平滑过渡效果 */
  z-index: 1000;
}
</style>