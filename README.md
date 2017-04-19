# CustomView03
这个工程是学习鸿洋大神自定义View之后自己实现的一个工程，修改了原工程中一些瑕疵。在此再次谢过鸿洋大神。
<br>原文链接如下：
<br>http://blog.csdn.net/lmj623565791/article/details/24500107

主要修改如下：
<br>1、onDraw（）中new 对象；
<br>2、颜色交替方式优化；
<br>3、绘制线程优化，离开显示页面停止线程，避免内存泄漏；
<br>4、get自定义属性方式优化，避免取到默认值为空；
<br>5、重新onMeasure方法，增强健壮性；
<br>6、优化speed表达方式；

ProgressBar2文件是为了测试onMeasure（）而写的对比view。
