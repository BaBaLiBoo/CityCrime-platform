<template>
  <div class="dashboard-container">
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

const casesByTypeChart = ref(null);
const casesByDistrictChart = ref(null);

onMounted(() => {
  // 使用 nextTick 确保 DOM 已经渲染完毕
  nextTick(() => {
    // --- 初始化案件类型图表 ---
    const typeChart = echarts.init(casesByTypeChart.value);
    const typeOption = {
      tooltip: { trigger: 'item' },
      legend: { top: '5%', left: 'center' },
      series: [{
        name: '案件数量',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
        labelLine: { show: false },
        data: [
          { value: 10, name: '盗窃' },
          { value: 5, name: '抢劫' },
          { value: 8, name: '诈骗' },
          { value: 3, name: '伤害' },
          { value: 2, name: '其他' }
        ]
      }]
    };
    typeChart.setOption(typeOption);

    // --- 初始化区域案件数量图表 ---
    const districtChart = echarts.init(casesByDistrictChart.value);
    const districtOption = {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['杨浦区', '浦东新区', '黄浦区', '徐汇区', '静安区'] },
      yAxis: { type: 'value' },
      series: [{
        data: [12, 20, 15, 8, 7],
        type: 'bar',
        showBackground: true,
        backgroundStyle: { color: 'rgba(180, 180, 180, 0.2)' }
      }]
    };
    districtChart.setOption(districtOption);

    // 监听窗口大小变化，使图表自适应
    window.addEventListener('resize', () => {
      typeChart.resize();
      districtChart.resize();
    });
  });
});
</script>

<style scoped>
.dashboard-container {
  width: 100%;
}
</style>