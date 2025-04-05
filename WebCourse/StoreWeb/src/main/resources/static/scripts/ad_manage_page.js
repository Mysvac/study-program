document.getElementById("submit").addEventListener('click', (e) => {
    e.preventDefault()

    let adEnable = document.getElementById("adEnable").checked;
    const adPosition = document.getElementById("adPosition").value;

    const formData = new FormData();
    formData.append("adEnable", adEnable);
    formData.append("adPosition", adPosition);

    // 发送 POST 请求
    fetch('/data/ad-manager', {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
            window.location.reload(true);
        })
        .catch(error => {
            console.log("error:"+error);
        });

});