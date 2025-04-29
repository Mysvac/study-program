using System;
using System.Linq;
using System.Reflection;
using System.Text;

public static class CommandHandler
{
    private static readonly Dictionary<string, MethodInfo> _commands;

    static CommandHandler()
    {
        _commands = new Dictionary<string, MethodInfo>();

        // 扫描所有带有 [Command] 的方法
        var methods = typeof(Service)
            .GetMethods(BindingFlags.Public | BindingFlags.Static)
            .Where(m => m.GetCustomAttribute<CommandAttribute>() != null);

        foreach (var method in methods)
        {
            var attr = method.GetCustomAttribute<CommandAttribute>();
            if(attr is null || attr.Name is null) continue;
            _commands[attr.Name] = method;
        }
    }

    public static string ExecuteCommand(string commandName, string[] args)
    {
        if (!_commands.TryGetValue(commandName, out var method))
        {
            return "Command not found.";
        }

        try
        {
            // 转换参数类型
            var parameters = method.GetParameters();
            object[] convertedArgs = new object[1];


            if(parameters[0].ParameterType == typeof(int)){
                convertedArgs[0] = int.Parse(args[0]);
            }
            else if(parameters[0].ParameterType == typeof(int[])){
                int[] nums = new int[args.Length];
                for(int i=0; i < args.Length; ++i){
                    nums[i] = int.Parse(args[i]);
                }
                convertedArgs[0] = nums;
            }


            // 调用方法
            var result = method.Invoke(null, convertedArgs);

            // 处理返回结果
            return result?.ToString() ?? "OK";
            
        }
        catch (Exception)
        {
            return "Server handle failed.";
        }
    }
}
