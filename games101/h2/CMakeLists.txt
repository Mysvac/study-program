cmake_minimum_required(VERSION 3.24)

# 使用 vcpkg 管理依赖
# 注意，这里的设置必须在project()前面
file(TO_CMAKE_PATH "$ENV{VCPKG_ROOT}" NORMALIZED_VCPKG_ROOT)
set(CMAKE_TOOLCHAIN_FILE ${NORMALIZED_VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake)

project(Rasterizer)

set(CMAKE_CXX_STANDARD 17)


find_package(Eigen3 CONFIG REQUIRED)
find_package(OpenCV REQUIRED)



add_executable(Rasterizer main.cpp rasterizer.cpp Triangle.cpp)

target_include_directories(Rasterizer PRIVATE ${PROJECT_SOURCE_DIR})
target_link_libraries(Rasterizer PRIVATE Eigen3::Eigen)
target_include_directories(Rasterizer PRIVATE ${OpenCV_INCLUDE_DIRS})
target_link_libraries(Rasterizer PRIVATE ${OpenCV_LIBS})
