import Mock from "mockjs/dist/mock.js";

export default [{
    url: '/api/website-feedback',
    method: 'get',
    response: (req) => {
        const dailyActiveUsers = Mock.Random.integer(90000, 110000); // 每日活跃用户随机数
        const monthlyActiveUsers = Mock.Random.integer(600000, 750000); // 每月活跃用户随机数
        const newTransactionsToday = Mock.Random.integer(70000, 80000); // 今日新交易随机数
        return {
            code: 200,
            message:'更新仪表盘状态',
            data: {
                dailyActiveUsers,
                monthlyActiveUsers,
                newTransactionsToday
            },
        };
    }
}]
