<template>
  <div class="page-container">
    <div class="header-bar">
      <h1>新增地点</h1>
    </div>
    <el-card>
      <el-form :model="form" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="详细地址" prop="address" required>
              <el-input v-model="form.address" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行政区" prop="district">
              <el-input v-model="form.district" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input v-model.number="form.longitude" placeholder="-180 ~ 180" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input v-model.number="form.latitude" placeholder="-90 ~ 90" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="isSubmitting">保存</el-button>
          <el-button @click="onCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import locationApi from '@/api/location.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const formRef = ref(null);
const isSubmitting = ref(false);

const form = ref({
  address: '',
  district: '',
  longitude: null,
  latitude: null,
});

const onSubmit = async () => {
  isSubmitting.value = true;
  try {
    await locationApi.createLocation(form.value);
    ElMessage.success('保存成功!');
    router.push('/locations');
  } catch (error) {
    ElMessage.error('保存失败，请检查数据！');
  } finally {
    isSubmitting.value = false;
  }
};

const onCancel = () => { router.push('/locations'); };
</script>

<style scoped>
.page-container { max-width: 1400px; margin: 0 auto; }
.header-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-bar h1 { font-size: 22px; margin: 0; }
</style>


