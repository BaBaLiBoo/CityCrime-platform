<template>
  <div class="report-container">
    <el-card>
      <template #header>
        <h2>违法事件举报</h2>
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
        
        <el-form-item label="行政区">
          <el-select v-model="form.district" placeholder="请选择行政区" style="width: 100%">
            <el-option label="黄浦区" value="黄浦区" />
            <el-option label="徐汇区" value="徐汇区" />
            <el-option label="长宁区" value="长宁区" />
            <el-option label="静安区" value="静安区" />
            <el-option label="普陀区" value="普陀区" />
            <el-option label="虹口区" value="虹口区" />
            <el-option label="杨浦区" value="杨浦区" />
            <el-option label="闵行区" value="闵行区" />
            <el-option label="宝山区" value="宝山区" />
            <el-option label="嘉定区" value="嘉定区" />
            <el-option label="浦东新区" value="浦东新区" />
            <el-option label="金山区" value="金山区" />
            <el-option label="松江区" value="松江区" />
            <el-option label="青浦区" value="青浦区" />
            <el-option label="奉贤区" value="奉贤区" />
            <el-option label="崇明区" value="崇明区" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="定位">
          <el-button @click="getCurrentLocation" :loading="locating">获取当前位置</el-button>
          <div v-if="form.longitude && form.latitude" class="location-info">
            <span>经度: {{ form.longitude }}, 纬度: {{ form.latitude }}</span>
          </div>
        </el-form-item>
        
        <el-form-item label="多媒体上传">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            multiple
            :limit="5"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持图片、视频等文件，最多5个，单个文件不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitReport" :loading="submitting">提交举报</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/api/index.js';
import reportApi from '@/api/report.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const formRef = ref(null);
const submitting = ref(false);
const locating = ref(false);
const fileList = ref([]);

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
  locationAddress: [{ required: true, message: '请输入案发地点', trigger: 'blur' }]
};

const handleAddressBlur = () => {
  // 尝试从地址中提取行政区
  if (form.locationAddress && !form.district) {
    const districts = ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', 
                      '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区'];
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
      (error) => {
        ElMessage.error('定位失败，请手动输入地址');
        locating.value = false;
      }
    );
  } else {
    ElMessage.error('您的浏览器不支持定位功能');
    locating.value = false;
  }
};

const handleFileChange = (file, files) => {
  fileList.value = files;
};

const handleFileRemove = (file, files) => {
  fileList.value = files;
};

const submitReport = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    
    submitting.value = true;
    try {
      const formData = new FormData();
      
      // 添加举报数据（JSON格式）
      const reportData = {
        caseType: form.caseType,
        description: form.description,
        locationAddress: form.locationAddress,
        district: form.district,
        longitude: form.longitude,
        latitude: form.latitude
      };
      formData.append('report', new Blob([JSON.stringify(reportData)], { type: 'application/json' }));
      
      // 添加文件
      fileList.value.forEach((file) => {
        if (file.raw) {
          formData.append('files', file.raw);
        }
      });
      
      const res = await reportApi.createReport(formData);
      
      ElMessage.success('举报提交成功！');
      router.push('/user/my-reports');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '提交失败，请重试');
    } finally {
      submitting.value = false;
    }
  });
};

const resetForm = () => {
  formRef.value?.resetFields();
  fileList.value = [];
  form.longitude = null;
  form.latitude = null;
};
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
</style>

