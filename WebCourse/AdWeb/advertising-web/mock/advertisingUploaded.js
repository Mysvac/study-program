import Mock from "mockjs";

export default [
    {
        url: "/api/upload-advertising", // 模拟上传广告的 API 接口
        method: "post", // 请求方法为 POST
        response: (req) => {
            // 解析请求体中的数据
            const { tag,title,status, description, distributor, cost, fileType, file } = req.body;

            // 模拟生成广告 ID
            const id = Mock.Random.guid();

            // 模拟返回数据
            return {
                code: 200,
                message: "广告上传成功",
                data: {
                    id, // 广告 ID
                    title,
                    status,
                    tag, // 广告类型
                    description, // 广告描述
                    distributor, // 发布商
                    cost, // 广告价格
                    fileType, // 文件类型
                    file: file ? file.name : "未上传文件", // 文件名
                },
            };
        },
    },
    {
        url: "/api/advertising-file-upload", // 模拟文件上传的 API 接口
        method: "post", // 请求方法为 POST
        response: (req) => {
            // 模拟文件上传成功
            const file = req.file || { name: "unknown.file" }; // 获取上传的文件名
            const fileType = file.type || "unknown"; // 获取文件类型

            // 模拟返回数据
            return {
                code: 200,
                message: "文件上传成功",
                data: {
                    fileId: Mock.Random.guid(), // 模拟生成的文件 ID
                    fileName: file.name, // 文件名
                    fileType: fileType, // 文件类型
                    url: `http://example.com/uploads/${file.name}`, // 模拟文件的 URL
                },
            };
        },
    },
];