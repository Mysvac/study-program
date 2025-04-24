## 编译步骤

### 0. vcpkg全局安装 eigen3
**如果已经安装，请省略**
```shell
vcpkg install eigen3
```

### 1. CMake 配置
```shell
cmake -B build
```

### 2. CMake 构建
```shell
cmake --build build
```

### 3. 运行程序
```powershell
# windows
.\build\Debug\Transformattion.exe
```
```shell
# linux
./build/Debug/Transformattion
```
