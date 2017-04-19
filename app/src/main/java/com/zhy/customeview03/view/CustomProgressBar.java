package com.zhy.customeview03.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.zhy.customeview03.R;

public class CustomProgressBar extends View
{
	/**
	 * 第一圈的颜色
	 */
	private int mFirstColor;
	/**
	 * 第二圈的颜色
	 */
	private int mSecondColor;
	/**
	 * 圈的宽度
	 */
	private int mCircleWidth;
	/**
	 * 画笔
	 */
	private Paint mPaint;
	/**
	 * 当前进度
	 */
	private int mProgress;

	/**
	 * 速度
	 */
	private int mSpeed;

	private RectF mOval;

	private int centre;
	private int radius;

	private InvalidateThread mInvalidateThread;

	/**
	 * 是否应该开始下一个
	 */
	private boolean isNext = false;

	/**
	 * 这里写构造函数，要保证一定能调用到第三个获取自定义属性的构造方法。
	 * @param context
	 * @param attrs
     */
	public CustomProgressBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);// 这里要用this，用super是不行的，调用不到第三个获取自定义属性的构造方法
	}

	public CustomProgressBar(Context context)
	{
		this(context, null);// 这里要用this，用super是不行的，调用不到第三个获取自定义属性的构造方法
	}

	/**
	 * 必要的初始化，获得一些自定义的值
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);// 这里可以用super
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyle, 0);

		mFirstColor = a.getColor(R.styleable.CustomProgressBar_firstColor, Color.GREEN);
		mSecondColor = a.getColor(R.styleable.CustomProgressBar_secondColor, Color.RED);
		mCircleWidth = a.getDimensionPixelSize(R.styleable.CustomProgressBar_circleWidth,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
		mSpeed = a.getInteger(R.styleable.CustomProgressBar_speed, 5);
		a.recycle();
		Log.d("CustomProgressBar", "CustomProgressBar TypedArray mCircleWidth= " + mCircleWidth + " mSpeed= " + mSpeed);

		/*下面这种写法会获取不到没有设置的属性值的默认值*/
//		int n = a.getIndexCount();
//		Log.d("CustomProgressBar", "CustomProgressBar TypedArray size= " + n);
//		for (int i = 0; i < n; i++)
//		{
//			int attr = a.getIndex(i);
//			switch (attr)
//			{
//			case R.styleable.CustomProgressBar_firstColor:
//				mFirstColor = a.getColor(attr, Color.GREEN);
//				break;
//			case R.styleable.CustomProgressBar_secondColor:
//				mSecondColor = a.getColor(attr, Color.RED);
//				break;
//			case R.styleable.CustomProgressBar_circleWidth:
//				mCircleWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
//						TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
//				break;
//			case R.styleable.CustomProgressBar_speed:
//				mSpeed = a.getInt(attr, 20);// 默认20
//				break;
//			}
//		}
//		a.recycle();
//		Log.d("CustomProgressBar", "CustomProgressBar TypedArray mCircleWidth= " + mCircleWidth + " mSpeed= " + mSpeed);

		mPaint = new Paint();
		mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
		mPaint.setAntiAlias(true); // 消除锯齿
		mPaint.setStyle(Paint.Style.STROKE); // 设置空心


		startInvalidateThread();

		// 绘图线程
//		new Thread()
//		{
//			public void run()
//			{
//				while (isProgressVisible)
//				{
//					mProgress++;
//					if (mProgress == 360)
//					{
//						mProgress = 0;
//						if (!isNext)
//							isNext = true;
//						else
//							isNext = false;
//					}
//					postInvalidate();
//					try
//					{
//						Thread.sleep(mSpeed);
//					} catch (InterruptedException e)
//					{
//						e.printStackTrace();
//					}
//				}
//			};
//		}.start();

	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		Log.d("CustomProgressBar", "onVisibilityChanged= " + visibility);
		if(visibility == VISIBLE) {
			startInvalidateThread();
		} else {
			stopInvalidateThread();
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	protected void onAttachedToWindow() {
		Log.d("CustomProgressBar", "onAttachedToWindow" );
		startInvalidateThread();
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		Log.d("CustomProgressBar", "onDetachedFromWindow");
		stopInvalidateThread();
		super.onDetachedFromWindow();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("CustomProgressBar", "onMeasure");
		int width = 0;
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int sizeWidth  = MeasureSpec.getSize(widthMeasureSpec);

		if(widthMode == MeasureSpec.EXACTLY) {
			width = sizeWidth ;
		} else {
//			int widthInDP = (int) getResources().getDimension(R.dimen.view_height);
//			width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDP, getResources().getDisplayMetrics());
			width = (int) getResources().getDimension(R.dimen.view_height);
		}


		setMeasuredDimension(width, width);
		centre = getWidth() / 2; // 获取圆心的x坐标
		radius = centre - mCircleWidth / 2;// 半径
		mOval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{

//		int centre = getWidth() / 2; // 获取圆心的x坐标
//		int radius = centre - mCircleWidth / 2;// 半径
//		mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
//		mPaint.setAntiAlias(true); // 消除锯齿
//		mPaint.setStyle(Paint.Style.STROKE); // 设置空心
//		if(mOval == null) {
//			Log.d("CustomProgressBar", "onDraw new mOval");
//			mOval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
//		}

		mPaint.setColor(mFirstColor); // 设置圆环的颜色
		canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
		mPaint.setColor(mSecondColor); // 设置圆环的颜色
//		canvas.drawArc(mOval, mProgress-90, 360-mProgress, false, mPaint); // 根据进度画圆弧
		canvas.drawArc(mOval, -90, mProgress, false, mPaint);

//		if (!isNext)
//		{// 第一颜色的圈完整，第二颜色跑
//			mPaint.setColor(mFirstColor); // 设置圆环的颜色
//			canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
//			mPaint.setColor(mSecondColor); // 设置圆环的颜色
//			canvas.drawArc(mOval, mProgress-90, 360-mProgress, false, mPaint); // 根据进度画圆弧
//		} else
//		{
//			mPaint.setColor(mSecondColor); // 设置圆环的颜色
//			canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
//			mPaint.setColor(mFirstColor); // 设置圆环的颜色
//			canvas.drawArc(mOval, -90, mProgress, false, mPaint); // 根据进度画圆弧
//		}

	}

	private void startInvalidateThread() {
		if(mInvalidateThread == null) {
			mInvalidateThread = new InvalidateThread();
			mInvalidateThread.start();
		}
	}

	private void stopInvalidateThread() {
		if(mInvalidateThread != null && mInvalidateThread.isAlive()) {
			mInvalidateThread.stopRunning();
			mInvalidateThread = null;
		}
	}

	class InvalidateThread extends Thread {

		private boolean _invalidateExit = true;

		public InvalidateThread() {
			Log.d("CustomProgressBar", "InvalidateThread constructor!");
		}

		public void stopRunning() {
			this._invalidateExit = false;
		}

		@Override
		public void run() {
			super.run();
			while (_invalidateExit) {
				mProgress++;
				if (mProgress == 360)
				{
					mProgress = 0;
					// 没有必要使用 isNext 变量
					int temp = mFirstColor;
					mFirstColor = mSecondColor;
					mSecondColor = temp;
//					if (!isNext)
//						isNext = true;
//					else
//						isNext = false;
				}
				postInvalidate();
//				Log.d("CustomProgressBar", "InvalidateThread postInvalidate");
				try
				{
					if(mSpeed <= 0 || mSpeed > 100) {
						break;
					}
					// 解决 速度值越大，动画越慢
					Thread.sleep(100/mSpeed);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}


		}
	}
}
