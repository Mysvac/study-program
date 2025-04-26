cmake_minimum_required(VERSION 3.10)
project(RayTracing)

set(CMAKE_CXX_STANDARD 17)

add_executable(RayTracing main.cpp Vector.cpp Scene.cpp BVH.cpp Renderer.cpp)

target_include_directories(RayTracing PRIVATE ${CMAKE_SOURCE_DIR})

