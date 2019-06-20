cmake_minimum_required(VERSION 3.13)
project(cpprenderer)
set(CMAKE_CXX_STANDARD 14)            # Enable c++14 standard

# Add main.cpp file of project root directory as source file
set(SOURCE_FILES main.cpp)
include_directories("/usr/local/Cellar/sdl2/2.0.9_1/include")
link_directories("/usr/local/Cellar/sdl2/2.0.9_1/lib")

# Add executable target with source files listed in SOURCE_FILES variable
add_executable(cpprenderer ${SOURCE_FILES})
target_link_libraries(cpprenderer SDL2)