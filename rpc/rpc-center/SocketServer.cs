using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;


class SocketServer
{
    private readonly TcpListener _listener;
    private readonly int _port;

    public SocketServer(int port)
    {
        _port = port;
        _listener = new TcpListener(IPAddress.Any, port);
    }

    public void Start()
    {
        _listener.Start();
        Console.WriteLine($"Server started on port {_port}");

        while (true)
        {
            // 等待客户端连接
            TcpClient client = _listener.AcceptTcpClient();
            Console.WriteLine("New client connected.");

            // 开启子线程处理请求
            ThreadPool.QueueUserWorkItem(HandleClient, client);
        }
    }

    private static void HandleClient(object? state)
    {
        if(state is null) return;
        using TcpClient client = (TcpClient)state;
        using NetworkStream stream = client.GetStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        try
        {
            // 读取客户端发送的数据
            StringBuilder stringBuilder = new StringBuilder();
            while ((bytesRead = stream.Read(buffer, 0, buffer.Length)) > 0)
            {
                stringBuilder.Append(Encoding.UTF8.GetString(buffer, 0, bytesRead));
                if (bytesRead < buffer.Length) break; // 数据读取完毕
            }

            string requestData = stringBuilder.ToString();
            Console.WriteLine(requestData);
            // 解析 JSON
            var jsonData = JsonSerializer.Deserialize<DataModel>(requestData);
            if(jsonData is null) return;

            Console.WriteLine(jsonData.Type);
            // 转发到远程服务器
            if(jsonData.Type == "request"){
                if(Program.ServerSet.Count == 0){
                    byte[] res = Encoding.UTF8.GetBytes("have not remote server");
                    stream.Write(res, 0 , res.Length);
                    Console.WriteLine("have not remote server");
                    return;
                }
                int index = Program.Randomer.Next(Program.ServerSet.Count);
                // 通过 Values 获取随机值
                ServerModel randomServer = Program.ServerSet.Values.ElementAt(index);
                if(randomServer.Url is null) return;

                DataModel response = SendRequest(randomServer.Url, randomServer.Port, jsonData);

                Console.WriteLine("response: ");
                Console.WriteLine(JsonSerializer.Serialize(response));

                // 发射成功，更新时间
                if(response.Type is not null) randomServer.time =  DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
                byte[] responseBuffer = Encoding.UTF8.GetBytes(response.Result ?? "Remote handle Failed");
                stream.Write(responseBuffer, 0 , responseBuffer.Length);
            }
            else if(jsonData.Type == "register"){
                if(jsonData.Name is null || jsonData.Args is null) return;
                try{
                    ServerModel serverModel = new()
                    {
                        Url = jsonData.Args[0],
                        Port = Convert.ToInt32(jsonData.Args[1]),
                        time = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()
                    };
                    Program.ServerSet[jsonData.Name] = serverModel;

                    byte[] responseBuffer = Encoding.UTF8.GetBytes("register success");
                    stream.Write(responseBuffer, 0 , responseBuffer.Length);
                }
                catch(Exception){}
            }

        }
        catch (Exception)
        {
            Console.WriteLine($"Error");
        }
    }


    private static DataModel SendRequest(string url, int? port, DataModel message)
    {
        try
        {
            // 1. 创建 Socket
            using Socket client = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            // 2. 连接远程服务
            client.Connect(new IPEndPoint(IPAddress.Parse(url), port ?? 80));

            // 3. 发送数据（UTF-8编码）
            byte[] data = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(message));
            client.Send(data);

            // 3. 接收响应数据
            byte[] buffer = new byte[1024];
            int received = client.Receive(buffer);
            string responseJson = Encoding.UTF8.GetString(buffer, 0, received);

            // 4. 反序列化为 DataModel
            return JsonSerializer.Deserialize<DataModel>(responseJson) ?? new DataModel(){ Result = "failed" };
        }
        catch (Exception)
        {
            return new DataModel(){
                Result = "failed"
            };
        }
    }

}

