cmake_minimum_required(VERSION 3.29)

project(QTLearn VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 这个必须开，不然图片不会存入
set(CMAKE_AUTORCC ON)
# AUTOMOC 和 AUTOUIC 会被qt_standard_project_setup函数自动开启
# set(CMAKE_AUTOMOC ON)
# set(CMAKE_AUTOUIC ON)

# 注意Quick 依赖Core Gui Qml，所以前三者可以不显式导入
find_package(Qt6 REQUIRED COMPONENTS Quick QuickControls2)

qt_standard_project_setup(REQUIRES 6.8)

# https://doc.qt.io/qt-6/qt-cmake-policy-qtp0004.html
# qt_policy(QTP0004 NEW)

# 添加文件
set(MY_SOURCE_FILES src/main.cpp)
qt_add_resources(MY_SOURCE_FILES res/ico.qrc)

qt_add_executable(${PROJECT_NAME} ${MY_SOURCE_FILES})

qt_add_qml_module(${PROJECT_NAME}
    URI MainModule
    VERSION 1.0
    QML_FILES
        MainModule/Main.qml
    RESOURCES
        res/images.qrc
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(${PROJECT_NAME} PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.appQtLearn-02
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

target_include_directories(${PROJECT_NAME} 
PRIVATE 
    "${CMAKE_SOURCE_DIR}/include"
)

target_link_libraries(${PROJECT_NAME} 
PRIVATE 
    Qt6::Quick
    Qt6::QuickControls2
)

include(GNUInstallDirs)
install(TARGETS ${PROJECT_NAME}
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

# 控制 复制需要的QT动态库文件到目标文件夹
if(COPY_QT_DLL STREQUAL "ON")
    # 获取windeployqt6_exe工具路径
    get_target_property(qt_qmake_path Qt6::qmake IMPORTED_LOCATION)
    get_filename_component(qt_bin_dir "${qt_qmake_path}" DIRECTORY)
    find_program(windeployqt6_exe NAMES windeployqt6 HINTS "${qt_bin_dir}")

    # 解析qml和exe文件，并复制需要的动态库过去
    # 注意，必须要指定--qmldir参数，给出.qml文件所在的路径让windeployqt6_exe解析
    # 否则会出现“XXX模块未安装" 的报错
    add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
        COMMAND "${windeployqt6_exe}"
        --qmldir "${CMAKE_SOURCE_DIR}/MainModule"
        "$<TARGET_FILE:${PROJECT_NAME}>"
        COMMENT "Copying Qt dynamic(shared) libs..."
        VERBATIM
    )
endif()
