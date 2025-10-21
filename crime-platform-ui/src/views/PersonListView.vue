<template>
    <div class="page-container">
      <div class="header-bar">
        <h1>人员管理</h1>
        <el-button type="primary" @click="handleCreate">登记新人员</el-button>
      </div>
  
      <el-card>
        <el-table :data="persons" v-loading="loading" style="width: 100%">
          <el-table-column prop="idNumber" label="身份证号" width="180" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="gender" label="性别" width="80" />
          <el-table-column prop="birthDate" label="出生日期" width="120" />
          <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="contactInfo" label="联系方式" width="150" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="info" size="small" @click="handleView(scope.row.idNumber)">查看</el-button>
              <el-button type="primary" size="small" @click="handleEdit(scope.row.idNumber)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row.idNumber)">删除</el-button>
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
  import { ElMessage, ElMessageBox } from 'element-plus';
  
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
  
  const handleView = (idNumber) => {
    // 显示人员详情对话框
    const person = persons.value.find(p => p.idNumber === idNumber);
    if (person) {
      ElMessageBox.alert(
        `
        <div style="text-align: left;">
          <p><strong>身份证号：</strong>${person.idNumber}</p>
          <p><strong>姓名：</strong>${person.name}</p>
          <p><strong>性别：</strong>${person.gender}</p>
          <p><strong>出生日期：</strong>${person.birthDate || '未填写'}</p>
          <p><strong>地址：</strong>${person.address || '未填写'}</p>
          <p><strong>联系方式：</strong>${person.contactInfo || '未填写'}</p>
        </div>
        `,
        '人员详情',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定'
        }
      );
    }
  };
  
  const handleEdit = (id) => { router.push(`/persons/edit/${id}`); };
  const handleDelete = (id) => {
    ElMessageBox.confirm('确定删除该人员吗？', '提示', { type: 'warning' }).then(async () => {
      try {
        await personApi.deletePerson(id);
        ElMessage.success('已删除');
        fetchPersons();
      } catch (e) {
        ElMessage.error('删除失败');
      }
    }).catch(() => {});
  };
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