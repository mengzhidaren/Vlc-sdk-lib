### Vlc-sdk-lib

构建切换大小屏播放器  推荐<br>

  * [VideoHeaderView]https://github.com/mengzhidaren/RecylerViewMultiHeaderView <br>

转码视频的命令行工具  推荐<br>

  * [FFmpegCmdSdk]https://github.com/mengzhidaren/FFmpegCmdSdk <br>
  
在RecyclerView中播放器的实现  参考<br>
  * [VlcPlayer]https://github.com/mengzhidaren/VlcPlayer <br>

---

# 实现的功能
```
能支持大部分主流格式
软硬解切换.支持vlc指令  < transform:rotation=90>
当前缓冲百分比 
视频(音频)播放速度可调,任意速度可调. (0.25-4)   < player.setRate(float rate); >
加载字幕(addSlave)，设置镜面等
实时录制视频(测试中.. 有问题请留言)

编译问题请不要找我留言(请自已google)
```
# 使用方法
```
<xml>布局
//最好继承VlcVideoView    重写adjustAspectRatio()方法   高度自已设置调整
 <org.videolan.vlc.VlcVideoView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
<java>
VlcVideoPlayer   player = new VlcVideoPlayer(context);
                 player.setMediaListenerEvent(new MediaListenerEvent());
                 player.startPlay(path);

<其它>
 截封面图方法     byte[] b = VLCUtil.getThumbnail(media, width, height);
 运行时截图用     TextureView.getBitmap()保存图片
 
 vlc原生截图      new RecordEvent().takeSnapshot(mediaPlayer,"保存图片的地址或目录",width,height);
  
  字幕功能  mMediaPlayer.addSlave(Media.Slave.Type.Subtitle, "字幕文件地址", true);
  
  录像功能(测试中   参考demo)
        new RecordEvent().startRecord(mediaPlayer, "保存视频的目录","文件名");
          
```

#引用库文件
```
   dependencies {
        compile 'com.yyl.vlc:vlc-android-sdk:3.0.13'
   }
   
    ndk {
        //支持的abi    可选 精简的库
        abiFilters 'armeabi-v7a'//,'x86_64','arm64-v8a','x86'
    }
         

```

## Donate ##
Alipay:<br>
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/yyl.png)
<br>
谢谢支持我会和官方同步更新<br>

# VLC for Android
This is the official Android port of VLC.

## License
VLC for Android is licensed under GPLv3

## Build

You will need a recent Linux distribution to build VLC.
It should work with Windows 10, but no official support for this.

Check our [AndroidCompile wiki page](https://wiki.videolan.org/AndroidCompile/)

## Contribute

VLC is a libre and open source project, we welcome all contributions.

### Pull requests

Pull requests can be proposed on [github mirror](https://github.com/videolan/vlc-android) as code.videolan.org is reserved to VideoLAN members

### 3.0以下编译方法 不支持3.0.0以上编译

```
(3.0.0以上编译忽略吧  其它自已google吧 编译问题不要留言给我 )

vlc-android的代码在  linux  ubuntu64  16.4  中搭建编绎环境
android-sdk 版本 api25
ndk版本 r13b
java 版本 8
vlc-android 版本 3.0.0-v2.1.0版本
```

1.在win10中安装的  VMware Workstation Pro
安装ubuntu 64  16 的最新版
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/1.png)

2.安装  linux 版的  jdk   sdk 最新版

```
安装包管理工具和开源库等
参考：https://wiki.videolan.org/AndroidCompile/

## sudo apt-get install automake ant autopoint cmake build-essential libtool \
     patch pkg-config protobuf-compiler ragel subversion unzip git
```
3.  ubuntu 64 vlc-android环境设置
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
#export ANDROID_ABI=x86   //编译对应的平台
```

![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/3.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/9.png)


### DEMO效果预览
![image](https://github.com/mengzhidaren/VlcPlayer/blob/master/GIF/j1.gif)
