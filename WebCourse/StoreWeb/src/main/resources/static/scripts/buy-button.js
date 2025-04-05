/**
 * 买商品的按钮
 * */
const buyButtons = document.querySelector('.buy');


buyButtons.addEventListener('click', function() {

    // 获取当前商品的 goodsid
    const goodsid = this.getAttribute('data-goodsid');

    const formData = new FormData();
    formData.append("goodsid", goodsid);
    formData.append("amount", "1");

    // 发送 POST 请求
    fetch('/data/buy-one-goods', {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
            window.location.reload()
        })
        .catch(error => {
            console.log("error:"+error);
        });

});