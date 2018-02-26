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
```
# 使用方法
```
<xml>
//高度需要自已设置调整
 <org.videolan.vlc.VlcVideoView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
<java>
VlcVideoPlayer   player = new VlcVideoPlayer(context);
                 player.setMediaListenerEvent(new MediaListenerEvent());
                 player.startPlay(path);

<其它>
 thumbnail 
 如果你有截图需求最好用我写的ffmpeg截图       
 截图方法   byte[] b = VLCUtil.getThumbnail(media, width, height);
           if (b != null) {
                 Bitmap thumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                 thumbnail.copyPixelsFromBuffer(ByteBuffer.wrap(b));
           }
  字幕功能 请用这个方法 或者参考demo
  mMediaPlayer.addSlave(Media.Slave.Type.Subtitle, "字幕文件地址", true);
          
```

#引用库文件
```
   dependencies {
   //3.0.2有点小问题 等下个版本修改下  先回退到2.5.15
   compile 'com.yyl.vlc:vlc-android-sdk:2.5.15'
   
   }
    ndk {
        //支持的abi    可选 精简的库
        abiFilters 'armeabi-v7a'//,'x86_64','arm64-v8a','x86'
    }
         
  目前支持的库 ： x86_64     x86    armeabi-v7a    arm64-v8a   

```

## Donate ##
Alipay:<br>
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/yyl.png)
<br>
谢谢支持我会和官方同步更新<br>


### 官方网站 ###
* [vlc](https://www.videolan.org)

### 编译方法 ###

```
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
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/4.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/8.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/9.png)


### DEMO效果预览
![image](https://github.com/mengzhidaren/VlcPlayer/blob/master/GIF/j1.gif)
