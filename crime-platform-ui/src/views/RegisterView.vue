<template>
  <div class="register-page">
    <div class="register-hero">
      <div class="hero-title">守沪者 · 创建账号</div>
      <div class="hero-subtitle">完成实名认证后即可在线举报、查询案件进度，与警方共同守护城市安全。</div>
    </div>
    <el-card class="register-card">
      <h2>创建用户账号</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="用户名" prop="username" required>
          <el-input v-model.trim="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" required>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" required>
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <el-divider>个人信息</el-divider>
        <el-form-item label="姓名" prop="realName" required>
          <el-input v-model.trim="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber" required>
          <el-input v-model.trim="form.idNumber" placeholder="请输入18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="性别" prop="gender" required>
          <el-select
            v-model="form.gender"
            placeholder="请选择性别"
            style="width: 100%;"
          >
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="请选择出生日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model.trim="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo" required>
          <el-input v-model.trim="form.contactInfo" placeholder="请输入手机号或邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onRegister">创建</el-button>
          <el-button @click="goLogin">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import apiClient from '@/api/index.js';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const formRef = ref(null);
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  idNumber: '',
  gender: '',
  birthDate: '',
  address: '',
  contactInfo: ''
});

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

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
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { validator: validateIdNumber, trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
};

const onRegister = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请填写完整信息');
      return;
    }
    
    loading.value = true;
    try {
      const payload = {
        username: form.value.username,
        password: form.value.password,
        realName: form.value.realName,
        idNumber: form.value.idNumber?.trim().toUpperCase(),
        gender: form.value.gender,
        birthDate: form.value.birthDate || null,
        address: form.value.address || null,
        contactInfo: form.value.contactInfo
      };
      await apiClient.post('/auth/register', payload);
      ElMessage.success('创建成功，请登录');
      router.push('/login');
    } catch (e) {
      const msg = e?.response?.data?.message || '创建失败，请检查信息';
      ElMessage.error(msg);
    } finally {
      loading.value = false;
    }
  });
};

watch(
  () => form.value.idNumber,
  (val) => {
    if (!val) return;
    const upper = val.toUpperCase();
    if (upper !== val) {
      form.value.idNumber = upper;
    }
  }
);

const goLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.register-page {
  max-width: 900px;
  margin: 32px auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.register-hero {
  background: linear-gradient(135deg, #002fa7 0%, #00e5ff 90%);
  border-radius: 12px;
  padding: 18px 20px;
  color: #f9fafb;
  box-shadow: 0 14px 32px rgba(0, 47, 167, 0.4);
}

.hero-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
}

.hero-subtitle {
  font-size: 13px;
  opacity: 0.95;
}

.register-card {
  border-radius: 12px;
}
</style>


