export default [
    {
        url: '/api/show-ad', // 匹配的URL
        method: 'get', // 请求方法
        response: (req) => {
            const {adId} = req.body;

            if (adId === null) {
                return {
                    code: 400,
                    message: '错误访问',
                };
            }

            return {
                code: 200,
                message: '访问成功',
                data: {
                    title:"123",
                    tags:'test tags',
                    description:'test description',
                    fileType:'test file type',
                    fileUrl:'test file url',
                },
            };
        },
    }
]