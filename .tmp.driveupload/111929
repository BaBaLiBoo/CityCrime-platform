import apiClient from './index.js';

export default {
  // 创建举报
  createReport(formData) {
    return apiClient.post('/reports', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  
  // 获取我的举报记录
  getMyReports() {
    return apiClient.get('/reports/my-reports');
  },
  
  // 获取我的举报详情
  getMyReportDetail(caseId) {
    return apiClient.get(`/reports/${caseId}`);
  },

  // 更新我的举报信息
  updateMyReport(caseId, payload) {
    return apiClient.put(`/reports/${caseId}`, payload);
  }
};

