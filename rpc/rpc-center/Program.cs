using System.Text.Json;

class Program{
    public static Dictionary<string, ServerModel> ServerSet = new ();
    public static string FilePath = "ServerSet.ss";
    public static Random Randomer = new Random();

    public static void Main(){
        LoadServers();

        var server = new SocketServer(18083);
        server.Start();

        SaveServers();
    }

    public static void SaveServers()
    {
        try
        {
            foreach( var it in ServerSet.Keys ){
                if(ServerSet[it].Time == null || DateTimeOffset.UtcNow.ToUnixTimeMilliseconds() - ServerSet[it].Time > 1800000L){
                    ServerSet.Remove(it);
                }
            }
            string json = JsonSerializer.Serialize(ServerSet);
            File.WriteAllText(FilePath, json);
            Console.WriteLine("服务器数据已保存到文件。");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"保存服务器数据时发生错误：{ex.Message}");
        }
    }

    public static void LoadServers()
    {
        if (File.Exists(FilePath))
        {
            try
            {
                string json = File.ReadAllText(FilePath);
                ServerSet = JsonSerializer.Deserialize<Dictionary<string, ServerModel>>(json) ?? new ();
                foreach( var it in ServerSet.Keys ){
                    if(ServerSet[it].Time == null || DateTimeOffset.UtcNow.ToUnixTimeMilliseconds() - ServerSet[it].Time > 1800000L){
                        ServerSet.Remove(it);
                    }
                }
                Console.WriteLine("服务器数据已从文件加载。");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"加载服务器数据时发生错误：{ex.Message}");
            }
        }
        else
        {
            Console.WriteLine("未找到服务器数据文件，将使用默认数据。");
        }
    }

}


