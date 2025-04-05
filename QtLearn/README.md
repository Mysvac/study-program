# QT C++ 个人学习项目

开始于 2025/02/21<br>
编写者： **迷枵-Mysvac**

## 项目基础说明

### 配置：

- C++标准：17
- QT版本：6.8
- 构建工具：CMake 3.29+
- 依赖管理：vcpkg
- 开发模式：QtQuick

### 其他说明：

项目文件仅给出了`CMakePresets.json`，内部不含QT和vcpkg配置，需自行添加。<br>
推荐使用`CMakeUserPresets.json`。

#### Windows下的CMake预设模版

**重点参数：**

- **VCINSTALLDIR** : MSVC工具链所在的路径。
- **CMAKE_PREFIX_PATH** : Qt库所在的位置。
- **COPY_QT_DLL** : 是否复制所需的QT动态库到目标文件夹。<br>
（QtCreator调试时无需复制。VSCode或最终发布时需要复制。）

**可选参数：**

- **QT_QML_GENERATE_QMLLS_INI** : 是否生成QML智能感知辅助文件，可选。
- **CMAKE_MAKE_PROGRAM** : Ninja路径，可选，找不到则需要显式指定。
- **CMAKE_TOOLCHAIN_FILE** : 包管理器vcpkg路径，可选。
- **toolset** : 指定MSVC工具链版本，默认v143，可选。
- **CMAKE_CXX_COMPILER** : 强制指定MSVC编译器，防止找错，可选。

```json
{
    "version": 5,
    "configurePresets": [
        {
            "name": "Ninja-config",
            "displayName": "MSVC Ninja Multi-config",
            "description": "Not Copy Qt DLL files",
            "inherits": "x64-base-config",
            "cacheVariables": {
                "CMAKE_CONFIGURATION_TYPES": "Debug;Release",
                "CMAKE_CXX_COMPILER": "cl.exe",
                "QT_QML_GENERATE_QMLLS_INI": "ON",
                "COPY_QT_DLL": "OFF",
                "CMAKE_PREFIX_PATH": "E:/Qt/6.8.2/msvc2022_64"
            },
            "environment": {
                "VCINSTALLDIR": "E:/VisualStudio/VC"
            }
        },
        {
            "name": "Ninja-config-DLL",
            "displayName": "MSVC Ninja Multi-config Dll",
            "description": "Copy Qt DLL files after Building",
            "inherits": "Ninja-config",
            "cacheVariables": {
                "CMAKE_CONFIGURATION_TYPES": "Debug;Release",
                "CMAKE_CXX_COMPILER": "cl.exe",
                "QT_QML_GENERATE_QMLLS_INI": "ON",
                "COPY_QT_DLL": "ON",
                "CMAKE_PREFIX_PATH": "E:/Qt/6.8.2/msvc2022_64"
            }
        }
    ],
    "buildPresets": [
        {
            "name": "build-debug",
            "displayName": "Build Debug",
            "configurePreset": "Ninja-config",
            "configuration": "Debug"
        },
        {
            "name": "build-release",
            "displayName": "Build Release",
            "configurePreset": "Ninja-config",
            "configuration": "Release"
        },
        {
            "name": "build-debug-dll",
            "displayName": "Build Debug dll",
            "configurePreset": "Ninja-config-DLL",
            "configuration": "Debug"
        },
        {
            "name": "build-release-dll",
            "displayName": "Build Release dll",
            "configurePreset": "Ninja-config-DLL",
            "configuration": "Release"
        }
    ]
}
```

## 分支：Learn-1

1. VSCode中，使用CMake构建并运行了简单的QT程序。（2025/02/21）
2. 学习了qmldir的使用，规范项目结构与代码。
3. 添加README.md文件，并上传github。
4. 修改了CMake配置以及预设。可用Qt Creator修改/调试。
5. 调整预设，可用CLion或Visual Studio修改/调试项目。
6. 尝试Qt VS Tool插件，强制增加了CMake预设的部分内容。
