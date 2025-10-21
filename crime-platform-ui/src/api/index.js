import axios from 'axios';

// 创建一个 Axios 实例
const apiClient = axios.create({
    // 这里的 baseURL 会被加在所有请求的前面
    // 它指向我们 Spring Boot 后端的地址
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

// 附加认证 token 拦截器
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('auth_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 全局响应错误处理（未授权等）
apiClient.interceptors.response.use(
    r => r,
    err => {
        if (err?.response?.status === 401) {
            localStorage.removeItem('auth_token');
            window.location.href = '/login';
        }
        return Promise.reject(err);
    }
);

export default apiClient;