<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">城市犯罪管理平台</div>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#304156"
        text-color="#bfcbd9"
        :router="true"
        :default-active="activeMenu"
      >
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
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <div class="main-content-wrapper">
      <el-header>
        <div class="user-info">欢迎您，管理员</div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </div>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const activeMenu = computed(() => route.path);
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

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  flex-grow: 1; /* 确保main区域也能撑开 */
}
</style>