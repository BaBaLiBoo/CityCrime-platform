<template>
  <div class="user-profile-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <h2>个人信息</h2>
          <el-tag :type="verified ? 'success' : 'warning'">
            {{ verified ? '已完善' : '未完善' }}
          </el-tag>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-input v-model="form.userType" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input
            v-model="form.idNumber"
            placeholder="请输入18位身份证号"
            maxlength="18"
            :disabled="Boolean(form.idNumber && form.idNumberLocked)"
          />
          <span v-if="form.idNumberLocked" class="field-tip">已验证的身份证号不可修改</span>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%;">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="请选择出生日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="联系地址">
          <el-input v-model="form.address" placeholder="请输入联系地址" />
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="form.contactInfo" placeholder="请输入手机号或邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
          <el-button @click="reloadProfile">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import profileApi from '@/api/profile.js';

const formRef = ref(null);
const saving = ref(false);
const form = reactive({
  username: '',
  userType: '',
  realName: '',
  idNumber: '',
  idNumberLocked: false,
  gender: '',
  birthDate: '',
  address: '',
  contactInfo: ''
});

const verified = computed(() => {
  return (
    form.realName &&
    form.idNumber &&
    form.gender &&
    form.contactInfo
  );
});

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
};

const loadProfile = async () => {
  try {
    const res = await profileApi.getProfile();
    const data = res.data || {};
    form.username = data.username || '';
    form.userType = data.userType || '普通用户';
    form.realName = data.realName || '';
    form.idNumber = data.idNumber || '';
    form.idNumberLocked = Boolean(data.idNumber);
    form.gender = data.gender || '';
    form.birthDate = data.birthDate || '';
    form.address = data.address || '';
    form.contactInfo = data.contactInfo || '';
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取个人信息失败');
  }
};

const reloadProfile = () => {
  loadProfile();
};

const saveProfile = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      await profileApi.updateProfile({
        realName: form.realName,
        idNumber: form.idNumber,
        gender: form.gender,
        birthDate: form.birthDate || null,
        address: form.address || null,
        contactInfo: form.contactInfo
      });
      ElMessage.success('个人信息已更新');
      await loadProfile();
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '保存失败');
    } finally {
      saving.value = false;
    }
  });
};

onMounted(loadProfile);
</script>

<style scoped>
.user-profile-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.field-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 12px;
}
</style>

