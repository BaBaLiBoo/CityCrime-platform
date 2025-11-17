<template>
  <div class="report-container" v-if="ready">
    <el-card>
      <template #header>
        <div class="header">
          <h2>编辑举报信息</h2>
          <el-tag type="info">案件状态：{{ caseStatus }}</el-tag>
        </div>
      </template>
      
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="事件类型" prop="caseType">
          <el-select v-model="form.caseType" placeholder="请选择事件类型" style="width: 100%">
            <el-option label="盗窃" value="盗窃" />
            <el-option label="诈骗" value="诈骗" />
            <el-option label="抢劫" value="抢劫" />
            <el-option label="故意伤害" value="故意伤害" />
            <el-option label="交通违法" value="交通违法" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="基本情况描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="请详细描述事件发生的时间、地点、经过等情况"
          />
        </el-form-item>
        
        <el-form-item label="案发地点" prop="locationAddress">
          <el-input
            v-model="form.locationAddress"
            placeholder="请输入详细地址"
            @blur="handleAddressBlur"
          />
        </el-form-item>
        
        <el-form-item label="行政区" prop="district">
          <el-select v-model="form.district" placeholder="请选择行政区" style="width: 100%">
            <el-option v-for="district in districts" :key="district" :label="district" :value="district" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="定位">
          <el-button @click="getCurrentLocation" :loading="locating">更新当前位置</el-button>
          <div v-if="form.longitude && form.latitude" class="location-info">
            <span>经度: {{ form.longitude }}, 纬度: {{ form.latitude }}</span>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">保存修改</el-button>
          <el-button @click="goDetail">取消</el-button>
        </el-form-item>
      </el-form>
      
      <el-alert
        title="提示：目前暂不支持在线更新已上传的多媒体文件，如需补充请联系管理员。"
        type="info"
        show-icon
      />
    </el-card>
  </div>
  <div v-else class="report-container">
    <el-skeleton :rows="6" animated />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import reportApi from '@/api/report.js';

const route = useRoute();
const router = useRouter();
const caseId = computed(() => route.params.id);
const formRef = ref(null);
const submitting = ref(false);
const locating = ref(false);
const ready = ref(false);
const caseStatus = ref('');
const lockedStatuses = ['立案侦查', '已告破', '已归档'];

const districts = [
  '黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区',
  '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区',
  '奉贤区', '崇明区'
];

const form = reactive({
  caseType: '',
  description: '',
  locationAddress: '',
  district: '',
  longitude: null,
  latitude: null
});

const rules = {
  caseType: [{ required: true, message: '请选择事件类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入基本情况描述', trigger: 'blur' }],
  locationAddress: [{ required: true, message: '请输入案发地点', trigger: 'blur' }],
  district: [{ required: true, message: '请选择行政区', trigger: 'change' }]
};

const loadCaseData = async () => {
  try {
    const res = await reportApi.getMyReportDetail(caseId.value);
    const data = res.data;
    caseStatus.value = data.status;

    if (lockedStatuses.includes(data.status)) {
      ElMessage.warning('案件已进入办理阶段，无法修改');
      router.push(`/user/cases/${caseId.value}`);
      return;
    }

    form.caseType = data.caseType;
    form.description = data.description || '';
    form.locationAddress = data.location?.address || '';
    form.district = data.location?.district || '';
    form.longitude = data.location?.longitude || null;
    form.latitude = data.location?.latitude || null;

    ready.value = true;
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '无法加载案件信息');
    router.push('/user/home');
  }
};

const handleAddressBlur = () => {
  if (form.locationAddress && !form.district) {
    for (const district of districts) {
      if (form.locationAddress.includes(district)) {
        form.district = district;
        break;
      }
    }
  }
};

const getCurrentLocation = () => {
  locating.value = true;
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        form.longitude = position.coords.longitude;
        form.latitude = position.coords.latitude;
        ElMessage.success('定位成功');
        locating.value = false;
      },
      () => {
        ElMessage.error('定位失败，请手动输入地址');
        locating.value = false;
      }
    );
  } else {
    ElMessage.error('您的浏览器不支持定位功能');
    locating.value = false;
  }
};

const submit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    submitting.value = true;
    try {
      await reportApi.updateMyReport(caseId.value, {
        caseType: form.caseType,
        description: form.description,
        locationAddress: form.locationAddress,
        district: form.district,
        longitude: form.longitude,
        latitude: form.latitude
      });
      ElMessage.success('案件信息已更新');
      router.push(`/user/cases/${caseId.value}`);
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '更新失败');
    } finally {
      submitting.value = false;
    }
  });
};

const goDetail = () => {
  router.push(`/user/cases/${caseId.value}`);
};

onMounted(async () => {
  await loadCaseData();
});
</script>

<style scoped>
.report-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.location-info {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

