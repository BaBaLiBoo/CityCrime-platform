<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">上海市犯罪事件管理平台</div>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#304156"
        text-color="#bfcbd9"
        :router="true"
        :default-active="activeMenu"
      >
        <el-menu-item index="/home">
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/dashboard">
          <span>数据仪表盘</span>
        </el-menu-item>
        <el-sub-menu index="/cases-management">
          <template #title><span>案件管理</span></template>
          <el-menu-item index="/cases">案件列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/personnel-management">
          <template #title><span>人员管理</span></template>
          <el-menu-item index="/persons">人员列表</el-menu-item>
          <el-menu-item index="/officers">警员列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <div class="main-content-wrapper">
      <el-header>
        <div class="header-content">
          <div class="user-info">欢迎您，管理员</div>
          <el-button type="danger" size="small" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </div>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();
const activeMenu = computed(() => route.path);

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 清除本地存储的认证信息
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_info');
    
    ElMessage.success('已退出登录');
    router.push('/login');
  } catch (error) {
    // 用户取消退出
  }
};
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex; /* 确保flex布局生效 */
}

.el-aside {
  background-color: #304156;
  color: #fff;
  flex-shrink: 0; /* 防止侧边栏被压缩 */
}

.main-content-wrapper {
  flex-grow: 1; /* 让主内容区占据所有剩余空间 */
  display: flex;
  flex-direction: column;
  overflow-y: auto; /* 如果内容超长，则主内容区内部滚动 */
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 1.2rem;
  font-weight: 600;
  color: #fff;
  background-color: #2b3a4d;
}

.el-menu {
  border-right: none;
}

.el-header {
  flex-shrink: 0; /* 防止header被压缩 */
  background-color: #fff;
  color: #333;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-bottom: 1px solid #e6e6e6;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  font-size: 14px;
  color: #666;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  flex-grow: 1; /* 确保main区域也能撑开 */
}
</style>