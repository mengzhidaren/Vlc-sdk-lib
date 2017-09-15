# Vlc-sdk-lib
如果你想构建切换大小屏播放器 demo参考，
  * [VideoHeaderView]https://github.com/mengzhidaren/RecylerViewMultiHeaderView
---

# 实现的功能
```
1,能支持MP4,FLV,AVI,TS,3GP,RMVB,WM,WMV等格式还有网络流 http,rtsp,rtmp,mms,m3u8.（有不支持的请留言）
2,软硬解切换.支持ffmpeg指令  < transform:rotation=90>
3,当前缓冲百分比    <MediaPlayer.Event.Buffering>
4,视频(音频)播放速度可调,任意速度可调. (0.25-4)   < player.setRate(float rate); >
5,加载字幕，设置镜面<>
```
# 使用方法
```
 <org.videolan.vlc.VlcVideoView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

VlcVideoPlayer   player = new VlcVideoPlayer(context);
                 player.setMediaListenerEvent(new MediaListenerEvent());
                 player.startPlay(path);
                 
                 
 thumbnail        
 截图方法   byte[] b = VLCUtil.getThumbnail(media, width, height);
           if (b != null) {
                 Bitmap thumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                 thumbnail.copyPixelsFromBuffer(ByteBuffer.wrap(b));
           }
```

#引用库文件
```
   dependencies {
     // jCenter
    compile 'com.yyl.vlc:vlc-android-sdk:1.3.0'
   }
   
   
   可选 精简的库
    ndk {
               //支持的abi   , //'x86_64'//, 'arm64-v8a'
               abiFilters 'armeabi-v7a'//,'x86_64','arm64-v8a','x86'
  }
```

目前支持的库： x86_64     x86    armeabi-v7a    arm64-v8a    mips    mips64
*目前的功能：几乎所有格式文件的播放(已知的.mov不支持)  具体请看官方更新日志

##Thanks
* [vlc](https://www.videolan.org)

###V1.0.1
1.提交第一版
###V1.2.0
1更新播放控件
发现bug或有好的建议欢迎[issue]
###V1.3.0
增加 mips   mips64  指令集的支持
# DEMO效果预览
![image](https://github.com/mengzhidaren/VlcPlayer/blob/master/GIF/j1.gif)

# 编译方法
参考：https://wiki.videolan.org/AndroidCompile/
```
## sudo apt-get install automake ant autopoint cmake build-essential libtool \
     patch pkg-config protobuf-compiler ragel subversion unzip git
```
版本号vlc-android-lib 3.0.0-2.1.0
 在这吐槽下 我在github上找 vlc-android的代码几 乎所有源码都是3年前或者两年前的 库文件
此次版本是最新版的 vlc-android 3.0.0- v2.1.0
```
vlc-android的代码在
linux  ubuntu64  16.4  中搭建编绎环境
android-sdk 版本 api25
ndk版本 r13b
java 版本 8
vlc-android 版本 3.0.0-v2.1.0版本
```
1.我在win10中安装的  VMware Workstation Pro
安装ubuntu 64  16 的最新版
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/1.png)

2.安装  linux 版的  jdk   sdk 最新版就行
3.  ubuntu 64 环境设置
```
sudo gedit /etc/profile
$source /etc/profile

export NDK=/opt/sdk/android-sdk-linux/ndk-bundle
export ANDROID_NDK=/opt/sdk/android-sdk-linux/ndk-bundle
export PATH=${ANDROID_NDK}:$PATH
export ANDROID_SDK=/opt/sdk/android-sdk-linux
export PATH=$ANDROID_SDK/tools:$ANDROID_SDK/platform-tools:$PATH

export JAVA_HOME=/opt/sdk/jdk1.8.0_101
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:${JAVA_HOME}/lib/tools.jar
export PATH=${JAVA_HOME}/bin:$PATH

export ANDROID_ABI=armeabi-v7a   //对应的cpu平台 .so包
```

![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/3.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/4.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/8.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/9.png)
