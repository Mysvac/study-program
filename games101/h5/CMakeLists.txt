cmake_minimum_required(VERSION 3.24)

project(RayTracing)

set(CMAKE_CXX_STANDARD 17)

add_executable(RayTracing main.cpp Scene.cpp Renderer.cpp)

target_include_directories(RayTracing PRIVATE ${CMAKE_SOURCE_DIR})
# target_compile_options(RayTracing PUBLIC -Wall -Wextra -pedantic -Wshadow -Wreturn-type -fsanitize=undefined)
target_compile_features(RayTracing PUBLIC cxx_std_17)
target_link_libraries(RayTracing PUBLIC -fsanitize=undefined)

