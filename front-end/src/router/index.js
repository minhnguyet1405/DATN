import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/* Router Modules */

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
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
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'Dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'dashboard',
        meta: { title: 'Tổng quan', icon: 'dashboard', affix: true }
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
    path: '/checkinout',
    component: Layout,
    hidden: false,
    meta: {
      title: 'Chấm công',
      icon: 'el-icon-location'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/checkinout/index'),
        name: 'checkinout',
        meta: { title: 'CheckIN' },
        roles: [!'admin']
      },
      {
        path: 'adminList',
        component: () => import('@/views/checkinout/adminList'),
        name: 'adminList',
        meta: { title: 'Quản lý chấm công' },
        roles: ['admin']
      },
      {
        path: 'ai',
        component: () => import('@/views/AI/checkin'),
        name: 'ai',
        meta: {
          title: 'Chấm công khuân mặt',
          affix: true
        }
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
  // {
  //   path: '/AI',
  //   component: Layout,
  //   meta: {
  //     title: 'AI',
  //     icon: 'el-icon-user-solid',
  //     roles: ['admin'],
  //     hidden: true
  //   },
  //   children: [
  //     {
  //       path: 'camera',
  //       component: () => import('@/views/AI/camera'),
  //       name: 'camera',
  //       meta: {
  //         title: 'Mở cam',
  //         icon: 'el-icon-s-custom',
  //         affix: true
  //       }
  //     },
  //     {
  //       path: 'checkin',
  //       component: () => import('@/views/AI/checkin'),
  //       name: 'checkin',
  //       meta: {
  //         title: 'Chấm công',
  //         icon: 'el-icon-s-custom',
  //         affix: true
  //       }
  //     }
  //   ]
  // },

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
