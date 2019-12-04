# UHF_NEW_UI
新版R2000库使用，读写盘点数据通过回调返回

##  导入依赖库
**AndroidStudio** build.gradle中的dependencies中添加

```
//最外层build.gradle
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
```
 dependencies {
    implementation 'com.github.SpeedataG:UHF:8.1.3'
    //模块上电引用
    implementation 'com.github.SpeedataG:Device:1.6.8'
  }
```
## 高温禁用说明
* 模块温度高于75℃时禁用超高频

  
## API文档

	详细的接口说明在showdoc，地址：http://www.showdoc.cc/web/#/79868361520440?page_id=452063154391852

北京思必拓科技股份有限公司

网址 http://www.speedata.cn/

技术支持 电话：155 4266 8023

QQ：2480737278
