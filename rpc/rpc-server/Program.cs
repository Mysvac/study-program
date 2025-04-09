using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;

try
{
    // 1. 创建 Socket
    using Socket client = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
    // 2. 连接远程服务
    client.Connect(new IPEndPoint(IPAddress.Parse("10.100.164.32"), 18083));

    DataModel dataModel = new (){
        Type = "register",
        Args = new string[] { "10.100.164.33", "18084" },
        Name = "URL-10.100.164.33:18084"
    };

    // 3. 发送数据（UTF-8编码）
    byte[] data = JsonSerializer.SerializeToUtf8Bytes(dataModel);
    client.Send(data);

    // 3. 接收响应数据
    byte[] buffer = new byte[1024];
    int received = client.Receive(buffer);
    string response = Encoding.UTF8.GetString(buffer, 0, received);
    Console.WriteLine(response);
}
catch (Exception)
{
    Console.WriteLine("register failed");
}


var server = new SocketServer(18084);
server.Start();
