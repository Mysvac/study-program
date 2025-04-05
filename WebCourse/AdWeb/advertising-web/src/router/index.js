import {createRouter, createWebHashHistory} from 'vue-router'

import LoginAndRegister from "../components/LoginAndRegister.vue";

const SAD = () => import( "../components/SAD.vue");
const MainView = () => import("../components/MainView.vue");
const _404View = () => import("../components/404View.vue")
const routes = [
    {path: '/', component: LoginAndRegister},
    {path: '/mainView', component: MainView},
    {path: '/404View', component: _404View},
    {path: '/SAD/:adId', component: SAD}
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})

export default router;