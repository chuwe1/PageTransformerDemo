package com.chuwe1.pagetransformerdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
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

        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {

            float MIN_ALPHA = 0.2f;
            float MIN_SCALE = 0.6f;

            @Override
            public void transformPage(View page, float position) {
                Log.e((String) page.getTag(), position + "");
                /*
                 *          左2    左侧页面（左1）  当前页面   右侧页面（右1）  右2
                 * 左滑： -3 <- -2   -2 <- -1     -1 <- 0     0 <- 1      1 <- 2
                 *
                 * 右滑： -2 -> -1   -1 -> 0       0 -> 1     1 -> 2      2 -> 3
                 *
                 */

                if (position < -1 || position > 1) {
                    page.setAlpha(0);
                    page.setScaleX(0);
                    page.setScaleY(0);
                    page.setTranslationX(0);
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
                        page.setScaleX(1);
                        page.setScaleY(1);
                        page.setTranslationX(0);
                    }
                }
            }
        });

        // 翻转效果
        // viewPager.setPageTransformer(false, new FlipPageTransformer());
    }


    private void initViews() {
        views = new ArrayList<>();
        TextView left2 = new TextView(this);
        left2.setGravity(Gravity.CENTER);
        left2.setText("左2");
        left2.setTextSize(30);
        left2.setTag("left2");
        left2.setBackgroundColor(Color.parseColor("#33b5e5"));//浅蓝色
        views.add(left2);

        TextView left1 = new TextView(this);
        left1.setGravity(Gravity.CENTER);
        left1.setText("左1");
        left1.setTextSize(30);
        left1.setTag("left1");
        left1.setBackgroundColor(Color.parseColor("#99cc00"));//浅绿色
        views.add(left1);

        TextView middle = new TextView(this);
        middle.setGravity(Gravity.CENTER);
        middle.setText("中");
        middle.setTextSize(30);
        middle.setTag("middle");
        middle.setBackgroundColor(Color.parseColor("#cc0000"));//浅红色
        views.add(middle);

        TextView right1 = new TextView(this);
        right1.setGravity(Gravity.CENTER);
        right1.setText("右1");
        right1.setTextSize(30);
        right1.setTag("right1");
        right1.setBackgroundColor(Color.parseColor("#aa66cc"));//紫色
        views.add(right1);

        TextView right2 = new TextView(this);
        right2.setGravity(Gravity.CENTER);
        right2.setText("右2");
        right2.setTextSize(30);
        right2.setTag("right2");
        right2.setBackgroundColor(Color.parseColor("#ffbb33"));//浅橘色
        views.add(right2);
    }
}
