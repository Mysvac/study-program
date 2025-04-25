cmake_minimum_required(VERSION 3.24)

# 使用 vcpkg 管理依赖
# 注意，这里的设置必须在project()前面
file(TO_CMAKE_PATH "$ENV{VCPKG_ROOT}" NORMALIZED_VCPKG_ROOT)
set(CMAKE_TOOLCHAIN_FILE ${NORMALIZED_VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake)

project(BezierCurve)

find_package(OpenCV REQUIRED)

set(CMAKE_CXX_STANDARD 17)

add_executable(BezierCurve main.cpp)

target_include_directories(BezierCurve PRIVATE ${OpenCV_INCLUDE_DIRS})
target_link_libraries(BezierCurve PRIVATE ${OpenCV_LIBS})

