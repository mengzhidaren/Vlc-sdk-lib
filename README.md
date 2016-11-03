# Vlc-sdk-lib
如果你想构建列表播放器
 请到我的另一个项目有更多列表demo参考  我会长期更新这个列表demo的(ps:公司不忙的话)
  * [VlcPlayer] https://github.com/mengzhidaren/VlcPlayer

#使用方法
```
 <org.videolan.vlc.VlcVideoView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
或者直接新建播放器
```
  VlcVideoPlayer   player = new VlcVideoPlayer(context);
                   player.setMediaListenerEvent(new MediaListenerEvent());
                   player.startPlay(path);

```

##Points
#引用库文件
```
   dependencies {
     // jCenter
    compile 'com.yyl.vlc:vlc-android-sdk:1.2.0'
   }
```
版本号vlc-android-lib 3.0.0-2.1.0
 在这吐槽下 我在github上找 vlc-android的代码几 乎所有源码都是3年前或者两年前的 库文件
此次版本是最新版的 vlc-android 3.0.0- v2.1.0
```
vlc-android的代码在
linux  ubuntu64  16.4  中搭建编绎环境
android-sdk 版本 api24
ndk版本 r12
java 版本 8
vlc-android 版本 3.0.0-v2.1.0版本
```
目前支持的库： x86_64   x86  armeabi-v7a  arm64-v8a

*目前的功能：几乎所有格式文件的播放 buffing进度支持 多线程死锁的bug也修好了

**[个人评价 比 ijk的播放器要好很多 应该是本人目前所知道的开源播放器 中最好的了]

1.我在win10中安装的  VMware Workstation Pro  可以自已百度下载
安装ubuntu 64  16 的最新版
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/1.png)

2.安装  linux 版的  jdk   sdk 最新版就行
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/2.png)
3.  ubuntu 64 环境设置
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/3.png)

全局用户环境设置
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


4.安装工具  https://wiki.videolan.org/AndroidCompile/
```
## sudo apt-get install automake ant autopoint cmake build-essential libtool \
     patch pkg-config protobuf-compiler ragel subversion unzip git
```
还是最新版就行
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/4.png)

5. 编译文件 （可能会出现各种问题  一个一个google解吧）
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/5.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/6.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/7.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/8.png)
![](https://raw.githubusercontent.com/mengzhidaren/Vlc-sdk-lib/master/screenshots/9.png)
MAC电脑没试过 本来就是linux 应该会简单一点


##Thanks
* [vlc](https://www.videolan.org)


###V1.0.1
1.提交第一版
###V1.2.0
1更新播放控件

发现bug或有好的建议欢迎[issue]

我后期会时间会优化下库大小  和其它bug

目前已知bug：  还没有   就是库文件有点大

可以用我编译好的仓库   不定期更新

