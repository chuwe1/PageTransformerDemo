# ViewPager PageTransformer 探索
How to custom ViewPager's PageTransformer

很多人对于ViewPager PageTransformer一直都是停留在用的阶段，都是把别的写好的能够实现一些效果的PageTransformer直接拿过来给set上，可是如果有一天你的产品经理突然给你来了一个你从未见过的切换效果要你实现。于是各种百度、Google，无奈并没有什么卵用，根本搜不到这种效果。这时候怎么办？

**当然是朝这里看过来了**

废话不多说，我们先看一个效果图
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/1.gif)
这种切换效果相信大家都在很多地方见过，没错这就是[Google的官方示例_DepthPageTransformer](https://developer.android.google.cn/training/animation/screen-slide.html)，其实现代码如下：
```
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            page.setAlpha(1 - position);

            // Counteract the default slide transition
            page.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }
}
```
可以看出来，这里当position位于（-∞，-1）和（1，∞）这两个区间的的时候，给page的透明度设为0，在[-1，1]这个区间根据一些相关极限对page进行了一些缩放和平移的操作。然后只需要viewPager.setPageTransformer(new DepthPageTransformer());就能实现如上的效果了。关键就是这里计算的这些公式是怎么的出来的呢？这便是我们今天要讨论的重点了。

废话不多收，直接进入我们今天的主题

好了，先来看一下这两个参数的解释
```
public interface PageTransformer {
    /**
     * Apply a property transformation to the given page.
     * 对给定的page施加一个属性切换效果
     *
     * @param page Apply the transformation to this page
                   将转换效果引用到此page
     * @param position Position of page relative to the current front-and-center                 
     *                 position of the pager. 0 is front and center. 1 is one full             
     *                 page position to the right, and -1 is one page position to the left.
     *                 相对于当前处于中心显示的page的postion。
     *                 0是当前页面。1是右侧页面。
     *                 -1是左侧页面。
     */
    void transformPage(View page, float position);
}
```
虽然解释还算挺清楚的，不过好像并没有什么卵用啊。根本没说滑动的时候page是如何变化的，position又是如何变化的。对我自己要实现一个切换效果根本没什么帮助啊。那么怎么办？好吧，没办法我们打一下log，看一下是如何变化的：

1. 布局文件
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

2. MainActivity
```
public class MainActivity extends AppCompatActivity {

    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 为了更容易看出position的变化规律，我们让pageLimit由3改为5
        viewPager.setOffscreenPageLimit(5);

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position));
                return views.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                Log.e("=====", position + "");
            }
        });
    }

    private void initViews() {
        views = new ArrayList<>();
        TextView left2 = new TextView(this);
        left2.setGravity(Gravity.CENTER);
        left2.setText("左2");
        left2.setTextSize(30);
        left2.setBackgroundColor(Color.parseColor("#33b5e5"));//浅蓝色
        views.add(left2);

        TextView left1 = new TextView(this);
        left1.setGravity(Gravity.CENTER);
        left1.setText("左1");
        left1.setTextSize(30);
        left1.setBackgroundColor(Color.parseColor("#99cc00"));//浅绿色
        views.add(left1);

        TextView middle = new TextView(this);
        middle.setGravity(Gravity.CENTER);
        middle.setText("中");
        middle.setTextSize(30);
        middle.setBackgroundColor(Color.parseColor("#cc0000"));//浅红色
        views.add(middle);

        TextView right1 = new TextView(this);
        right1.setGravity(Gravity.CENTER);
        right1.setText("右1");
        right1.setTextSize(30);
        right1.setBackgroundColor(Color.parseColor("#aa66cc"));//紫色
        views.add(right1);

        TextView right2 = new TextView(this);
        right2.setGravity(Gravity.CENTER);
        right2.setText("右2");
        right2.setTextSize(30);
        right2.setBackgroundColor(Color.parseColor("#ffbb33"));//浅橘色
        views.add(right2);
    }
}
```
接下来我们运行，效果如下：
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/2.gif)
这是很正常的一个ViewPager切换，我们看一下log

![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/log1.png)
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/log2.png)
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/log3.png)
我一共滑了三次，截取了三个log，你能看出什么规律来吗？讲道理其实为看不出来什么规律的。

