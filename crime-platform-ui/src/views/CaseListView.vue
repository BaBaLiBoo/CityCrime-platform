<template>
  <div class="page-container">
    <div class="header-bar">
      <h1>案件管理</h1>
      <el-button type="primary" @click="handleCreate">新建案件</el-button>
    </div>

    <el-card>
      <el-table :data="cases" v-loading="loading" style="width: 100%">
        <el-table-column prop="caseId" label="案件ID" width="100" />
        <el-table-column prop="caseTitle" label="案件标题" width="250" />
        <el-table-column prop="caseType" label="案件类型" width="150" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="reportTime" label="报案时间" />
        <el-table-column prop="locationAddress" label="案发地点" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row.caseId)">查看</el-button>
            <el-button type="primary" size="small" @click="handleEdit(scope.row.caseId)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.caseId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="totalCases"
          v-model:current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import caseApi from '@/api/case.js';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const cases = ref([]);
const loading = ref(true);
const totalCases = ref(0);
const currentPage = ref(1);

const fetchCases = async () => {
  try {
    loading.value = true;
    const response = await caseApi.getAllCases();
    cases.value = response.data;
    totalCases.value = response.data.length;
  } catch (error) {
    ElMessage.error("获取案件列表失败!");
  } finally {
    loading.value = false;
  }
};

onMounted(fetchCases);

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchCases();
};

const handleCreate = () => {
  router.push('/cases/create');
};

const handleView = (id) => {
  router.push(`/cases/${id}`);
};

const handleEdit = (id) => {
  router.push(`/cases/edit/${id}`);
};

const handleDelete = (id) => {
  ElMessageBox.confirm(
    `确定要删除ID为 ${id} 的案件吗？此操作不可逆。`, '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await caseApi.deleteCase(id);
      ElMessage.success('删除成功!');
      fetchCases();
    } catch (error) {
      ElMessage.error('删除失败!');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
}
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-bar h1 {
  font-size: 22px;
  margin: 0;
}
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>