import Mock from "mockjs";

export default [
    {
        url: "/api/fetch-request-ads", // API 接口路径
        method: "post", // 请求方法
        response: (req) => {
            const { userCookie } = req.body; // 从请求体中获取 userCookie

            // 模拟广告数据
            const adsData = Mock.mock({
                "ads|5-10": [
                    {
                        "id|1-1000": 1, // 广告 ID，从 1 开始递增
                        "title": "@ctitle(5, 15)", // 广告标题，5 到 15 个字符
                        "tag": '@pick(["电子产品", "家居用品", "服装服饰", "美妆护肤", "食品饮料", "汽车交通", "旅游出行"])', // 随机选择广告类型
                        "description": "@cparagraph(2, 5)", // 广告描述，2 到 5 句话
                        "url": "@url('http')", // 广告链接
                    },
                ],
            });

            // 校验 userCookie
            if (userCookie === "null") {
                return {
                    code: 200, // 状态码
                    message: "获取广告申请数据成功", // 提示信息
                    data: adsData.ads, // 返回的广告数据
                };
            } else {
                return {
                    code: 400, // 状态码
                    message: "Cookie Error", // 错误信息
                };
            }
        },
    },
];