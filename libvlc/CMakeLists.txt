cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_C_STANDARD 99)
add_library( yylRecord
             SHARED
            src/main/cpp/yyl_record.c
            src/main/cpp/yyl_log.c
         #   src/main/cpp/yyl_fixsurface.c
             )

add_library( vlc
            SHARED
            IMPORTED
            )
add_library( vlcjni
            SHARED
            IMPORTED
            )
#if(${ANDROID_ABI} STREQUAL "armeabi")
message("yyl abi=${ANDROID_ABI}")

set(soPath ${CMAKE_SOURCE_DIR}_/jniLibs/${ANDROID_ABI}/libvlc.so)
set(soPathjni ${CMAKE_SOURCE_DIR}_/jniLibs/${ANDROID_ABI}/libvlcjni.so)

message(" vlclib=${soPath}")
message(" jnilib=${soPathjni}")
#message("PROJECT_SOURCE_DIR=${PROJECT_SOURCE_DIR} CMAKE_SOURCE_DIR=${CMAKE_SOURCE_DIR} CMAKE_CURRENT_LIST_DIR=${CMAKE_CURRENT_LIST_DIR}  ")

set_target_properties(
            vlc
            PROPERTIES IMPORTED_LOCATION
            #${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libvlcjni.so
            ${soPath}
             )
set_target_properties(
            vlcjni
            PROPERTIES IMPORTED_LOCATION
            #${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libvlcjni.so
            ${soPathjni}
             )

include_directories(
   ${CMAKE_SOURCE_DIR}/include/
   )

target_link_libraries( yylRecord
                       vlc
                       vlcjni
                       android
                       log )