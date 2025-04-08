<script>
export default {
  name: 'App',
  data() {
    return {
      fibInput: null,
      fibResult: '',
      sortInput: '',
      sortResult: ''
    }
  },
  methods: {
    async calculateFib() {
      if (this.fibInput === null || this.fibInput < 0) {
        this.fibResult = '请输入非负整数';
        return;
      }
      
      try {
        const response = await fetch(`http://localhost:18081/fib?n=${this.fibInput}`);
        this.fibResult = await response.text();
      } catch (error) {
        this.fibResult = '计算失败: ' + error.message;
      }
    },
    async sortArray() {
      if (!this.sortInput.trim()) {
        this.sortResult = '请输入数组';
        return;
      }
      
      try {
        const array = this.sortInput.split(/[, ]+/).map(num => parseInt(num.trim()));
        console.log(JSON.stringify(array))
        const response = await fetch('http://localhost:18081/sort', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json' // 确保设置正确的Content-Type
          },
          body: JSON.stringify(array)
        });
        this.sortResult = await response.text();
      } catch (error) {
        this.sortResult = '排序失败: ' + error.message;
      }
    }
  }
}
</script>

<template>
    <h1>本地服务演示</h1>
    
    <div class="service">
      <h2>斐波那契数列计算</h2>
      <input v-model.number="fibInput" type="number" placeholder="输入数字">
      <button @click="calculateFib">计算</button>
      <p>结果: {{ fibResult }}</p>
    </div>
    
    <div class="service">
      <h2>数组排序</h2>
      <input v-model="sortInput" placeholder="输入数组，如: 3,1,4,2">
      <button @click="sortArray">排序</button>
      <p>结果: {{ sortResult }}</p>
    </div>
</template>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.service {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 5px;
}
input {
  padding: 8px;
  margin-right: 10px;
  width: 200px;
}
button {
  padding: 8px 15px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background: #369f6b;
}
</style>
