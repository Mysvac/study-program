import Mock from "mockjs/dist/mock.js";

export default [
    {
        url: '/api/advertising-review-data', // 接口 URL
        method: 'post', // 请求方法
        response: (req) => {
            const tableData = Mock.mock({
                'data|10-20': [ // 生成 5 到 10 条数据
                    {
                        'id|1-1000': 1,
                        'tag': '@pick(["电子产品", "家居用品", "服装服饰", "美妆护肤", "食品饮料", "汽车交通", "旅游出行"])', // 随机选择广告类型
                        'title': '@ctitle(1,10)',
                        'description': '@cparagraph(1, 10)', // 随机生成广告描述
                        'distributor': '@cname', // 随机生成发布商名称
                        'cost|100-1000': 1, // 随机生成广告价格，范围是 100 到 1000
                    }
                ]
            });

            // 返回模拟的响应数据
            return {
                code: 200,
                message: '获取需要审核广告表格数据成功',
                data: tableData.data
            };
        }
    },
    {
        url: '/api/advertising-review-data-ok', // 接口 URL
        method: 'post', // 请求方法
        response: (req) => {
            const {id} = req.body;
            // 返回模拟的响应数据
            return {
                code: 200,
                message: '审核广告数据成功',
                id
            };
        }
    }
]