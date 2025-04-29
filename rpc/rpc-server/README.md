## 项目运行
注意，dotnet版本至少7.0
```sh
dotnet run
```

## 项目编译（可选）
```sh
# 生成windows下的单个可执行文件
dotnet publish -c Release -r win-x64 --self-contained true /p:PublishSingleFile=true

# 生成Linux 64位下的可执行文件
dotnet publish -c Release -r linux-x64 --self-contained true /p:PublishSingleFile=true

# 生成macOS ARM64下的可执行文件
dotnet publish -c Release -r osx-arm64 --self-contained true /p:PublishSingleFile=true
```
输出在 `./bin/版本/publish/`下，可以直接执行`/publish`下的可执行文件，其他文件没用，可删除。
