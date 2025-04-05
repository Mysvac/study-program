import {defineConfig} from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
import vue from '@vitejs/plugin-vue'
import {viteMockServe} from "vite-plugin-mock";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    vue(),
    viteMockServe({
      mockPath: 'mock', // 设置mockPath为根目录下的mock目录
      localEnabled: true, // 设置是否监视mockPath对应的文件夹内文件中的更改
      prodEnabled: false, // 设置是否启用生产环境的mock服务
      watchFiles: true, // 是否监视文件更改
      logger: true  //是否在控制台显示请求日志
    }),
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://10.100.164.22:8080/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }

})
