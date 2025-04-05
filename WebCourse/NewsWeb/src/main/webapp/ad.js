/*
    获取设备信息
*/
function getDeviceInfo() {
    const devicePixelRatio = window.devicePixelRatio || "";  // 设备像素比
    const userAgent = navigator.userAgent || "";  // 用户代理
    const language = navigator.language || "";  // 浏览器语言
    const maxTouchPoints = navigator.maxTouchPoints || "";  // 最大触摸点数（如果支持）
    const screenWidth = screen.width || "";
    const screenHeight = screen.height || "";
    const hardwareConcurrency = navigator.hardwareConcurrency || "";
    const deviceMemory = navigator.deviceMemory || "";

    // 合并设备信息为一个字符串
    return `${screenWidth}|${screenHeight}|${devicePixelRatio}|${hardwareConcurrency}|${deviceMemory}|${userAgent}|${language}|${maxTouchPoints}`;
}

/*
    不断移动的广告界面
*/
function move_ad(elem_div){

    // 初始化广告的运动参数
    let speedX = 2;  // 水平运动速度
    let speedY = 2;  // 垂直运动速度
    let directionX = 1;  // 水平方向 1: 右移, -1: 左移
    let directionY = 1;  // 垂直方向 1: 下移, -1: 上移
    let isMoving = true;  // 默认广告会移动

    // 创建一个函数用于更新广告的位置
    function moveAd() {
        if (!isMoving) {
            // 如果 isMoving 为 false，停止动画
            return;
        }
        // 获取窗口的宽度和高度
        let windowWidth = window.innerWidth;
        let windowHeight = window.innerHeight;
        // 获取当前的位置
        let rect = elem_div.getBoundingClientRect();

        // 更新位置
        let newX = rect.left + speedX * directionX;
        let newY = rect.top + speedY * directionY;

        // 检查是否到达窗口的右边或左边
        if (newX <= 0 || newX + rect.width >= windowWidth) {
            directionX *= -1;  // 反转水平方向
        }

        // 检查是否到达窗口的上边或下边
        if (newY <= 0 || newY + rect.height >= windowHeight) {
            directionY *= -1;  // 反转垂直方向
        }

        // 更新广告的位置
        elem_div.style.left = `${newX}px`;
        elem_div.style.top = `${newY}px`;

        // 使用 requestAnimationFrame 来持续调用 moveAd，保持平滑的动画效果
        requestAnimationFrame(moveAd);
    }

    // 监听鼠标进入和离开广告元素时，控制动画是否继续
    elem_div.addEventListener('mouseenter', () => {
        isMoving = false;  // 鼠标进入时停止广告移动
    });

    elem_div.addEventListener('mouseleave', () => {
        isMoving = true;   // 鼠标离开时恢复广告移动
        moveAd();
    });

    // 开始运动
    moveAd();
}

/*
    根据json数据data，在页面上生成广告图片
*/
function create_ad(data){
    // 获取广告链接数组
    let adUrlList = data.ad_urls;
    console.log(adUrlList);
    let adUrlsLength = adUrlList.length;
    // 随机获取一个广告
    let randomIndex = Math.floor(Math.random() * adUrlsLength);
    let item = adUrlList[randomIndex];

    // 创建广告相关元素
    let elem_div = document.createElement('div');
    // 设置 <div> 的样式，使其固定在页面最上层
    elem_div.style.position = 'fixed';  // 固定位置
    elem_div.style.zIndex = '9999';     // 设置较高的层级，确保它显示在最上层
    elem_div.style.top = '100px';
    elem_div.style.left = '100px';
    elem_div.style.width = '150px';
    elem_div.style.height = '150px';

    // 创建链接
    let elem_a = document.createElement('a');
    elem_a.href=item.adDescription;

    // 创建广告图片
    let elem_img = document.createElement('img');
    elem_img.src=item.adUrl;
    elem_img.style.width = '100%';
    elem_img.style.height = '100%';


    // 将 <img> 元素嵌套到 <a> 元素中
    elem_a.appendChild(elem_img);
    // 将 <a> 元素嵌套到 <div> 元素中
    elem_div.appendChild(elem_a);
    // 将 <div> 元素添加到 <body> 元素的末尾
    document.body.appendChild(elem_div);
    // 将 <a> 元素嵌套到 <div> 元素中
    elem_div.appendChild(elem_a);

    // 在 <div> 右上角添加一个关闭按钮
    let closeButton = document.createElement('span');
    closeButton.style.position = 'absolute';  // 绝对定位
    closeButton.style.width = '20px';
    closeButton.style.height = '20px';
    closeButton.style.textAlign = "center";
    closeButton.innerText = 'x';  // 设置关闭按钮文本
    closeButton.style.top = '0px';
    closeButton.style.right = '0px';
    closeButton.style.fontSize = '18px';  // 设置字体大小
    closeButton.style.color = '#000';  // 设置颜色为白色
    closeButton.style.backgroundColor = '#EEE';  // 设置颜色为白色
    closeButton.style.cursor = 'pointer';  // 鼠标样式为指针
    closeButton.style.zIndex = '10000';  // 确保关闭按钮在最上层

    // 给关闭按钮添加点击事件，点击时删除广告元素
    closeButton.addEventListener('click', () => {
        elem_div.remove(); // 删除广告元素
    });

    // 将关闭按钮添加到广告 <div> 中
    elem_div.appendChild(closeButton);


    // 设置，让广告不断移动
    move_ad(elem_div);
}


// 哈希编码，转成长度64的字符串（十六进制数的字符串）
function generateDeviceIdentifier() {
    let deviceInfo = getDeviceInfo()

    return CryptoJS.SHA256(deviceInfo).toString(CryptoJS.enc.Hex);
}



// 获取到网址类型
let test = document.getElementById("goods_type");
let tag = test ? test.textContent : "";
let userIdentifier = generateDeviceIdentifier();

/*
    获取广告信息
*/
fetch('http://10.100.164.22:8080/api/ad-click', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        client_id: userIdentifier.substring(0,32),
        user_id: "d312e97ba0265422a0ad7cd222656bf4",
        tag: tag
    }),
    })
    .then(response => response.json())
    .then(data => {
        // 创建广告
        create_ad(data);
    })
    .catch(error => console.error('Error:', error));
