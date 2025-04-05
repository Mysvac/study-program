/**
 * 修改库存和价格
 * */
const updateButtons = document.querySelectorAll('.update-goods');

updateButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前商品的 goodsid
        const goodsid = this.getAttribute('data-goodsid');

        const goodsElement = this.closest('.a-goods');

        // 在当前商品容器中找到数量输入框 (input)
        const price = goodsElement.querySelector('.price-input').value;
        const stock = goodsElement.querySelector('.stock-input').value;

        const formData = new FormData();
        formData.append("goodsid", goodsid);
        formData.append("price",price);
        formData.append("stock",stock);

        // 发送 POST 请求
        fetch('/data/goods-update', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-goodsList';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});

/**
 * 删除商品
 * */
const deleteButtons = document.querySelectorAll('.delete-goods');

deleteButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前商品的 goodsid
        const goodsid = this.getAttribute('data-goodsid');

        const formData = new FormData();
        formData.append("goodsid", goodsid);

        // 发送 POST 请求
        fetch('/data/goods-delete', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-goodsList';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 修改状态
 * */
const ableButtons = document.querySelectorAll('.buy-able');

ableButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前商品的 goodsid
        const goodsid = this.getAttribute('data-goodsid');

        const formData = new FormData();
        formData.append("goodsid", goodsid);

        // 发送 POST 请求
        fetch('/data/goods-able', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-goodsList';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});
