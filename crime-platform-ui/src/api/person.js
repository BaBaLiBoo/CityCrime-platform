import apiClient from './index.js';

export default {
    // 获取所有人员列表
    getAllPersons() {
        return apiClient.get('/persons');
    },
    // 创建一个新的人员记录
    createPerson(data) {
        return apiClient.post('/persons', data);
    },
    // 未来可以添加更新、删除等其他人员相关的API
    // getPersonById(id) {
    //     return apiClient.get(`/persons/${id}`);
    // },
    // updatePerson(id, data) {
    //     return apiClient.put(`/persons/${id}`, data);
    // },
    // deletePerson(id) {
    //     return apiClient.delete(`/persons/${id}`);
    // }
};