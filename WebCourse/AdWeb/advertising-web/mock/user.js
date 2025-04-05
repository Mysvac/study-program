export default [
    {
        url: '/api/register', // 匹配的URL
        method: 'post', // 请求方法
        response: (req) => {
            const {username, name, password, verifiedPassword} = req.body;

            // 简单的验证逻辑
            if (!username || !password || !name || !verifiedPassword
                || verifiedPassword !== password) {
                return {
                    code: 400,
                    message: '用户名、密码和用户名不能为空',
                };
            }

            // 模拟注册成功
            return {
                code: 200,
                message: '注册成功',
                data: {
                    username,
                    name,
                    verifiedPassword,
                    password
                },
            };
        },
    },
    {
        url: '/api/login', // 匹配的URL
        method: 'post', // 请求方法
        response: (req) => {
            const {username, password} = req.body;

            // 简单的验证逻辑
            if (!username || !password) {
                return {
                    code: 400,
                    message: '用户名和密码不能为空',
                };
            }
            if (username !== "123" || password !== "123") {
                return {
                    code: 401,
                    message: '用户名或密码错误',
                };
            }
            return {
                code: 200,
                message: '登陆成功',
                data: {
                    name:"Asaki",
                    cookie:"AS334-1",
                    role: "admin"
                },
            };
        },
    }
];

