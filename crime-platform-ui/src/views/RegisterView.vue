<template>
  <div class="page-container">
    <el-card>
      <h2>创建用户账号</h2>
      <el-form :model="form" label-width="120px">
        <el-form-item label="用户名" required><el-input v-model.trim="form.username" /></el-form-item>
        <el-form-item label="密码" required><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="确认密码" required><el-input v-model="form.confirmPassword" type="password" /></el-form-item>
        <!-- 取消身份选择，后端将默认分配“警务人员”角色 -->
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onRegister">创建</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import apiClient from '@/api/index.js';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const form = ref({ username: '', password: '', confirmPassword: '' });

const onRegister = async () => {
  loading.value = true;
  try {
    if (!form.value.username || !form.value.password) {
      ElMessage.warning('请输入用户名和密码');
      return;
    }
    if (form.value.password !== form.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致');
      return;
    }
    const payload = { username: form.value.username, password: form.value.password };
    await apiClient.post('/auth/register', payload);
    ElMessage.success('创建成功，请登录');
    router.push('/login');
  } catch (e) {
    const msg = e?.response?.data?.message || '创建失败，请检查信息';
    ElMessage.error(msg);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.page-container { max-width: 800px; margin: 20px auto; }
</style>


