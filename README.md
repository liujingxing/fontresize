FontResize是一个仿微信/支付宝 字体大小 调整控件的库

**gradle依赖**
```java
implementation 'com.ljx.view:fontresize:1.0.1'
```
该库里面只有一个类`FontResizeView`，故不用担心库很大。

**效果图**

![image](https://github.com/liujingxing/FontReszie/blob/master/screenRecord/screen.gif)

### 属性说明

```java
<declare-styleable name="FontResizeView">
    <attr name="minSize" format="reference|dimension" /> <!--最小size-->
    <attr name="maxSize" format="reference|dimension" /> <!--最大size-->
    <attr name="totalGrade" format="integer" />   <!--可调整的等级、即总的等级-->
    <attr name="standardGrade" format="integer" />  <!--标准等级-->

    <attr name="leftText" format="reference|string" />  <!--左边文本，默认为 'A'-->
    <attr name="middleText" format="reference|string" />  <!--中间文本，默认为 '标准'-->
    <attr name="rightText" format="reference|string" />  <!--右边文本，默认为 'A'-->
    <attr name="leftTextColor" format="reference|color" />  <!--左边文本颜色，默认为全黑-->
    <attr name="middleTextColor" format="reference|color" />  <!--中间文本颜色，默认为全黑-->
    <attr name="rightTextColor" format="reference|color" />  <!--右边文本颜色，默认为全黑-->

    <attr name="sliderRadius" format="reference|dimension" />  <!--滑块半径-->
    <attr name="sliderColor" format="reference|color" />  <!--滑块颜色-->
    <attr name="sliderShadowColor" format="reference|color" />  <!--滑块边缘阴影颜色-->

    <attr name="lineColor" format="reference|color" />  <!--线条颜色-->
    <attr name="lineStrokeWidth" format="reference|dimension" />  <!--线条宽度，即粗细程度-->
    <attr name="horizontalLineLength" format="reference|dimension" />  <!--横向线条长度-->
    <attr name="verticalLineLength" format="reference|dimension" />  <!--纵向线条长度-->

</declare-styleable>
```
### xml配置
```java
<com.ljx.view.FontResizeView
    android:id="@+id/font_resize_view"
    android:layout_width="match_parent"
    android:layout_height="140dp"
    android:background="@android:color/white"
    app:maxSize="25sp"
    app:minSize="13sp"
    app:sliderColor="@android:color/white"
    app:sliderRadius="12dp"
    app:standardGrade="2"
    app:totalGrade="7" />
```

### 设置回调
```java
FontResizeView fontResizeView = findViewById(R.id.font_resize_view);
fontResizeView.setOnFontChangeListener(new OnFontChangeListener() {
    @Override
    public void onFontChange(float fontSize) {
        //字体size改变回调  单位:sp
    }
});
```

这样配置过后，代码跑起来效果就跟上面的 gif 动图一样

我们把所有属性都加上，看看效果

```java
<com.ljx.view.FontResizeView
    android:id="@+id/font_resize_view"
    android:layout_width="match_parent"
    android:layout_height="140dp"
    android:background="@android:color/white"
    app:horizontalLineLength="300dp"
    app:leftText="AA"
    app:leftTextColor="#FF0000"
    app:lineColor="#009999"
    app:lineStrokeWidth="2dp"
    app:maxSize="31sp"
    app:minSize="15sp"
    app:middleText="标准1"
    app:middleTextColor="#00FF00"
    app:rightText="AAA"
    app:rightTextColor="#0000ff"
    app:sliderColor="@android:color/white"
    app:sliderRadius="13dp"
    app:sliderShadowColor="#FF0000"
    app:standardGrade="3"
    app:totalGrade="9"
    app:verticalLineLength="15dp" />
```
效果如下

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190512142658512.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWppbmd4aW5nOTM=,size_16,color_FFFFFF,t_70)

相信以上属性已经能满足大部分场景，如果不能满足您的场景，请提出您的需求，我会第一时间回应您。