诶！有人说了每次最后都有5个整数，**没错!**这还真是一条规律。
为了让我们的方便找出规律，我对5个page做了一些处理
```
left2.setTag("left2");
left1.setTag("left1");
middle.setTag("middle");
right1.setTag("right1");
right2.setTag("right3");
```
对log的输出方式也做了一些处理
```
Log.e((String) page.getTag(), position + "");
```
在来看一下
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/log4.png)
好像还是看不出来什么，不过从现在打出的log中能发现，其实滑动的时候是每个page都在动，transformPage方法回调的page并非只是当前滑动的page而是所有滑动的page，postion也并非是当前滑动page的postion变化，而是每个page的position变化。这个时候怎么办？这里有个小技巧
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/log5.png)
这是就只会看到左2页面的position变化了，可以看出来是一个有-4 -> -3的变化过程，同样我们也能得到其他每个页面的变化归率来。

下面是我列举的一张log表格，给大家展示一下每次滑动，每个page所对应的postion变化，
这里我们以**中**page为当前页面进行左右滑动。


|                        |      左2     |    左1   |    中（当前页面）   | 右1 |   右2    |
|:------------------:|:------------:|:----------:|:----------:|:-----------:|:---------------:|
|右滑(左1页面变为中的过程)|-1.9833333<br>-1.7740741<br>-1.5592593<br>-1.3277777<br>-1.1722223<br>-1.0592593<br>-1.0166667<br>-1.0018518<br>-1.0|-0.98333335<br>-0.7740741<br>-0.55925924<br>-0.32777777<br>-0.17222223<br>-0.05925926<br>-0.016666668<br>-0.0018518518<br>0.0|0.016666668<br>0.22592592<br>0.44074073<br>0.6722222<br>0.8277778<br>0.94074076<br>0.98333335<br>0.99814814<br>1.0|1.0666667<br>1.2907407<br>1.4388889<br>1.4944445<br>1.812963<br>1.9425926<br>1.9888889<br>1.9981482<br>2.0|2.0666666<br>2.2907407<br>2.4388888<br>2.4944444<br>2.812963<br>2.9425926<br>2.988889<br>2.9981482<br>3.0|
|右滑规律| -2 -> -1 | -1 -> 0 | 0 -> 1 | 1 -> 2 | 2-> 3 |
|左滑(右1页面变为中的过程)|-2.2277777<br>-2.338889<br>-2.4074075<br>-2.6814816<br>-2.85<br>-2.9388888<br>-2.9777777<br>-2.9944444<br>-3.0|-1.2277777<br>-1.3388889<br>-1.4074074<br>-1.6814815<br>-1.85<br>-1.9388889<br>-1.9777777<br>-1.9944445<br>-2.0|-0.22777778<br>-0.33888888<br>-0.4074074<br>-0.6814815<br>-0.85<br>-0.9388889<br>-0.9777778<br>-0.99444443<br>-1.0|0.7722222<br>0.6611111<br>0.5925926<br>0.31851852<br>0.15<br>0.06111111<br>0.022222223<br>0.0055555557<br>0.0|1.7722223<br>1.6611111<br>1.5925926<br>1.3185185<br>1.15<br>1.0611111<br>1.0222223<br>1.0055555<br>1.0|
|左滑规律| -2 -> -3 | -1 -> -2 | 0 -> -1 | 1 -> 0 | 2 -> 1 |

**观察这个表格，是不就就可以得出一些结论了（无论滑动前后当前页面都为显示在屏幕正中间页面）：**
- 滑动开始前每个页面的postion是一个整数，当前页面为0，左侧页面position值为0 - 相对于当前页面偏移页面数，右侧页面postion值 0 + 相对于当前页面偏移页面数
- 滑动结束后，每个页面的postion依然是一个整数，数值依然符合上一条规则，不过当前页面已经改变。也可以说左滑全部 -1，右滑全部 +1，postion变为0的页面成为新的当前页面
- 每个页面的postion右滑时都在增大，左滑时都在减小，滑动后结果为滑动前结果 ±1

有了这些结论，我们是不是就可以按部就班来实现一开始的那个效果了！
先来做一波分析，这个效果一个有三种变换：**透明度**、**缩放**、**平移**。其中平移可能有些人不太理解，我会在后面解释

