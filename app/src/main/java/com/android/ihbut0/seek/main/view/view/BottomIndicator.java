package com.android.ihbut0.seek.main.view.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ihbut0.seek.R;

public class BottomIndicator extends LinearLayout {

    public static final int ICON_INDEX_NORMAL = 0;
    public static final int ICON_INDEX_SELECTED = 1;
    public static final int DEFAULT_SELECTED_ITEM = 0;
    public static final String COLOR_TEXT_NORMAL = "#FF999999";
    public static final String COLOR_TEXT_SELECTED = "#FF2a4826";
    //文字颜色渐变类
    private ArgbEvaluator mColorEvaluator;
    //正常文本的颜色
    private int mTextNormalColor;
    //选中时文本的颜色
    private int mTextSelectedColor;
    //最后的位置
    private int mLastPosition;
    //选中的位置
    private int mSelectedPosition;
    //选择的偏移量
    private float mSelectionOffset;

    //底部tab文本
    private String mTitles[] = {"聊一聊","好友","我"};
    //对应的图标
    private int mIconRes[][] = {
            {R.drawable.btn_message_normal, R.drawable.btn_message_selected},
            {R.drawable.btn_list_normal, R.drawable.btn_list_selected},
            {R.drawable.btn_me_normal, R.drawable.btn_me_selected}};

    //Item数组
    private View[] mItemLayout;
    //关联的ViewPager
    private ViewPager mViewPager;

    public BottomIndicator(Context context) {
        this(context,null);
    }

    public BottomIndicator(Context context,AttributeSet attrs) {
        this(context,attrs,0);
    }

    public BottomIndicator(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        //初始化
        init();
    }

    /**
     * 初始化
     */
    private void init()
    {
        //实例化颜色渐变类
        mColorEvaluator = new ArgbEvaluator();
        //选中和未选中的文本的颜色
        mTextNormalColor = Color.parseColor(COLOR_TEXT_NORMAL);
        mTextSelectedColor = Color.parseColor(COLOR_TEXT_SELECTED);
    }

    public void setViewPager(ViewPager viewPager)
    {
        //清空所有的view
        removeAllViews();

        mViewPager = viewPager;
        //viewPager数据不为空
        if(viewPager != null && viewPager.getAdapter() != null)
        {
            //设置监听
            viewPager.addOnPageChangeListener(new ViewPagerListener());
            try
            {
                populateTabLayout();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * 生成底部tab
     */
    private void populateTabLayout() throws Exception
    {

        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();
        //根据adapter中item 的数量生成数组
        mItemLayout = new View[ adapter.getCount() ];
        //遍历
        for(int i = 0;i < adapter.getCount();i++)
        {
                //带字tab
                final View tabView = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_tab,this,false);
                //找不到对应的布局
                if(tabView == null)
                    throw new IllegalStateException("tabView is null.");
                mItemLayout[ i ] = tabView;
                //图标
                BottomIconView iconView = (BottomIconView)tabView.findViewById(R.id.bottom_tab_icon);
                iconView.init(mIconRes[ i ][ ICON_INDEX_NORMAL ],mIconRes[ i ][ ICON_INDEX_SELECTED ]);
                //文字
                TextView textView = (TextView)tabView.findViewById(R.id.bottom_tab_text);
                textView.setText(mTitles[ i ]);

                //改变宽度和权重 item平分屏幕
                LayoutParams lp = (LayoutParams)tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;

                tabView.setOnClickListener(tabClickListener);
                addView(tabView);

                if(i == mViewPager.getCurrentItem())
                {
                    iconView.transformPage(DEFAULT_SELECTED_ITEM);
                    tabView.setSelected(true);
                    textView.setTextColor(mTextSelectedColor);
                }

        }
    }


    /**
     * 内部ViewPager监听
     * 外面想监听，自定义一个
     */
    private class ViewPagerListener implements ViewPager.OnPageChangeListener
    {
        //状态
        private int mScrollState;

        @Override
        public void onPageScrolled(int position,float positionOffset,int positionOffsetPixels)
        {
            onViewPagerPageChanged(position,positionOffset);
        }

        @Override
        public void onPageSelected(int position)
        {

            for(int i = 0;i < getChildCount();i++)
            {
                //图标
                BottomIconView bottomIcon = ((BottomIconView)mItemLayout[ i ].findViewById(R.id.bottom_tab_icon));
                bottomIcon.transformPage(position == i ? 0 : 1);
                TextView bottomText = ((TextView)mItemLayout[ i ].findViewById(R.id.bottom_tab_text));
                bottomText.setTextColor(position == i ? mTextSelectedColor : mTextNormalColor);

            }

            if(mScrollState == ViewPager.SCROLL_STATE_IDLE)
            {
                onViewPagerPageChanged(position,0f);
            }
            //设置选中项
            for(int i = 0, size = getChildCount();i < size;i++)
            {
                getChildAt(i).setSelected(position == i);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            mScrollState = state;
        }
    }

    /**
     * ViewPager的item改变
     *
     * @param position
     * @param positionOffset
     */
    private void onViewPagerPageChanged(int position,float positionOffset)
    {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        if(positionOffset == 0f && mLastPosition != mSelectedPosition)
        {
            mLastPosition = mSelectedPosition;
        }
        invalidate();
    }

    /**
     * 绘制方法
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        final int childCount = getChildCount();
        if(childCount > 0)
        {
            if(mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1))
            {

                View selectedTab = getChildAt(mSelectedPosition);
                View nextTab = getChildAt(mSelectedPosition + 1);

                View selectedIconView = ((LinearLayout)selectedTab).getChildAt(0);
                View nextIconView = ((LinearLayout)nextTab).getChildAt(0);

                View selectedTextView = ((LinearLayout)selectedTab).getChildAt(1);
                View nextTextView = ((LinearLayout)nextTab).getChildAt(1);

                //draw icon alpha
                if(selectedIconView instanceof BottomIconView && nextIconView instanceof BottomIconView)
                {
                    ((BottomIconView)selectedIconView).transformPage(mSelectionOffset);
                    ((BottomIconView)nextIconView).transformPage(1 - mSelectionOffset);
                }
                /**
                 * 使用ArgbEvaluator类来控制文本的颜色渐变
                 */
                //draw text color
                Integer selectedColor = (Integer)mColorEvaluator.evaluate(mSelectionOffset,
                        mTextSelectedColor,
                        mTextNormalColor);

                Integer nextColor = (Integer)mColorEvaluator.evaluate(1 - mSelectionOffset,
                        mTextSelectedColor,
                        mTextNormalColor);

                if(selectedTextView instanceof TextView && nextTextView instanceof TextView)
                {
                    ((TextView)selectedTextView).setTextColor(selectedColor);
                    ((TextView)nextTextView).setTextColor(nextColor);
                }
            }
        }
    }

    /**
     * Tab的Item点击
     */
    private class TabClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            for(int i = 0;i < getChildCount();i++)
            {
                if(v == getChildAt(i))
                {
                    mViewPager.setCurrentItem(i,false);
                    return;
                }

            }
        }
    }

}
