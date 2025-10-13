<template>
    <div class="page-container">
      <div class="header-bar">
        <h1>人员管理</h1>
        <el-button type="primary" @click="handleCreate">登记新人员</el-button>
      </div>
  
      <el-card>
        <el-table :data="persons" v-loading="loading" style="width: 100%">
          <el-table-column prop="personId" label="人员ID" width="100" />
          <el-table-column prop="name" label="姓名" width="150" />
          <el-table-column prop="idNumber" label="身份证号" />
          <el-table-column prop="gender" label="性别" width="100" />
          <el-table-column prop="contactInfo" label="联系方式" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row.personId)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row.personId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import personApi from '@/api/person.js';
  import { ElMessage } from 'element-plus';
  
  const router = useRouter();
  const persons = ref([]);
  const loading = ref(true);
  
  const fetchPersons = async () => {
    try {
      loading.value = true;
      const response = await personApi.getAllPersons();
      persons.value = response.data;
    } catch (error) {
      ElMessage.error("获取人员列表失败!");
    } finally {
      loading.value = false;
    }
  };
  
  onMounted(fetchPersons);
  
  const handleCreate = () => {
    router.push('/persons/create');
  };
  
  const handleEdit = (id) => { ElMessage.info(`编辑功能待实现 (ID: ${id})`); };
  const handleDelete = (id) => { ElMessage.info(`删除功能待实现 (ID: ${id})`); };
  </script>
  
  <style scoped>
  .page-container {
    max-width: 1400px;
    margin: 0 auto;
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
  </style>