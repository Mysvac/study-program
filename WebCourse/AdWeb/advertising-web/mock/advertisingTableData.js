import Mock from "mockjs/dist/mock.js";

export default [
    {
        url: '/api/advertising-table-data', // 接口 URL
        method: 'post', // 请求方法
        response: (req) => {
            const tableData = Mock.mock({
                'data|10-20': [ // 生成 5 到 10 条数据
                    {
                        'id|1-1000': 1,
                        'isRequest': "@pick(['已申请','未申请'])",
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
                message: '获取广告表格数据成功',
                data: tableData.data
            };
        }
    },
    {
        url: "/api/advertising-id-table-data", // 模拟获取广告数据的 API 接口
        method: "post", // 请求方法为 POST
        response: (req) => {
            // 模拟生成广告数据
            const tableData = Mock.mock({
                "data|10-20": [
                    {
                        id: "@id", // 随机生成广告 ID
                        status: "@pick(['未发布', '已发布', '审核中'])",
                        tag: "@pick(['电子产品', '家居用品', '服装服饰', '美妆护肤', '食品饮料', '汽车交通', '旅游出行'])", // 随机生成广告类型
                        title: '@ctitle(1,10)',
                        description: "@cparagraph(1, 3)", // 随机生成广告描述
                        distributor: "@cname", // 随机生成发布商名称
                        cost: "@integer(100, 10000)", // 随机生成广告价格
                    },
                ],
            });

            // 模拟返回数据
            return {
                code: 200,
                message: "获取广告数据成功",
                data: tableData.data,
            };
        },
    },
];