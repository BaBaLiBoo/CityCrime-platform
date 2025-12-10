<template>
  <!-- 未登录时显示登录页 -->
  <router-view v-if="!isLoggedIn" />
  
  <!-- 管理员布局 -->
  <el-container v-else-if="userType === '管理员'" class="layout-container admin-layout">
    <el-aside width="220px">
      <div class="logo">
        <img src="/logo-shouhuzhe.svg" alt="守沪者 Logo" class="side-logo-img" />
        <div class="logo-text">
          <div class="logo-title">守沪者</div>
          <div class="logo-subtitle">城市治安协同治理 · 治理端</div>
        </div>
      </div>
      <el-menu
        active-text-color="#00E5FF"
        background-color="transparent"
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
      <el-header class="admin-header">
        <div class="header-content">
          <div class="system-title">守沪者——基于公众参与的城市治安协同治理系统</div>
          <div class="user-info">管理员</div>
          <el-button type="danger" size="small" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </div>
  </el-container>
  
  <!-- 普通用户布局 -->
  <el-container v-else class="layout-container user-layout">
    <el-aside width="220px">
      <div class="logo user-logo">
        <img src="/logo-shouhuzhe.svg" alt="守沪者 Logo" class="side-logo-img" />
        <div class="logo-text">
          <div class="logo-title">守沪者</div>
          <div class="logo-subtitle">城市治安协同治理 · 公众端</div>
        </div>
      </div>
      <el-menu
        active-text-color="#002FA7"
        background-color="#ffffff"
        text-color="#1f2933"
        :router="true"
        :default-active="activeMenu"
      >
        <el-menu-item index="/user/home">
          <span>我的举报</span>
        </el-menu-item>
        <el-menu-item index="/user/report">
          <span>我要举报</span>
        </el-menu-item>
        <el-menu-item index="/user/profile">
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <div class="main-content-wrapper">
      <el-header class="user-header">
        <div class="header-content">
          <div class="system-title">守沪者——基于公众参与的城市治安协同治理系统</div>
          <div class="user-info">欢迎您，{{ username }}</div>
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
import { computed, ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import apiClient from '@/api/index.js';

const route = useRoute();
const router = useRouter();
const activeMenu = computed(() => route.path);
const userType = ref(localStorage.getItem('user_type') || '普通用户');
const username = ref('');
const isLoggedIn = ref(false);

// 检查是否在登录页面
const isLoginPage = computed(() => {
  return route.path === '/login' || route.path === '/register';
});

onMounted(async () => {
  const token = localStorage.getItem('auth_token');
  if (token && !isLoginPage.value) {
    try {
      const res = await apiClient.get('/auth/me');
      const currentUserType = res.data.userType || '普通用户';
      username.value = res.data.username || '用户';
      userType.value = currentUserType;
      // 更新localStorage中的用户类型
      localStorage.setItem('user_type', currentUserType);
      
      // 验证当前路由是否与用户类型匹配
      const currentPath = route.path;
      const adminPages = ['/dashboard', '/cases', '/persons', '/locations', '/officers', '/home'];
      const userPages = ['/user'];
      
      if (currentUserType === '管理员') {
        // 管理员访问用户页面，重定向到管理员页面
        if (userPages.some(page => currentPath.startsWith(page))) {
          router.replace('/dashboard');
          return;
        }
        isLoggedIn.value = true;
      } else {
        // 普通用户访问管理员页面，重定向到用户页面
        if (adminPages.some(page => currentPath.startsWith(page))) {
          router.replace('/user/home');
          return;
        }
        isLoggedIn.value = true;
      }
    } catch (error) {
      // 如果token无效，清除并跳转到登录页
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_type');
      localStorage.removeItem('user_info');
      isLoggedIn.value = false;
      router.push('/login');
    }
  } else if (!token) {
    isLoggedIn.value = false;
  }
});

// 监听路由变化
watch(() => route.path, async (newPath) => {
  const token = localStorage.getItem('auth_token');
  if (newPath === '/login' || newPath === '/register') {
    isLoggedIn.value = false;
  } else if (token) {
    // 验证用户类型是否匹配当前路由
    try {
      const res = await apiClient.get('/auth/me');
      const currentUserType = res.data.userType || '普通用户';
      userType.value = currentUserType;
      localStorage.setItem('user_type', currentUserType);
      
      const adminPages = ['/dashboard', '/cases', '/persons', '/locations', '/officers', '/home'];
      const userPages = ['/user'];
      
      if (currentUserType === '管理员' && userPages.some(page => newPath.startsWith(page))) {
        router.replace('/dashboard');
        return;
      }
      if (currentUserType !== '管理员' && adminPages.some(page => newPath.startsWith(page))) {
        router.replace('/user/home');
        return;
      }
      
      isLoggedIn.value = true;
    } catch (error) {
      isLoggedIn.value = false;
    }
  }
});

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
    localStorage.removeItem('user_type');
    isLoggedIn.value = false;
    
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
  display: flex;
}

.admin-layout {
  background: radial-gradient(circle at top, rgba(0, 229, 255, 0.16), transparent 55%),
              #091020;
  color: #e5f3ff;
}

