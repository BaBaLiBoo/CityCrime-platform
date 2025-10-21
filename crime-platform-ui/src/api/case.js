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
    },
    // 关联警员到案件
    addOfficerToCase(caseId, officerId) {
        return apiClient.post(`/cases/${caseId}/officers/${officerId}`);
    },
    // 关联涉案人员到案件
    addPersonToCase(caseId, { idNumber, roleInCase }) {
        return apiClient.post(`/cases/${caseId}/persons`, { idNumber, roleInCase });
    },
    // 从案件中移除警员
    removeOfficerFromCase(caseId, officerId) {
        return apiClient.delete(`/cases/${caseId}/officers/${officerId}`);
    },
    // 从案件中移除涉案人员
    removePersonFromCase(caseId, idNumber) {
        return apiClient.delete(`/cases/${caseId}/persons/${idNumber}`);
    }
};