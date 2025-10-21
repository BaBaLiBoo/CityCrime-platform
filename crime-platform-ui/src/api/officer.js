import apiClient from './index.js';

// 警员相关 API
export const getAllOfficers = () => apiClient.get('/officers');
export const getOfficerById = (id) => apiClient.get(`/officers/${id}`);
export const createOfficer = (data) => apiClient.post('/officers', data);
export const updateOfficer = (id, data) => apiClient.put(`/officers/${id}`, data);
export const deleteOfficer = (id) => apiClient.delete(`/officers/${id}`);

