cmake_minimum_required(VERSION 3.28)

project(FirstTriangle)

set(CMAKE_CXX_STANDARD 20)

find_package(Vulkan REQUIRED)

find_package(glfw3 CONFIG REQUIRED)
find_package(glm CONFIG REQUIRED)

FILE(GLOB SRC_FILES src/*.cpp)

add_executable(${PROJECT_NAME} ${SRC_FILES})

target_link_libraries(${PROJECT_NAME} PRIVATE Vulkan::Vulkan Vulkan::Headers)
target_link_libraries(${PROJECT_NAME} PRIVATE glm::glm-header-only)
target_link_libraries(${PROJECT_NAME} PRIVATE glfw)
