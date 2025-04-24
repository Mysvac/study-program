cmake_minimum_required (VERSION 3.24)

# 使用 vcpkg 管理依赖
# 注意，这里的设置必须在project()前面
file(TO_CMAKE_PATH "$ENV{VCPKG_ROOT}" NORMALIZED_VCPKG_ROOT)
set(CMAKE_TOOLCHAIN_FILE ${NORMALIZED_VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake)

project (Transformation)

find_package(Eigen3 CONFIG REQUIRED)

add_executable (Transformation main.cpp)

target_link_libraries(Transformation PRIVATE Eigen3::Eigen)