.user-layout {
  background: linear-gradient(135deg, #f0f4ff 0%, #f5f7fa 40%, #ffffff 100%);
}

.admin-layout .el-aside {
  background: rgba(6, 14, 32, 0.96);
  border-right: 1px solid rgba(0, 229, 255, 0.16);
  color: #e5f3ff;
  flex-shrink: 0;
}

.user-layout .el-aside {
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  color: #111827;
  flex-shrink: 0;
}

.main-content-wrapper {
  flex-grow: 1; /* 让主内容区占据所有剩余空间 */
  display: flex;
  flex-direction: column;
  overflow-y: auto; /* 如果内容超长，则主内容区内部滚动 */
}

.logo {
  height: 72px;
  padding: 10px 12px;
  display: flex;
  flex-direction: row;
  align-items: center;
  border-bottom: 1px solid rgba(148, 163, 184, 0.4);
  background: linear-gradient(135deg, #002fa7 0%, #091020 90%);
}

.user-logo {
  background: #ffffff;
}

.logo-title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
  color: #ffffff;
}

.user-logo .logo-title {
  color: #002fa7;
}

.logo-subtitle {
  font-size: 11px;
  opacity: 0.85;
  color: #e5f3ff;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.side-logo-img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.user-logo .logo-subtitle {
  color: #6b7280;
}

.el-menu {
  border-right: none;
}

.el-header {
  flex-shrink: 0; /* 防止header被压缩 */
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.admin-header {
  background: rgba(9, 16, 32, 0.96);
  color: #e5f3ff;
  border-bottom: 1px solid rgba(148, 163, 184, 0.45);
}

.user-header {
  background-color: #ffffff;
  color: #111827;
  border-bottom: 1px solid #e5e7eb;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  justify-content: flex-end;
}

.system-title {
  margin-right: auto;
  font-size: 14px;
  letter-spacing: 1px;
  color: #9ca3af;
}

.admin-header .system-title {
  color: #9ca3af;
}

.user-header .system-title {
  color: #4b5563;
}

.user-info {
  font-size: 13px;
  color: inherit;
}

.el-main {
  padding: 20px;
  flex-grow: 1; /* 确保main区域也能撑开 */
}

.admin-layout .el-main {
  background: transparent;
}

.user-layout .el-main {
  background-color: #f0f2f5;
}

/* 让内部卡片/内容区域与整体背景更融合 */
.admin-layout :deep(.page-container) {
  background: rgba(15, 23, 42, 0.96);
  border-radius: 12px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.45);
  color: #e5e7eb;
}

.admin-layout :deep(.el-card) {
  background: rgba(15, 23, 42, 0.98);
  border-color: rgba(55, 65, 81, 0.9);
  color: #e5e7eb;
}

.user-layout :deep(.page-container) {
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 8px 26px rgba(15, 23, 42, 0.08);
}

/* 管理端列表/表格在深色背景上的文字与表头样式 */
.admin-layout :deep(h1),
.admin-layout :deep(h2),
.admin-layout :deep(h3),
.admin-layout :deep(.header-bar h1),
.admin-layout :deep(.page-header h2) {
  color: #f9fafb;
}

.admin-layout :deep(.el-table) {
  background: transparent;
  color: #e5e7eb;
}

.admin-layout :deep(.el-table th.el-table__cell) {
  background-color: rgba(15, 23, 42, 0.98);
  color: #e5e7eb;
}

.admin-layout :deep(.el-table td.el-table__cell) {
  background-color: rgba(15, 23, 42, 0.96);
  border-bottom-color: rgba(55, 65, 81, 0.8);
}

.admin-layout :deep(.el-table__body tr:hover > td) {
  background-color: rgba(15, 23, 42, 0.98);
}

.admin-layout :deep(.el-table__body tr.el-table__row--striped td.el-table__cell) {
  background-color: rgba(15, 23, 42, 0.92);
}

.admin-layout :deep(.el-table__inner-wrapper::before) {
  background-color: transparent;
}

/* 管理端编辑/表单界面深色样式 */
.admin-layout :deep(.el-card__header) {
  border-bottom-color: rgba(55, 65, 81, 0.9);
}

.admin-layout :deep(.el-card__header span) {
  color: #f9fafb;
}

.admin-layout :deep(.el-form-item__label) {
  color: #cbd5f5;
}

.admin-layout :deep(.el-input__wrapper),
.admin-layout :deep(.el-select__wrapper),
.admin-layout :deep(.el-textarea__inner),
.admin-layout :deep(.el-date-editor.el-input__wrapper) {
  background-color: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.5) inset;
  color: #e5e7eb;
}

.admin-layout :deep(.el-input__inner),
.admin-layout :deep(.el-select__selected-item),
.admin-layout :deep(.el-textarea__inner),
.admin-layout :deep(.el-date-editor .el-input__inner) {
  color: #e5e7eb;
}

.admin-layout :deep(.el-input__wrapper.is-focus),
.admin-layout :deep(.el-select__wrapper.is-focused),
.admin-layout :deep(.el-date-editor.el-input__wrapper.is-active) {
  box-shadow: 0 0 0 1px #00e5ff inset;
}

.admin-layout :deep(.el-dialog) {
  background-color: rgba(15, 23, 42, 0.98);
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.5);
  color: #e5e7eb;
}

.admin-layout :deep(.el-dialog__title) {
  color: #f9fafb;
}

.admin-layout :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #9ca3af;
}
</style>