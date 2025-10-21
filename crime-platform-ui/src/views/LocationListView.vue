<template>
  <div class="page-container">
    <div class="header-bar">
      <h1>地点管理</h1>
      <el-button type="primary" @click="handleCreate">新增地点</el-button>
    </div>

    <el-card>
      <el-table :data="locations" v-loading="loading" style="width: 100%">
        <el-table-column prop="locationId" label="ID" width="100" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="district" label="行政区" width="160" />
        <el-table-column prop="longitude" label="经度" width="160" />
        <el-table-column prop="latitude" label="纬度" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import locationApi from '@/api/location.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const locations = ref([]);
const loading = ref(true);

const fetchLocations = async () => {
  try {
    loading.value = true;
    const res = await locationApi.getAllLocations();
    locations.value = res.data;
  } catch (e) {
    ElMessage.error('获取地点失败');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchLocations);

const handleCreate = () => {
  router.push('/locations/create');
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
</style>


