<template>
  <v-chart class="chart" :option="options" autoresize />
</template>

<script setup>
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
} from 'echarts/components';
import VChart from 'vue-echarts';
import {computed, defineProps} from 'vue';

// 注册 ECharts 组件
use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
]);

// 定义 props
const props = defineProps({
  data: {
    type: Array,
    required: true,
  },
  title: {
    type: String,
    default: '',
  },
});

// 使用 computed 监听 props.data 和 props.title 的变化
const options = computed(() => {
  return {
    title: {
      text: props.title, // 使用 props.title
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: props.data.map(d => d.name), // 使用 props.data
    },
    series: [
      {
        name: props.title, // 使用 props.title
        type: 'pie',
        radius: '55%',
        center: ['50%', '60%'],
        data: props.data, // 使用 props.data
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };
});
</script>

<style scoped>
.chart {
  height: 300px;
}
</style>