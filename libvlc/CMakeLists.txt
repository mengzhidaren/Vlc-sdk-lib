cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_C_STANDARD 99)
add_library(yylRecord
        SHARED
        src/main/cpp/yyl_record.c
        src/main/cpp/yyl_log.c
        #   src/main/cpp/yyl_fixsurface.c
        )


add_library(vlc
        SHARED
        IMPORTED
        )
add_library(vlcjni
        SHARED
        IMPORTED
        )
message(AUTHOR_WARNING 'yyl CMAKE_ANDROID_ARCH_ABI=${CMAKE_ANDROID_ARCH_ABI})
message(AUTHOR_WARNING 'yyl CMAKE_SOURCE_DIR=${CMAKE_SOURCE_DIR})

set(soPath ${CMAKE_SOURCE_DIR}/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libvlc.so)
set(soPathjni ${CMAKE_SOURCE_DIR}/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libvlcjni.so)


#message(AUTHOR_WARNING " jnilib=${soPathjni}")
#
set_target_properties(
        vlc
        PROPERTIES IMPORTED_LOCATION
        ${soPath}
)
set_target_properties(
        vlcjni
        PROPERTIES IMPORTED_LOCATION
        ${soPathjni}
)

include_directories(
        ${CMAKE_SOURCE_DIR}/include/
)


target_link_libraries(yylRecord
        android
        log
        vlc
        vlcjni
        )
