import apiClient from './index.js';

export default {
  getProfile() {
    return apiClient.get('/auth/profile');
  },
  updateProfile(data) {
    return apiClient.put('/auth/profile', data);
  },
  changePassword(data) {
    return apiClient.put('/auth/password', data);
  }
};

