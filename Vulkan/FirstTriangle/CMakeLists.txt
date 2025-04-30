cmake_minimum_required(VERSION 3.28)

project(FirstTriangle)

set(CMAKE_CXX_STANDARD 20)
# CMake默认行为，通过环境变量VULKAN_SDK查找
find_package(Vulkan REQUIRED)
# 通过vcpkg导入
find_package(glfw3 CONFIG REQUIRED)
find_package(glm CONFIG REQUIRED)

file(GLOB SRC_FILES src/*.cpp)

add_executable(${PROJECT_NAME} ${SRC_FILES})

target_link_libraries(${PROJECT_NAME} PRIVATE Vulkan::Vulkan)
target_link_libraries(${PROJECT_NAME} PRIVATE glm::glm-header-only)
target_link_libraries(${PROJECT_NAME} PRIVATE glfw)

add_subdirectory(shaders) # 假设子目录叫 shaders
