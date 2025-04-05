/**
 * 自动打印 JSON 数据到控制台
 * @param {Object} jsonData - 需要打印的 JSON 数据
 * @param {string} [label='JSON Data'] - 打印时的标签（可选）
 */
function printJsonToConsole(jsonData, label = 'JSON Data') {
    try {
        // 将 JSON 数据转换为格式化字符串
        const formattedJson = JSON.stringify(jsonData, null, 2);

        // 打印到控制台
        console.log(`%c${label}:`, 'color: #007bff; font-weight: bold;');
        console.log(formattedJson);
    } catch (error) {
        console.error('无法解析 JSON 数据:', error);
    }
}

export default printJsonToConsole;