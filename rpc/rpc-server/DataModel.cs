using System.Text.Json.Serialization;
// 定义 JSON 数据模型
public class DataModel
{
    [JsonPropertyName("name")]
    public string? Name { get; set; }

    [JsonPropertyName("type")]
    public string? Type { get; set; }

    [JsonPropertyName("args")]
    public string[]? Args { get; set; }

    [JsonPropertyName("note")]
    public string? Note { get; set; }

    [JsonPropertyName("result")]
    public string? Result { get; set; }
}
