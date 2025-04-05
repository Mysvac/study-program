
/**
 * 更新数据的按钮
 * */
const updateButtons = document.querySelectorAll('.update-num');
// 为每个按钮添加点击事件
updateButtons.forEach(button => {
    button.addEventListener('click', function() {
        // 获取当前按钮所在的商品容器（.goods）
        const goodsElement = this.closest('.goods');

        // 在当前商品容器中找到数量输入框 (input)
        const quantityInput = goodsElement.querySelector('.goodsnum');

        // 获取输入框中的数量值
        const amount = quantityInput.value;

        // 获取当前商品的 goodsid
        const goodsid = this.getAttribute('data-goodsid');

        const formData = new FormData();
        formData.append("goodsid", goodsid);
        formData.append("amount", amount);

        // 发送 POST 请求
        fetch('/data/cart-amount', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if(response.ok){
                    window.location.reload(true);
                }else {
                    console.log("请求失败");
                }
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 删除数据的按钮
 * */
const deleteButtons = document.querySelectorAll('.delete');

deleteButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前商品的 goodsid
        const goodsid = this.getAttribute('data-goodsid');

        const formData = new FormData();
        formData.append("goodsid", goodsid);
        formData.append("amount", "0");

        // 发送 POST 请求
        fetch('/data/cart-amount', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if(response.ok){
                    window.location.reload();
                }else {
                    console.log("请求失败");
                }
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 买商品*/
const buyButtons = document.querySelectorAll('.buy');
// 为每个按钮添加点击事件
buyButtons.forEach(button => {
    button.addEventListener('click', function() {
        // 获取当前按钮所在的商品容器（.goods）
        const goodsElement = this.closest('.goods');

        // 在当前商品容器中找到数量输入框 (input)
        const quantityInput = goodsElement.querySelector('.goodsnum');

        // 获取输入框中的数量值
        const amount = quantityInput.value;

        // 获取当前商品的 goodsid
        const goodsid = this.getAttribute('data-goodsid');

        const formData = new FormData();
        formData.append("goodsid", goodsid);
        formData.append("amount", amount);

        // 发送 POST 请求
        fetch('/data/buy-goods', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.reload();
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


document.getElementById("del-all").addEventListener('click',function(){
    const formData = new FormData();
    formData.append("sure", "1");
    // 发送 POST 请求
    fetch('/data/del-all-cart', {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
            window.location.reload();
        })
        .catch(error => {
            console.log("error:"+error);
        });
});

document.getElementById("buy-all").addEventListener('click',function(){
    const formData = new FormData();
    formData.append("sure", "1");
    // 发送 POST 请求
    fetch('/data/buy-all', {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
            window.location.reload();
        })
        .catch(error => {
            console.log("error:"+error);
        });
});

document.getElementById("del-un").addEventListener('click',function(){
    const formData = new FormData();
    formData.append("sure", "1");
    // 发送 POST 请求
    fetch('/data/del-un-cart', {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
            window.location.reload();
        })
        .catch(error => {
            console.log("error:"+error);
        });
});
