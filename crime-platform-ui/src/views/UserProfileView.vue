<template>
  <div class="user-profile-container">
    <el-card class="profile-card">
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
          <el-input
            v-model="form.realName"
            placeholder="请输入真实姓名"
            :disabled="Boolean(form.realName && form.realNameLocked)"
          />
          <span v-if="form.realNameLocked" class="field-tip">已验证的姓名不可修改</span>
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
          <el-select
            v-model="form.gender"
            placeholder="请选择性别"
            style="width: 100%;"
          >
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

    <el-card class="security-card">
      <template #header>
        <div class="header-content">
          <h2>账号安全</h2>
        </div>
      </template>
      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="120px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">
            修改密码
          </el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
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
const passwordFormRef = ref(null);
const saving = ref(false);
const changingPassword = ref(false);
const form = reactive({
  username: '',
  userType: '',
  realName: '',
  realNameLocked: false,
  idNumber: '',
  idNumberLocked: false,
  gender: '',
  birthDate: '',
  address: '',
  contactInfo: ''
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const verified = computed(() => {
  return (
    form.realName &&
    form.idNumber &&
    form.gender &&
    form.contactInfo
  );
});

const CHINA_ID_REGEX = /^[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/;
const ID_WEIGHTS = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
const ID_CHECK_CODES = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];

const isValidChineseId = (value) => {
  if (!value) return false;
  const upper = value.trim().toUpperCase();
  if (!CHINA_ID_REGEX.test(upper)) return false;
  let sum = 0;
  for (let i = 0; i < 17; i += 1) {
    sum += Number(upper[i]) * ID_WEIGHTS[i];
  }
  const expected = ID_CHECK_CODES[sum % 11];
  return expected === upper[17];
};

const validateIdNumber = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入身份证号'));
    return;
  }
  if (!isValidChineseId(value)) {
    callback(new Error('身份证号格式或校验码不正确'));
    return;
  }
  callback();
};

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [{ validator: validateIdNumber, trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
};

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'));
    return;
  }
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
    return;
  }
  callback();
};

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
};

const loadProfile = async () => {
  try {
    const res = await profileApi.getProfile();
    const data = res.data || {};
    form.username = data.username || '';
    form.userType = data.userType || '普通用户';
    form.realName = data.realName || '';
    form.realNameLocked = Boolean(data.realName);
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

const resetPasswordForm = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordFormRef.value?.clearValidate();
};

const saveProfile = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    const normalizedId = form.idNumber ? form.idNumber.trim().toUpperCase() : '';
    if (normalizedId && normalizedId !== form.idNumber) {
      form.idNumber = normalizedId;
    }
    try {
      await profileApi.updateProfile({
        realName: form.realName,
        idNumber: normalizedId,
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

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return;
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return;
    changingPassword.value = true;
    try {
      await profileApi.changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      });
      ElMessage.success('密码已更新，请使用新密码重新登录');
      resetPasswordForm();
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '修改密码失败');
    } finally {
      changingPassword.value = false;
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

.profile-card,
.security-card {
  margin-bottom: 20px;
}
</style>

