import { createRouter, createWebHistory } from 'vue-router'
import CaseListView from '../views/CaseListView.vue'
import DashboardView from '../views/DashboardView.vue'
import PersonListView from '../views/PersonListView.vue';
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      // 路由重定向：当用户访问根路径'/'时，自动跳转到仪表盘
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: DashboardView
    },
    {
      path: '/cases',
      name: 'CaseList',
      component: CaseListView
    },
    {
      path: '/cases/create',
      name: 'CaseCreate',
      // 使用路由懒加载
      component: () => import('../views/CaseForm.vue')
    },
    {
      path: '/cases/edit/:id',
      name: 'CaseEdit',
      component: () => import('../views/CaseForm.vue'),
      props: true
    },
    {
      path: '/cases/:id',
      name: 'CaseDetail',
      component: () => import('../views/CaseDetailView.vue'),
      props: true
    },
    {
      path: '/persons',
      name: 'PersonList',
      component: PersonListView
    },
    {
      path: '/persons/create',
      name: 'PersonCreate',
      component: () => import('../views/PersonForm.vue')
    },
    {
      path: '/persons/edit/:id',
      name: 'PersonEdit',
      component: () => import('../views/PersonForm.vue'),
      props: true
    }
  ]
})

export default router