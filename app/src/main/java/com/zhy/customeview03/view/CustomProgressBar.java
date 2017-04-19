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
	 * ��һȦ����ɫ
	 */
	private int mFirstColor;
	/**
	 * �ڶ�Ȧ����ɫ
	 */
	private int mSecondColor;
	/**
	 * Ȧ�Ŀ��
	 */
	private int mCircleWidth;
	/**
	 * ����
	 */
	private Paint mPaint;
	/**
	 * ��ǰ����
	 */
	private int mProgress;

	/**
	 * �ٶ�
	 */
	private int mSpeed;

	private RectF mOval;

	private int centre;
	private int radius;

	private InvalidateThread mInvalidateThread;

	/**
	 * �Ƿ�Ӧ�ÿ�ʼ��һ��
	 */
	private boolean isNext = false;

	/**
	 * ����д���캯����Ҫ��֤һ���ܵ��õ���������ȡ�Զ������ԵĹ��췽����
	 * @param context
	 * @param attrs
     */
	public CustomProgressBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);// ����Ҫ��this����super�ǲ��еģ����ò�����������ȡ�Զ������ԵĹ��췽��
	}

	public CustomProgressBar(Context context)
	{
		this(context, null);// ����Ҫ��this����super�ǲ��еģ����ò�����������ȡ�Զ������ԵĹ��췽��
	}

	/**
	 * ��Ҫ�ĳ�ʼ�������һЩ�Զ����ֵ
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);// ���������super
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyle, 0);

		mFirstColor = a.getColor(R.styleable.CustomProgressBar_firstColor, Color.GREEN);
		mSecondColor = a.getColor(R.styleable.CustomProgressBar_secondColor, Color.RED);
		mCircleWidth = a.getDimensionPixelSize(R.styleable.CustomProgressBar_circleWidth,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
		mSpeed = a.getInteger(R.styleable.CustomProgressBar_speed, 5);
		a.recycle();
		Log.d("CustomProgressBar", "CustomProgressBar TypedArray mCircleWidth= " + mCircleWidth + " mSpeed= " + mSpeed);

		/*��������д�����ȡ����û�����õ�����ֵ��Ĭ��ֵ*/
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
//				mSpeed = a.getInt(attr, 20);// Ĭ��20
//				break;
//			}
//		}
//		a.recycle();
//		Log.d("CustomProgressBar", "CustomProgressBar TypedArray mCircleWidth= " + mCircleWidth + " mSpeed= " + mSpeed);

		mPaint = new Paint();
		mPaint.setStrokeWidth(mCircleWidth); // ����Բ���Ŀ��
		mPaint.setAntiAlias(true); // �������
		mPaint.setStyle(Paint.Style.STROKE); // ���ÿ���


		startInvalidateThread();

		// ��ͼ�߳�
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
		centre = getWidth() / 2; // ��ȡԲ�ĵ�x����
		radius = centre - mCircleWidth / 2;// �뾶
		mOval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // ���ڶ����Բ������״�ʹ�С�Ľ���
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{

//		int centre = getWidth() / 2; // ��ȡԲ�ĵ�x����
//		int radius = centre - mCircleWidth / 2;// �뾶
//		mPaint.setStrokeWidth(mCircleWidth); // ����Բ���Ŀ��
//		mPaint.setAntiAlias(true); // �������
//		mPaint.setStyle(Paint.Style.STROKE); // ���ÿ���
//		if(mOval == null) {
//			Log.d("CustomProgressBar", "onDraw new mOval");
//			mOval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // ���ڶ����Բ������״�ʹ�С�Ľ���
//		}

		mPaint.setColor(mFirstColor); // ����Բ������ɫ
		canvas.drawCircle(centre, centre, radius, mPaint); // ����Բ��
		mPaint.setColor(mSecondColor); // ����Բ������ɫ
//		canvas.drawArc(mOval, mProgress-90, 360-mProgress, false, mPaint); // ���ݽ��Ȼ�Բ��
		canvas.drawArc(mOval, -90, mProgress, false, mPaint);

//		if (!isNext)
//		{// ��һ��ɫ��Ȧ�������ڶ���ɫ��
//			mPaint.setColor(mFirstColor); // ����Բ������ɫ
//			canvas.drawCircle(centre, centre, radius, mPaint); // ����Բ��
//			mPaint.setColor(mSecondColor); // ����Բ������ɫ
//			canvas.drawArc(mOval, mProgress-90, 360-mProgress, false, mPaint); // ���ݽ��Ȼ�Բ��
//		} else
//		{
//			mPaint.setColor(mSecondColor); // ����Բ������ɫ
//			canvas.drawCircle(centre, centre, radius, mPaint); // ����Բ��
//			mPaint.setColor(mFirstColor); // ����Բ������ɫ
//			canvas.drawArc(mOval, -90, mProgress, false, mPaint); // ���ݽ��Ȼ�Բ��
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
					// û�б�Ҫʹ�� isNext ����
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
					// ��� �ٶ�ֵԽ�󣬶���Խ��
					Thread.sleep(100/mSpeed);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}


		}
	}
}
