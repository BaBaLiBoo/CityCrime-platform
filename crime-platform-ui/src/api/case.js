import apiClient from './index.js';

export default {
    // 获取所有案件
    getAllCases() {
        return apiClient.get('/cases');
    },
    // 根据ID获取单个案件
    getCaseById(id) {
        return apiClient.get(`/cases/${id}`);
    },
    // 创建一个新案件
    createCase(data) {
        return apiClient.post('/cases', data);
    },
    // 更新一个案件
    updateCase(id, data) {
        return apiClient.put(`/cases/${id}`, data);
    },
    // 删除一个案件
    deleteCase(id) {
        return apiClient.delete(`/cases/${id}`);
    }
};