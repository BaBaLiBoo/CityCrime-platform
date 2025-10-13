<template>
  <div class="page-container">
    <div class="header-bar">
      <h1>{{ pageTitle }}</h1>
    </div>
    <el-card>
      <el-form :model="form" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="案件标题" prop="caseTitle" required>
              <el-input v-model="form.caseTitle" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案件类型" prop="caseType" required>
              <el-input v-model="form.caseType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案件状态" prop="status" required>
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="已接报" value="已接报" />
                <el-option label="立案侦查" value="立案侦查" />
                <el-option label="已告破" value="已告破" />
                <el-option label="已归档" value="已归档" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报案时间" prop="reportTime" required>
              <el-date-picker
                v-model="form.reportTime"
                type="datetime"
                placeholder="选择报案时间"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="案发地点" prop="locationAddress" required>
              <el-input v-model="form.locationAddress" placeholder="请输入详细案发地点" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="案件描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="4" />
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
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import caseApi from '@/api/case.js';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const formRef = ref(null);
const isEditMode = computed(() => !!route.params.id);
const pageTitle = computed(() => isEditMode.value ? '编辑案件' : '新建案件');
const isSubmitting = ref(false);

const form = ref({
  caseTitle: '',
  caseType: '',
  status: '已接报',
  reportTime: '',
  description: '',
  locationAddress: '',
});

onMounted(async () => {
  if (isEditMode.value) {
    const caseId = route.params.id;
    try {
      const response = await caseApi.getCaseById(caseId);
      const data = response.data;
      form.value = {
        caseTitle: data.caseTitle,
        caseType: data.caseType,
        status: data.status,
        reportTime: data.reportTime,
        description: data.description,
        locationAddress: data.location?.address || '',
      };
    } catch (error) {
      ElMessage.error("获取案件详情失败！");
    }
  }
});

const onSubmit = async () => {
  isSubmitting.value = true;
  try {
    if (isEditMode.value) {
      await caseApi.updateCase(route.params.id, form.value);
    } else {
      await caseApi.createCase(form.value);
    }
    ElMessage.success('保存成功!');
    router.push('/cases');
  } catch (error) {
    ElMessage.error('保存失败，请检查表单数据或联系管理员！');
    console.error("保存案件失败:", error.response?.data || error);
  } finally {
    isSubmitting.value = false;
  }
};

const onCancel = () => { router.push('/cases'); };
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