cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_C_STANDARD 99)
add_library( yylRecord
             SHARED
            src/main/cpp/yyl_record.c
            src/main/cpp/yyl_log.c
         #   src/main/cpp/yyl_fixsurface.c
             )

add_library( vlcjni
            SHARED
            IMPORTED
            )
#if(${ANDROID_ABI} STREQUAL "armeabi")
message("yyl abi=${ANDROID_ABI}")

set(soPath ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libvlcjni.so)

message("yyl abi=${soPath}")

set_target_properties(
            vlcjni
            PROPERTIES IMPORTED_LOCATION
            #${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libvlcjni.so
            ${soPath}
             )

include_directories(
   ${CMAKE_SOURCE_DIR}/include/
   )

target_link_libraries( yylRecord
                       vlcjni
                       android
                       log )