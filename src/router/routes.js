// // profile
// import profilePage from '../parges/profile/Profile.vue'
// import profilePostsPage from '../parges/profile/ProfilePostsPage.vue'

// // single views
// import homePage from '../views/Home.vue'
// import newsPage from '../views/news/NewsPage.vue'
// import loginPage from '../views/Login.vue'
// // import profilePage from '../views/Profile.vue'
// import notFoundPage from '../views/NotFound.vue'
// profile
import profilePage from '../pages/profile/ProfilePage.vue'
import profilePostsPage from '../pages/profile/ProfilePostsPage.vue'

// single pages
import homePage from '../pages/Home.vue'
import newsPage from '../pages/news/NewsPage.vue'
import loginPage from '../pages/Login.vue'
import todosPage from '../pages/Todos.vue'
import heroesPage from '../pages/Heroes.vue'
import notFoundPage from '../pages/NotFound.vue'

import {
  routePropResolver
} from './util'
// import {
//   DOMAIN_TITLE
// } from '../.env'
const DOMAIN_TITLE = "ashdevtools.com"

export const routes = [{
    path: '/',
    name: 'index',
    component: homePage,
    meta: {
      title: `${DOMAIN_TITLE} | home`
    }
  },
  {
    path: '/profile',
    component: profilePage,
    meta: {
      isAuth: false,

    },
  },
  {
    path: '/news',
    name: 'news',
    component: newsPage,
    meta: {
      title: `${DOMAIN_TITLE} | news`
    },
    props: routePropResolver
  },
  {
    path: '/profile',
    component: profilePage,
    meta: {
      isAuth: true,
      title: `${DOMAIN_TITLE} | profile`
    },
    children: [{
      path: '',
      name: 'profile',
      component: profilePostsPage
    }]
  },
  {
    path: '/login',
    name: 'login',
    component: loginPage,
    meta: {
      title: `${DOMAIN_TITLE} | login`
    }
  },
  {
    path: '/todos',
    name: 'todos',
    component: todosPage,
    meta: {
      isAuth: false,

    },
  },
  {
    path: '/heroes',
    name: 'heroes',
    component: heroesPage,
    meta: {
      isAuth: false,

    },
  },
  {
    path: '*',
    component: notFoundPage,
    meta: {
      title: `${DOMAIN_TITLE} | not found`
    }
  }
]