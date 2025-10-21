import apiClient from './index.js';

export default {
    getAllLocations() {
        return apiClient.get('/locations');
    },
    createLocation(data) {
        return apiClient.post('/locations', data);
    },
};