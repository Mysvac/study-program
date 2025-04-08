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

    private static void HandleClient(object? state) {
        if(state is null) return;
        using TcpClient client = (TcpClient)state;
        using NetworkStream stream = client.GetStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        try{
            // 读数据
            StringBuilder stringBuilder = new StringBuilder();
            while ((bytesRead = stream.Read(buffer, 0, buffer.Length)) > 0){
                stringBuilder.Append(Encoding.UTF8.GetString(buffer, 0, bytesRead));
                if (bytesRead < buffer.Length) break; // 数据读取完毕
            }
            string requestData = stringBuilder.ToString();
            Console.WriteLine(requestData);

            // 解析 JSON
            var jsonData = JsonSerializer.Deserialize<DataModel>(requestData);
            if(jsonData is null) return;

            Console.WriteLine(jsonData.Type);
            // 操作
            if(jsonData.Type == "request" && jsonData.Args is not null){
                jsonData.Result = "Unknown method.";
                if(jsonData.Args.Count() == 0){
                    jsonData.Result = "Lack of args.";
                }
                else if(jsonData.Name == "fib"){
                    try{
                        int n = Convert.ToInt32(jsonData.Args[0]);
                        jsonData.Result = Service.Fib(n).ToString();
                    }catch(Exception){
                        jsonData.Result = "Server handle Failed.";
                    }
                }
                else if(jsonData.Name == "sort"){
                    try{
                        int[] intArray = new int[jsonData.Args.Length];
                        for (int i = 0; i < jsonData.Args.Length; i++){
                            intArray[i] = int.Parse(jsonData.Args[i]);
                        }
                        Service.Sort(intArray);
                        StringBuilder builder = new ();
                        for (int i = 0; i < intArray.Length; i++){
                            if(i != 0) builder.Append(", ");
                            builder.Append(intArray[i].ToString());
                        }
                        jsonData.Result = buffer.ToString();
                    }catch(Exception){
                        jsonData.Result = "Server handle Failed.";
                    }
                }

                byte[] jsonChars = JsonSerializer.SerializeToUtf8Bytes(jsonData);
                stream.Write(jsonChars, 0 ,jsonChars.Length);
            }
        }
        catch (Exception)
        {
            Console.WriteLine($"Error");
        }
    }

}

