<template>
  <div v-if="loading">正在加载案件详情...</div>
  <div v-else-if="caseDetail" class="page-container">
    <div class="header-bar">
      <h1>案件详情</h1>
      <div class="actions">
        <el-button @click="goBack">返回</el-button>
        <el-button v-if="canEdit" type="primary" @click="goEdit">编辑信息</el-button>
        <el-button @click="refresh">刷新</el-button>
      </div>
    </div>

    <el-card class="mb16">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件ID">{{ caseDetail.caseId }}</el-descriptions-item>
        <el-descriptions-item label="案件标题">{{ caseDetail.caseTitle }}</el-descriptions-item>
        <el-descriptions-item label="案件类型">{{ caseDetail.caseType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(caseDetail.status)">{{ caseDetail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="案件来源">{{ caseDetail.source }}</el-descriptions-item>
        <el-descriptions-item label="行政区">{{ caseDetail.location?.district || '—' }}</el-descriptions-item>
        <el-descriptions-item label="报案时间">{{ formatDateTime(caseDetail.reportTime) }}</el-descriptions-item>
        <el-descriptions-item label="立案时间">{{ caseDetail.filingTime ? formatDateTime(caseDetail.filingTime) : '—' }}</el-descriptions-item>
        <el-descriptions-item label="侦破时间">{{ caseDetail.solveTime ? formatDateTime(caseDetail.solveTime) : '—' }}</el-descriptions-item>
        <el-descriptions-item label="归档时间">{{ caseDetail.archiveTime ? formatDateTime(caseDetail.archiveTime) : '—' }}</el-descriptions-item>
        <el-descriptions-item label="案发地点" :span="2">{{ caseDetail.location?.address || 'N/A' }}</el-descriptions-item>
        <el-descriptions-item label="案件描述" :span="2">{{ caseDetail.description || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 案件状态时间轴 -->
    <el-card class="mb16">
      <CaseTimeline :case-data="caseDetail" />
    </el-card>

    <!-- 多媒体文件 -->
    <el-card v-if="mediaFiles && mediaFiles.length > 0" class="mb16">
      <template #header>
        <span>多媒体文件</span>
      </template>
      <div class="media-files">
        <div v-for="(file, index) in mediaFiles" :key="index" class="media-item">
          <img v-if="isImage(file)" :src="getFileUrl(file)" alt="图片" class="media-image" />
          <a v-else :href="getFileUrl(file)" target="_blank" class="media-link">{{ file }}</a>
        </div>
      </div>
    </el-card>
  </div>
  <div v-else>未找到指定的案件信息。</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import reportApi from '@/api/report.js';
import { ElMessage } from 'element-plus';
import CaseTimeline from '@/components/CaseTimeline.vue';

const route = useRoute();
const router = useRouter();
const caseId = computed(() => route.params.id);
const caseDetail = ref(null);
const loading = ref(true);
const lockedStatuses = ['立案侦查', '已告破', '已归档'];

const mediaFiles = computed(() => {
  if (!caseDetail.value?.mediaFiles) return [];
  const raw = caseDetail.value.mediaFiles;
  try {
    // 优先 JSON 数组
    if (typeof raw === 'string' && raw.trim().startsWith('[')) {
      return JSON.parse(raw);
    }
    // 兼容旧数据：逗号分隔的字符串
    if (typeof raw === 'string' && raw.includes(',')) {
      return raw.split(',').map(s => s.trim()).filter(Boolean);
    }
    return Array.isArray(raw) ? raw : [raw];
  } catch (e) {
    return [];
  }
});

const refresh = async () => {
  try {
    loading.value = true;
    const res = await reportApi.getMyReportDetail(caseId.value);
    caseDetail.value = res.data;
  } catch (e) {
    console.error('获取案件详情失败:', e);
    const errorMessage = e.response?.data?.message || '获取案件详情失败';
    ElMessage.error(errorMessage);
    if (e.response?.status === 403 || e.response?.status === 404) {
      router.push('/user/home');
    }
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push('/user/home');
};

const canEdit = computed(() => {
  if (!caseDetail.value?.status) return false;
  return !lockedStatuses.includes(caseDetail.value.status);
});

const goEdit = () => {
  router.push(`/user/cases/${caseId.value}/edit`);
};

const getStatusType = (status) => {
  const statusMap = {
    '已接报': 'info',
    '立案侦查': 'warning',
    '已告破': 'success',
    '已归档': ''
  };
  return statusMap[status] || '';
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  try {
    let date;
    if (typeof dateTime === 'string') {
      if (dateTime.includes('T')) {
        date = new Date(dateTime);
      } else if (dateTime.includes(' ')) {
        date = new Date(dateTime.replace(' ', 'T'));
      } else {
        date = new Date(dateTime);
      }
    } else if (dateTime instanceof Date) {
      date = dateTime;
    } else {
      date = new Date(dateTime);
    }
    
    if (isNaN(date.getTime())) {
      return '时间格式错误';
    }
    
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  } catch (error) {
    return '时间格式错误';
  }
};

const isImage = (filename) => {
  const imageExts = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp'];
  return imageExts.some(ext => filename.toLowerCase().endsWith(ext));
};

const getFileUrl = (filename) => {
  if (!filename) return '';
  // 已包含 /uploads 前缀时直接拼 baseUrl，兼容旧数据
  if (filename.startsWith('/uploads/')) {
    return `http://localhost:8080${filename}`;
  }
  // 否则认为是纯文件名
  return `http://localhost:8080/uploads/${filename}`;
};

onMounted(async () => {
  await refresh();
});
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.mb16 {
  margin-bottom: 16px;
}

.media-files {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.media-item {
  flex: 0 0 auto;
}

.media-image {
  max-width: 300px;
  max-height: 300px;
  border-radius: 4px;
  cursor: pointer;
}

.media-link {
  display: block;
  padding: 8px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  text-decoration: none;
  color: #409eff;
}
</style>

