<template>
  <div class="dashboard-container">
    <div class="toolbar">
      <el-button type="primary" @click="seedExamples" :loading="seeding">生成示例案件</el-button>
      <el-button @click="loadData">刷新</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>案件类型分布</template>
          <div ref="casesByTypeChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>各行政区案件数量</template>
          <div ref="casesByDistrictChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
 </template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import caseApi from '@/api/case.js';
import locationApi from '@/api/location.js';
import { ElMessage } from 'element-plus';

const casesByTypeChart = ref(null);
const casesByDistrictChart = ref(null);
const seeding = ref(false);

const loadData = async () => {
  try {
    const res = await caseApi.getAllCases();
    const list = res.data || [];
    renderCharts(list);
  } catch (e) {
    ElMessage.error('加载案件数据失败');
  }
};

const renderCharts = (cases) => {
  // 类型统计
  const typeMap = new Map();
  cases.forEach(c => {
    const k = c.caseType || '其他';
    typeMap.set(k, (typeMap.get(k) || 0) + 1);
  });
  const typeData = Array.from(typeMap.entries()).map(([name, value]) => ({ name, value }));

  const typeChart = echarts.init(casesByTypeChart.value);
  typeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      name: '案件数量', type: 'pie', radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
      labelLine: { show: false },
      data: typeData
    }]
  });

  // 行政区统计（基于地点字符串包含区名）
  const districts = ['黄浦区','徐汇区','长宁区','静安区','普陀区','虹口区','杨浦区','闵行区','宝山区','嘉定区','浦东新区','金山区','松江区','青浦区','奉贤区','崇明区'];
  const districtCounts = districts.map(d => {
    const count = cases.filter(c => (c.locationAddress || c.locationAddress === undefined) ? (c.locationAddress || c.locationAddress === undefined) : true).length; // placeholder, will replace
    return 0;
  });
  // 纠正：真实统计
  const counts = new Array(districts.length).fill(0);
  cases.forEach(c => {
    const address = c.locationAddress || c.locationAddress === undefined ? (c.locationAddress || '') : (c.location?.address || '');
    const district = districts.find(d => address.includes(d));
    if (district) {
      const idx = districts.indexOf(district);
      counts[idx] += 1;
    }
  });

  const districtChart = echarts.init(casesByDistrictChart.value);
  districtChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: districts },
    yAxis: { type: 'value' },
    series: [{ data: counts, type: 'bar', showBackground: true, backgroundStyle: { color: 'rgba(180, 180, 180, 0.2)' } }]
  });

  window.addEventListener('resize', () => {
    typeChart.resize();
    districtChart.resize();
  });
};

const seedExamples = async () => {
  seeding.value = true;
  try {
    // 预置三条案件（不同类型和区）
    const now = new Date();
    const iso = (d) => d.toISOString().slice(0,19);
    const samples = [
      { caseTitle: '手机被盗', caseType: '盗窃', status: '已接报', reportTime: iso(new Date(now.getTime()-86400000)), description: '公共场所被盗', locationAddress: '黄浦区南京东路xxx号' },
      { caseTitle: '电诈案件', caseType: '诈骗', status: '立案侦查', reportTime: iso(new Date(now.getTime()-3600*1000*5)), description: '网购诈骗', locationAddress: '浦东新区张江路xxx号' },
      { caseTitle: '入室抢劫', caseType: '抢劫', status: '立案侦查', reportTime: iso(now), description: '夜间入室', locationAddress: '杨浦区政益路xxx号' },
    ];
    for (const s of samples) {
      await caseApi.createCase(s);
    }
    ElMessage.success('示例案件已生成');
    await loadData();
  } catch (e) {
    ElMessage.error('生成示例失败');
  } finally {
    seeding.value = false;
  }
};

onMounted(async () => {
  await nextTick();
  await loadData();
});
</script>

<style scoped>
.dashboard-container {
  width: 100%;
}
.toolbar { margin-bottom: 12px; }
</style>