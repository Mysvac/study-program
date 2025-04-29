using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;


class SocketServer{
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
            // 解析 JSON
            var jsonData = JsonSerializer.Deserialize<DataModel>(requestData);
            if(jsonData is null) return;

            Console.WriteLine(jsonData.Type);
            if(jsonData.Type == "register"){
                Console.WriteLine(requestData);
                if(jsonData.Name is null || jsonData.Args is null) return;
                try{
                    ServerModel serverModel = new()
                    {
                        Url = jsonData.Args[0],
                        Port = Convert.ToInt32(jsonData.Args[1]),
                        Time = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()
                    };
                    Program.ServerSet[jsonData.Name] = serverModel;

                    Console.WriteLine("register url: " + serverModel.Url + " " + serverModel.Port.ToString());

                    byte[] responseBuffer = Encoding.UTF8.GetBytes("register success");
                    stream.Write(responseBuffer, 0 , responseBuffer.Length);
                }
                catch(Exception){}
            }
            else if(jsonData.Type == "request"){
                Console.WriteLine(requestData);
                // 返回远程服务器地址
                ServerModel randomServer;
                do{
                    if(Program.ServerSet.Count == 0){
                        byte[] res = Encoding.UTF8.GetBytes("have not remote server");
                        stream.Write(res, 0 , res.Length);
                        Console.WriteLine("have not remote server");
                        return;
                    }
                    int index = Program.Randomer.Next(Program.ServerSet.Count);
                    randomServer = Program.ServerSet.Values.ElementAt(index);
                    if(DateTimeOffset.UtcNow.ToUnixTimeMilliseconds() - randomServer.Time > 1800000L){
                        Program.ServerSet.Remove(Program.ServerSet.Keys.ElementAt(index));
                        randomServer.Url = null;
                    }
                }while(randomServer.Url is null);

                if(randomServer.Url is null) return;

                string response = randomServer.Url + ":" + (randomServer.Port.ToString() ?? "");
                Console.WriteLine("response url: " + response);

                byte[] responseBuffer = Encoding.UTF8.GetBytes(response);
                stream.Write(responseBuffer, 0 , responseBuffer.Length);
            }
        }
        catch (Exception)
        {
            Console.WriteLine($"Error");
        }
    }

}

