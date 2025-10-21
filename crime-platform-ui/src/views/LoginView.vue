<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">上海市犯罪管理平台</h2>
      <el-form :model="form" @keyup.enter="onLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>没有账号？</span>
        <el-link type="primary" @click="goRegister">去注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import apiClient from '@/api/index.js';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const form = ref({ username: '', password: '' });
const loading = ref(false);

const onLogin = async () => {
  loading.value = true;
  try {
    // 期待后端提供 /auth/login 返回 { token }
    const res = await apiClient.post('/auth/login', form.value);
    const token = res.data?.token;
    if (token) {
      localStorage.setItem('auth_token', token);
      ElMessage.success('登录成功');
      router.push('/dashboard');
    } else {
      throw new Error('未收到token');
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查用户名或密码');
  } finally {
    loading.value = false;
  }
};

const goRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container { display: flex; align-items: center; justify-content: center; height: 100vh; background: #f0f2f5; }
.login-card { width: 360px; }
.title { text-align: center; margin-bottom: 16px; }
.footer { display: flex; justify-content: center; gap: 6px; margin-top: 8px; }
</style>


