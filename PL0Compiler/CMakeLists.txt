cmake_minimum_required(VERSION 4.0.0)

# 设置模块支持试验性代码 
# 可以做在https://github.com/Kitware/CMake/blob/master/Help/dev/experimental.rst查看最新代码
set(CMAKE_EXPERIMENTAL_CXX_IMPORT_STD "a9e1cf81-9932-4810-974b-6eccaf14e457")

project(module-test LANGUAGES CXX)

# 设置C++标准
set(CMAKE_CXX_MODULE_STD ON)
set(CMAKE_CXX_STANDARD 23)
# 启用全局模块支持
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 生产编译数据库，辅助vscode智能感知。
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)


FILE(GLOB src_files LIST_DIRECTORIES FALSE src/*.cpp)
FILE(GLOB module_files LIST_DIRECTORIES FALSE modules/*.ixx)

add_executable(${PROJECT_NAME} src/main.cpp)
target_sources(${PROJECT_NAME}
PRIVATE
    FILE_SET cxx_modules TYPE CXX_MODULES FILES ${module_files}
)


