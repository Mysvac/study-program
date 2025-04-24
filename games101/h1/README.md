## 步骤

**完成提高题**

### 0. vcpkg全局安装eigen3和opencv
**如果已经安装，请省略**
```shell
vcpkg install eigen3
vcpkg install opencv
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
cd ./build/Debug/
./Rasterizer.exe
```
```shell
# linux
cd ./build/Debug/
./Rasterizer
```

