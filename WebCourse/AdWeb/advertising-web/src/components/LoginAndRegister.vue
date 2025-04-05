<script setup>

import {ref} from "vue";
import {useRouter} from "vue-router";
import service from "../utils/service.js";
import {ElMessage} from "element-plus";

const router = useRouter()

const isLoginModel = ref(true);
const username = ref("");
const password = ref("");
const verifiedPassword = ref("");
const name = ref("");

const switchLoginModel = () => {
  isLoginModel.value = !isLoginModel.value;
  username.value = "";
  password.value = "";
}

async function handleLogin() {
  try {
    const response = await service.post('/api/login', {
      username: username.value,
      password: password.value
    });
    const json = response.data;
    if (json.code === 200) {
      ElMessage.success("登录成功")
      localStorage.setItem('role', json.data.role);
      localStorage.setItem('name', json.data.name);
      localStorage.setItem('cookie', json.data.cookie);
      localStorage.setItem('jwt', json.data.jwt);
      await router.replace('/mainView');
    } else {
      ElMessage.error(json.data.message);
    }
  } catch (e) {
    ElMessage.error("账号或密码错误");
  }
}

async function handleRegister() {
  if (password.value !== verifiedPassword.value) {
    ElMessage.error('两次输入的密码不一致');
    return;
  }
  try {
    const response = await service.post('/api/register', {
      username: username.value,
      name: name.value,
      password: password.value,
      verifiedPassword: verifiedPassword.value
    });
    if (response.data.code === 200) {
      ElMessage.success("注册成功");
      switchLoginModel();
    } else {
      ElMessage.error("注册失败");
    }
  } catch (error) {
    await router.replace('/404View')
  }
}
</script>

<template>
  <div class="background">
    <transition name="fade"
                enter-active-class="animate__animated animate__fadeIn"
                leave-active-class="animate__animated animate__fadeOut"
    >
      <img
          :key="isLoginModel ? 'login' : 'register'"
          class="background-image"
          :src="isLoginModel ? 'loginBack.png' : 'registerBack.png'"
          alt="background"
      />
    </transition>
    <div class="login-register-container">
      <div class="form-container">
        <h2>{{ isLoginModel ? '登录' : '注册' }}</h2>
        <form>
          <!-- 账号输入 -->
          <div class="form-group">
            <input type="text" id="username"
                   v-model="username" required
                   placeholder="请输入账号："/>
          </div>

          <!-- 用户名输入（仅在注册时显示） -->
          <div v-if="!isLoginModel" class="form-group">
            <input type="text" id="name" v-model="name" required
                   placeholder="请输入用户名："/>
          </div>

          <!-- 密码输入 -->
          <div class="form-group">
            <input type="password" id="password" v-model="password" required
                   placeholder="请输入密码："/>
          </div>

          <!-- 确认密码输入（仅在注册时显示） -->
          <div v-if="!isLoginModel" class="form-group">
            <input type="password" id="verified-password"
                   v-model="verifiedPassword" required
                   placeholder="请确认密码"/>
          </div>

          <!-- 按钮和链接 -->
          <div class="form-actions">
            <button v-if="isLoginModel" type="button" @click.prevent="handleLogin">登录</button>
            <button v-else type="button" @click.prevent="handleRegister">注册</button>
            <a href="#" @click.prevent="switchLoginModel">
              {{ isLoginModel ? '没有账号？点击注册！' : '已有账号，点击转到登录页面' }}
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.background {
  position: fixed; /* 或 absolute，取决于您的需求 */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden; /* 防止图片溢出 */
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片覆盖整个区域而不被拉伸 */
  transition: opacity 0.5s ease-in-out, transform 0.5s ease-in-out; /* 添加过渡效果 */
}

.login-register-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 400px;
  min-width: 300px;
  height: 400px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.form-container {
  display: flex;
  flex-direction: column;
}

.form-group {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.form-group input {
  flex: 2;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: rgba(255, 255, 255, 0.9);
  color: #1a1a1a;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

input::placeholder {
  color: black;
  font-size: 14px;
}

.form-actions {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

button {
  padding: 10px 15px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  background-color: #007bff;
  color: white;
}

button:hover {
  background-color: #0056b3;
}

a {
  color: #007bff;
  text-decoration: none;
  cursor: pointer;
}

a:hover {
  text-decoration: underline;
}

</style>