#### 先来看看透明度
我们希望**当前页面**（显示在正中间的页面）不透，两侧页面在变为当前页面的过程中，慢慢变为不透，当前页面变为两侧页面的过程中，慢慢变为一个**最小透明度**（比如0.3f）
通过上面的结论其实处于变得三个页面**当前页面**、**左1**、**右1**的变化范围只会在[-1，1]这个区间里而在这意外的页面其实并不能看见，那么是不是可以这样？
```
if (position < -1 || position > 1) {
    page.setAlpha(0f);
} else {
   // ...
}
```
接下来就是对变化区间的处理了（我们以**右滑**为例进行分析）
**-1 -> 0** : **左1**变为**当前页面**
对于**左1**的透明度是不是由 minAlpha -> 1 啊？
**0 ->  1** : **当前页面**变为**右1**
对于**当前页面**透明度是不是由 1 -> minAlpha 啊？
可能你还没有什么感觉，我给你一个提示:
```
             position                    position
      -1 ----------------> 0       0 ---------------> 1

               x1                           x2
minAlpha ----------------> 1       1 ---------------> minAlpha
```
是不是很熟悉？这个x是什么？
——这TM不就是初中数学等比运算嘛。这个x不就是我们要求的变化过程中的透明度值嘛。
好的！开始解题：
```
解：设所求变量“透明度为x”。
由题可得：
  -1 - position      position - 0        0 - position       position - 1
 --------------- = ----------------  , ---------------- =  ----------------
  minAlpha - x           x - 1              1 - x2          x2 - minAlpha
解得：
x1 = 1 + position - minAlpha*position, x2 = 1 + minAlpha*position - position
```
之后在调用page.setAlpha(float alpha)传入对应的值 是不是就可以了？
```
viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {

    float MIN_ALPHA = 0.1f;

    @Override
    public void transformPage(View page, float position) {
        Log.e((String) page.getTag(), position + "");

        if (position < -1 || position > 1) {
            page.setAlpha(0f);
        } else {
            if (-1 <= position && position < 0) {
                page.setAlpha(1 + position - MIN_ALPHA * position);
            } else if (0 < position && position <= 1) {
                page.setAlpha(1 - position + MIN_ALPHA * position);
            } else {
                page.setAlpha(1f);
            }
        }
    }
});
```
这个效果我就不单独截屏了，等到缩放效果实现后一并查看（不要纠结为什么跟DepthPageTransformer代码为何不一样，因为它的效果在(-1，0)区间本就是不透所以是1，在(1，0)区间的小最小透明度是0，你可以试试吧MIN_ALPHA换成0再看看是不是一样的）

#### 再来看看缩放的实现
和上面一同，可以算出来最终的x = 1 - position + minScale * position，同样也不要纠结为何不一样，你可以尝试把DepthPageTransformer在这个(0，1)这个区间的缩放公式化简出来再看看。

再来看看现在的效果图
![](https://github.com/chuwe1/PageTransformerDemo/blob/master/screenshots/3.gif)
是不是既有透明度的变化，也有缩放的变化了？

#### 最后就是平移的变化了（这个是需要解释一番的）

首先来看看一开始那张图：右侧页面的进出都是以屏幕为中心只有缩放，何来平移啊？
再来看看我们现在的效果：我们已经实现了透明度和缩放的变化，为何与一开始的效果并不一样呢？
**以下纯属个人理解，如有大神觉得不妥，欢迎指出**：以右滑为例，右侧页面在这个过程中，本身就有一个往右平移的过程，如果我们手动给这个页面一个向左的平移来抵消这个向右的平移，那么是不是就只有缩放效果了呢？

原本position的变化：0 -> 1
原本translationX的变化：0 -> pageWidth
既然我们要给他一个想反方向的X轴平移，那么我们需要的translationX的变化是否就是：0 -> - pageWidth?
```
      position
0 ---------------> 1                  0 - position        position - 1
                              ===>  ---------------- = ------------------
         x                                0 - x          x - (-pageWith)
0 ---------------> -pageWith

==> x = - pageWidth * position
```
我们发现，跟DepthPageTransformer果然是一样的，现在修改一下代码：
```
if (position < -1 || position > 1) {
    page.setAlpha(0f);
} else {
    if (-1 <= position && position < 0) {
        page.setAlpha(1 + position - MIN_ALPHA * position);

        page.setScaleX(1);
        page.setScaleY(1);

        page.setTranslationX(0);
    } else if (0 < position && position <= 1) {
        page.setAlpha(1 - position + MIN_ALPHA * position);

        float scaleFactor = 1 - position + MIN_SCALE * position;
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);

        page.setTranslationX(-page.getWidth() * position);
    } else {
        page.setAlpha(1f);
    }
}
```
现在的效果，就和开始的是一模一样了。

相信大家看完以后对于position是如果变化的都能了解了，也都知道如何自己去实现出各种效果了。
