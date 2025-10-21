import { createRouter, createWebHistory } from 'vue-router'
import CaseListView from '../views/CaseListView.vue'
import DashboardView from '../views/DashboardView.vue'
import HomeView from '../views/HomeView.vue'
import PersonListView from '../views/PersonListView.vue';
import LocationListView from '../views/LocationListView.vue';
import OfficerListView from '../views/OfficerListView.vue';
const LoginView = () => import('../views/LoginView.vue');
const RegisterView = () => import('../views/RegisterView.vue');
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      // 路由重定向：当用户访问根路径'/'时，自动跳转到首页
      path: '/',
      redirect: '/home'
    },
    { path: '/login', name: 'Login', component: LoginView },
    { path: '/register', name: 'Register', component: RegisterView },
    {
      path: '/home',
      name: 'Home',
      component: HomeView
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
    ,
    {
      path: '/locations',
      name: 'LocationList',
      component: LocationListView
    },
    {
      path: '/locations/create',
      name: 'LocationCreate',
      component: () => import('../views/LocationForm.vue')
    },
    {
      path: '/officers',
      name: 'OfficerList',
      component: OfficerListView
    }
  ]
})

// 简单路由守卫：登录态校验
router.beforeEach((to, from, next) => {
  const publicPages = ['/login', '/register'];
  const authRequired = !publicPages.includes(to.path);
  const token = localStorage.getItem('auth_token');
  if (authRequired && !token) {
    return next('/login');
  }
  next();
});

export default router