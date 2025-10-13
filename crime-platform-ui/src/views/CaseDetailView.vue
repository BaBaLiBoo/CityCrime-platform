<template>
    <div v-if="loading">
      正在加载案件详情...
    </div>
    <div v-else-if="caseDetail">
      <h1>案件详情</h1>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件ID">{{ caseDetail.caseId }}</el-descriptions-item>
        <el-descriptions-item label="案件标题">{{ caseDetail.caseTitle }}</el-descriptions-item>
        <el-descriptions-item label="案件类型">{{ caseDetail.caseType }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ caseDetail.status }}</el-descriptions-item>
        <el-descriptions-item label="报案时间">{{ caseDetail.reportTime }}</el-descriptions-item>
        <el-descriptions-item label="案发地点">{{ caseDetail.location?.address || 'N/A' }}</el-descriptions-item>
        <el-descriptions-item label="案件描述" :span="2">{{ caseDetail.description }}</el-descriptions-item>
      </el-descriptions>
      
      </div>
    <div v-else>
      未找到指定的案件信息。
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import caseApi from '@/api/case.js';
  import { ElMessage } from 'element-plus';
  
  const route = useRoute(); // 获取当前路由信息
  const caseDetail = ref(null); // 存储案件详情数据
  const loading = ref(true);
  
  onMounted(async () => {
    // 从路由中获取案件ID (即 /cases/:id 中的 id)
    const caseId = route.params.id;
    
    if (caseId) {
      try {
        loading.value = true;
        const response = await caseApi.getCaseById(caseId); // 调用API获取数据
        caseDetail.value = response.data;
      } catch (error) {
        console.error("获取案件详情失败:", error);
        ElMessage.error("获取案件详情失败！");
      } finally {
        loading.value = false;
      }
    }
  });
  </script>
  
  <style scoped>
  /* 您可以在这里添加一些样式 */
  </style>