<template>
  <div class="page-container">
    <div class="page-header">
      <h2>警员管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">新增警员</el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="警员编号">
          <el-input v-model="searchForm.officerId" placeholder="请输入警员编号" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="searchForm.department" placeholder="请输入部门" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchOfficers">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 警员列表 -->
    <el-card>
      <el-table :data="filteredOfficers" v-loading="loading" stripe>
        <el-table-column prop="officerId" label="警员编号" width="120" />
        <el-table-column prop="personDetails.name" label="姓名" width="100" />
        <el-table-column prop="personDetails.idNumber" label="身份证号" width="180" />
        <el-table-column prop="personDetails.gender" label="性别" width="80" />
        <el-table-column prop="personDetails.birthDate" label="出生日期" width="120" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="personDetails.contactInfo" label="联系方式" width="150" />
        <el-table-column prop="personDetails.address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="info" @click="viewOfficer(row)">查看</el-button>
            <el-button size="small" @click="editOfficer(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteOfficer(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑警员对话框 -->
    <el-dialog 
      :title="editingOfficer ? '编辑警员' : '新增警员'" 
      v-model="showCreateDialog" 
      width="600px"
    >
      <el-form :model="officerForm" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="警员编号" prop="officerId">
              <el-input v-model="officerForm.officerId" :disabled="editingOfficer" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="officerForm.name" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idNumber">
              <el-input v-model="officerForm.idNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="officerForm.gender" placeholder="请选择">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker 
                v-model="officerForm.birthDate" 
                type="date" 
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contactInfo">
              <el-input v-model="officerForm.contactInfo" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="地址" prop="address">
          <el-input v-model="officerForm.address" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="officerForm.department" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="officerForm.position" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveOfficer" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import * as officerApi from '@/api/officer.js';

const loading = ref(false);
const saving = ref(false);
const showCreateDialog = ref(false);
const editingOfficer = ref(null);
const officers = ref([]);
const formRef = ref();

const searchForm = ref({
  name: '',
  officerId: '',
  department: ''
});

const officerForm = ref({
  officerId: '',
  name: '',
  idNumber: '',
  gender: '',
  birthDate: '',
  address: '',
  contactInfo: '',
  department: '',
  position: ''
});

const rules = {
  officerId: [{ required: true, message: '请输入警员编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idNumber: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }]
};

// 过滤后的警员列表
const filteredOfficers = computed(() => {
  let result = officers.value;
  
  if (searchForm.value.name) {
    result = result.filter(officer => 
      officer.personDetails.name.includes(searchForm.value.name)
    );
  }
  
  if (searchForm.value.officerId) {
    result = result.filter(officer => 
      officer.officerId.includes(searchForm.value.officerId)
    );
  }
  
  if (searchForm.value.department) {
    result = result.filter(officer => 
      officer.department.includes(searchForm.value.department)
    );
  }
  
  return result;
});

// 加载警员列表
const loadOfficers = async () => {
  loading.value = true;
  try {
    const response = await officerApi.getAllOfficers();
    officers.value = response.data;
  } catch (error) {
    ElMessage.error('加载警员列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索警员
const searchOfficers = () => {
  // 搜索逻辑已在 computed 中实现
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: '',
    officerId: '',
    department: ''
  };
};

// 查看警员详情
const viewOfficer = (officer) => {
  ElMessageBox.alert(
    `
    <div style="text-align: left;">
      <h3>警员基本信息</h3>
      <p><strong>警员编号：</strong>${officer.officerId}</p>
      <p><strong>姓名：</strong>${officer.personDetails.name}</p>
      <p><strong>身份证号：</strong>${officer.personDetails.idNumber}</p>
      <p><strong>性别：</strong>${officer.personDetails.gender}</p>
      <p><strong>出生日期：</strong>${officer.personDetails.birthDate || '未填写'}</p>
      <p><strong>地址：</strong>${officer.personDetails.address || '未填写'}</p>
      <p><strong>联系方式：</strong>${officer.personDetails.contactInfo || '未填写'}</p>
      <hr style="margin: 10px 0;">
      <h3>工作信息</h3>
      <p><strong>部门：</strong>${officer.department}</p>
      <p><strong>职位：</strong>${officer.position}</p>
    </div>
    `,
    '警员详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  );
};

// 编辑警员
const editOfficer = (officer) => {
  editingOfficer.value = officer;
  officerForm.value = {
    officerId: officer.officerId,
    name: officer.personDetails.name,
    idNumber: officer.personDetails.idNumber,
    gender: officer.personDetails.gender,
    birthDate: officer.personDetails.birthDate,
    address: officer.personDetails.address,
    contactInfo: officer.personDetails.contactInfo,
    department: officer.department,
    position: officer.position
  };
  showCreateDialog.value = true;
};

// 删除警员
const deleteOfficer = async (officer) => {
  try {
    await ElMessageBox.confirm(`确定要删除警员 ${officer.personDetails.name} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await officerApi.deleteOfficer(officer.officerId);
    ElMessage.success('删除成功');
    loadOfficers();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

// 保存警员
const saveOfficer = async () => {
  try {
    await formRef.value.validate();
    saving.value = true;
    
    if (editingOfficer.value) {
      // 编辑
      await officerApi.updateOfficer(editingOfficer.value.officerId, officerForm.value);
      ElMessage.success('更新成功');
    } else {
      // 新增
      try {
        await officerApi.createOfficer(officerForm.value);
        ElMessage.success('创建成功');
      } catch (error) {
        if (error.response?.status === 409) {
          ElMessage.error('警员编号已存在，请使用其他编号');
          return;
        }
        // 显示后端返回的具体错误信息
        const errorMessage = error.response?.data?.message || '创建失败';
        ElMessage.error(errorMessage);
      }
    }
    
    showCreateDialog.value = false;
    resetForm();
    loadOfficers();
  } catch (error) {
    if (error.response?.status !== 409) {
      ElMessage.error(editingOfficer.value ? '更新失败' : '创建失败');
    }
  } finally {
    saving.value = false;
  }
};

// 重置表单
const resetForm = () => {
  editingOfficer.value = null;
  officerForm.value = {
    officerId: '',
    name: '',
    idNumber: '',
    gender: '',
    birthDate: '',
    address: '',
    contactInfo: '',
    department: '',
    position: ''
  };
};

onMounted(() => {
  loadOfficers();
});
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}
</style>
