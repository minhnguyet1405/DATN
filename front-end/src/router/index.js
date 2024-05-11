import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    redirect: '/profile/index',
    component: Layout,
    meta: { title: 'Tổng quan', icon: 'dashboard', roles: ['admin'] },
    children: [
      {
        path: '/Dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: {
          title: 'Tổng quan'
        }
      }
    ]
  },
  {
    path: '/director',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/directory/index'),
        name: 'Events',
        meta: { title: 'Danh bạ Elcom', icon: 'el-icon-phone', affix: true }
      }
    ]
  },
  {
    path: '/approve',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/boardroom/index'),
        name: 'Traffic',
        meta: {
          title: 'Phê duyệt',
          icon: 'el-icon-s-shop',
          affix: true
        }
      }
    ]
  },
  {
    path: '/leave',
    component: Layout,
    hidden: false,
    meta: {
      title: 'Đăng ký vắng măt',
      icon: 'el-icon-s-management',
      affix: true
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/leave/index'),
        name: 'leave',
        meta: { title: 'Đăng ký vắng măt' }
      },
      {
        path: 'manager',
        component: () => import('@/views/leave/manager'),
        name: 'leaveManager',
        meta: { title: 'Quản lý vắng măt' }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Trang cá nhân', icon: 'user', noCache: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  {
    path: '/checkinout',
    component: Layout,
    meta: {
      title: 'Chấm công',
      icon: 'el-icon-location'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/checkinout/index'),
        name: 'checkinout',
        meta: { title: 'Dữ liệu chấm công', roles: ['admin'] }
      }, {
        path: 'hand',
        component: () => import('@/views/checkinout/checkinHand'),
        name: 'checkinHand',
        meta: { title: 'Chấm công thủ công', roles: ['admin'] }
      },
      {
        path: 'adminList',
        component: () => import('@/views/checkinout/adminList'),
        name: 'adminList',
        meta: { title: 'Quản lý chấm công', roles: ['admin'] }
      },
      {
        path: 'ai',
        component: () => import('@/views/AI/checkin'),
        name: 'ai',
        meta: {
          title: 'Chấm công khuân mặt',
          roles: ['admin']
        }
      }
    ]
  },

  {
    path: '/accounts',
    component: Layout,
    hidden: false,
    meta: {
      title: 'Quản lý',
      icon: 'el-icon-user-solid',
      roles: ['admin']
    },
    children: [
      {
        path: '',
        component: () => import('@/views/accounts/index'),
        name: 'Accounts',
        meta: {
          title: 'Quản lý tài khoản'
        }
      },
      {
        path: 'department',
        component: () => import('@/views/accounts/department'),
        name: 'Department',
        meta: {
          title: 'Quản lý phòng ban'
        }
      }
    ]
  },
  {
    path: '/violation',
    component: Layout,
    children: [
      {
        path: 'camera',
        component: () => import('@/views/violation/index'),
        name: 'violation',
        meta: {
          title: 'Danh sách vi phạm',
          icon: 'el-icon-error',
          roles: ['admin']
        }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () =>
  new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes
  })

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
