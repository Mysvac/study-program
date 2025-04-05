import Mock from "mockjs/dist/mock.js";

export default [
    {
        //http://localhost:8080/api/advertising-table-data
        url: '/api/advertising-chart-data', // 接口 URL
        method: 'post', // 请求方法
        response: (req) => {
            const fixedTags = [
                '电子产品',
                '家居用品',
                '服装服饰',
                '美妆护肤',
                '食品饮料',
                '汽车交通',
                '旅游出行'
            ]
            const chartData = fixedTags.map(tag => {
                const value = Mock.Random.integer(50, 500); // 随机生成广告数量
                const distributed = Mock.Random.integer(0, value); // 随机生成发布数量，不超过广告数量
                return {
                    name: tag,
                    value: value,
                    distributed: distributed
                };
            });
            // 返回模拟的响应数据
            return {
                code: 200,
                message: '获取广告种类与数量成功',
                data: chartData
            };
        }
    }
];