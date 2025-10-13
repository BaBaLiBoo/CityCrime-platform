import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// 1. 完整导入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 2. 应用 Element Plus
app.use(ElementPlus)

app.mount('#app')