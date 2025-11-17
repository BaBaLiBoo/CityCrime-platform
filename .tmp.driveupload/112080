<template>
  <div class="user-home-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <h2>我的举报</h2>
          <div class="header-actions">
            <el-button @click="goProfile">个人信息</el-button>
            <el-button type="primary" @click="goToReport">我要举报</el-button>
          </div>
        </div>
      </template>

      <div v-if="!verified" class="verify-notice">
        <el-alert
          title="请先完善个人信息后再进行举报"
          type="warning"
          :closable="false"
          show-icon
        >
          <template #default>
            <el-button type="primary" size="small" @click="goProfile">前往完善</el-button>
          </template>
        </el-alert>
      </div>

      <el-table :data="myReports" v-loading="loading" style="width: 100%">
        <el-table-column prop="caseId" label="案件ID" width="100" />
        <el-table-column prop="caseTitle" label="案件标题" width="200" />
        <el-table-column prop="caseType" label="事件类型" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="district" label="行政区" width="120" />
        <el-table-column prop="reportTime" label="举报时间" />
        <el-table-column prop="locationAddress" label="案发地点" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row.caseId)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import reportApi from '@/api/report.js';
import profileApi from '@/api/profile.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const myReports = ref([]);
const profileInfo = ref({});

const verified = computed(() => {
  return (
    profileInfo.value.realName &&
    profileInfo.value.idNumber &&
    profileInfo.value.gender &&
    profileInfo.value.contactInfo
  );
});

const getStatusType = (status) => {
  const statusMap = {
    '已接报': 'info',
    '立案侦查': 'warning',
    '已告破': 'success',
    '已归档': ''
  };
  return statusMap[status] || '';
};

const loadProfile = async () => {
  try {
    const res = await profileApi.getProfile();
    profileInfo.value = res.data || {};
  } catch (error) {
    ElMessage.error('获取个人信息失败');
  }
};

const loadMyReports = async () => {
  loading.value = true;
  try {
    const res = await reportApi.getMyReports();
    myReports.value = res.data;
  } catch (error) {
    ElMessage.error('获取举报记录失败');
  } finally {
    loading.value = false;
  }
};

const goToReport = () => {
  if (!verified.value) {
    ElMessage.warning('请先完善个人信息');
    router.push('/user/profile');
    return;
  }
  router.push('/user/report');
};

const goProfile = () => {
  router.push('/user/profile');
};

const viewDetail = (caseId) => {
  router.push(`/user/cases/${caseId}`);
};

onMounted(async () => {
  await Promise.all([loadProfile(), loadMyReports()]);
});
</script>

<style scoped>
.user-home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.verify-notice {
  margin-bottom: 20px;
}

.verify-notice :deep(.el-alert__content) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.verify-notice :deep(.el-alert__title) {
  font-size: 14px;
}
</style>

