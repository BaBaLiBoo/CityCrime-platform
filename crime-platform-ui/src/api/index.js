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

// 你也可以在这里添加请求拦截器，例如，未来用于附加认证 token
// apiClient.interceptors.request.use(config => { ... });

export default apiClient;