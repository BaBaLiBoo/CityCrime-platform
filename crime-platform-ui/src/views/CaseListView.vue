<template>
  <div class="page-container">
    <div class="header-bar">
      <h1>案件管理</h1>
      <el-button type="primary" @click="handleCreate">新建案件</el-button>
    </div>

    <el-card class="mb16">
      <el-form :inline="true" :model="filters" class="filter-bar">
        <el-form-item label="类型">
          <el-input v-model="filters.caseType" placeholder="输入类型" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 140px;">
            <el-option label="已接报" value="已接报" />
            <el-option label="立案侦查" value="立案侦查" />
            <el-option label="已告破" value="已告破" />
            <el-option label="已归档" value="已归档" />
          </el-select>
        </el-form-item>
        <el-form-item label="报案时间">
          <el-date-picker v-model="filters.reportTimeRange" type="datetimerange" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="pagedCases" v-loading="loading" style="width: 100%">
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
          :total="filteredCases.length"
          v-model:current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import caseApi from '@/api/case.js';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const cases = ref([]);
const loading = ref(true);
const currentPage = ref(1);
const pageSize = 10;
const filters = ref({ caseType: '', status: '', reportTimeRange: [] });

const fetchCases = async () => {
  try {
    loading.value = true;
    const response = await caseApi.getAllCases();
    cases.value = response.data;
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

const resetFilters = () => {
  filters.value = { caseType: '', status: '', reportTimeRange: [] };
};

const filteredCases = computed(() => {
  const [start, end] = filters.value.reportTimeRange || [];
  return (cases.value || []).filter(c => {
    const typeOk = !filters.value.caseType || (c.caseType || '').includes(filters.value.caseType);
    const statusOk = !filters.value.status || c.status === filters.value.status;
    const timeOk = !start || !end ? true : (c.reportTime >= start && c.reportTime <= end);
    return typeOk && statusOk && timeOk;
  });
});

const pagedCases = computed(() => {
  const startIdx = (currentPage.value - 1) * pageSize;
  return filteredCases.value.slice(startIdx, startIdx + pageSize);
});

watch(filters, () => { currentPage.value = 1; }, { deep: true });

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