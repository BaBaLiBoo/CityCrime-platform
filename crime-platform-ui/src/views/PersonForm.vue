<template>
    <div class="page-container">
      <div class="header-bar">
        <h1>登记新人员</h1>
      </div>
      <el-card>
        <el-form :model="form" ref="formRef" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name" required>
                <el-input v-model="form.name" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idNumber" required>
                <el-input v-model="form.idNumber" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%;">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                  <el-option label="未知" value="未知" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
               <el-form-item label="出生日期" prop="birthDate">
                  <el-date-picker
                    v-model="form.birthDate"
                    type="date"
                    placeholder="选择出生日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%;"
                  />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="联系方式" prop="contactInfo">
                <el-input v-model="form.contactInfo" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="住址" prop="address">
                <el-input v-model="form.address" type="textarea" :rows="3" />
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
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import personApi from '@/api/person.js';
  import { ElMessage } from 'element-plus';
  
  const router = useRouter();
  const route = useRoute();
  const formRef = ref(null);
  const isSubmitting = ref(false);
  const isEdit = ref(false);
  
  const form = ref({
    name: '',
    idNumber: '',
    gender: '未知',
    birthDate: null,
    contactInfo: '',
    address: '',
  });
  
  onMounted(async () => {
    if (route.params.id) {
      isEdit.value = true;
      try {
        const res = await personApi.getPersonById(route.params.id);
        Object.assign(form.value, res.data || {});
      } catch (e) {
        ElMessage.error('加载人员失败');
      }
    }
  });

  const onSubmit = async () => {
    isSubmitting.value = true;
    try {
      const payload = { ...form.value };
      if (!payload.birthDate) delete payload.birthDate;
      if (isEdit.value) {
        await personApi.updatePerson(route.params.id, payload);
        ElMessage.success('保存成功!');
      } else {
        await personApi.createPerson(payload);
        ElMessage.success('人员登记成功!');
      }
      router.push('/persons');
    } catch (error) {
      ElMessage.error('登记失败，请检查数据！');
      console.error("登记人员失败:", error.response?.data || error);
    } finally {
      isSubmitting.value = false;
    }
  };
  
  const onCancel = () => {
    router.push('/persons');
  };
  </script>
  
  <style scoped>
  .page-container {
    max-width: 1400px;
    margin: 0 auto;
  }
  .header-bar h1 {
    font-size: 22px;
    margin: 0;
    margin-bottom: 20px;
  }
  </style>