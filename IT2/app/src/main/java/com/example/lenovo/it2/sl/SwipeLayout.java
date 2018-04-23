package com.example.lenovo.it2.sl;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SwipeLayout extends FrameLayout {

	private View contentView;// item内容区域的view
	private View deleteView;// delete区域的view
	private int deleteHeight;// delete区域的高度
	private int deleteWidth;// delete区域的宽度
	private int contentWidth;// content区域的宽度
	private ViewDragHelper viewDragHelper;

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SwipeLayout(Context context) {
		super(context);
		init();
	}
	enum SwipeState{  //使用枚举代替常量
		Open,Close;//用来表示item的状态
	}
	private SwipeState currentState = SwipeState.Close;//当前默认是关闭状态
	
	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		contentView = getChildAt(0);
		deleteView = getChildAt(1);
	}

	@Override//大小固定之后就可以获取宽和高了
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		deleteHeight = deleteView.getMeasuredHeight();
		deleteWidth = deleteView.getMeasuredWidth();
		contentWidth = contentView.getMeasuredWidth();
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// super.onLayout(changed, left, top, right, bottom);
		contentView.layout(0, 0, contentWidth, deleteHeight);
		deleteView.layout(contentView.getRight(), 0, contentView.getRight()
				+ deleteWidth, deleteHeight);
	}
	//使用SwipeLayoutManager来处理多个item同时被左滑的bug，因为是要用来管理整个ListView的所有item的，所以SwipeLayoutManager必须建立在外面
	//每个item在打开和关闭时都会用SwipeLayoutManager来记录，如果前一个item还没有关闭，则当前的item不能打开
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);//让viewDragHelper自己判断如何处理触摸事件
		//如果当前有打开的，则需要直接拦截，交给onTouch处理
		if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
			//先关闭已经打开的layout
			SwipeLayoutManager.getInstance().closeCurrentLayout(); //关毕动作放在onInterceptTouchEvent而不放在onTouchEvent
                      												//因为刚开始按下时onTouchEvent会调用一次onInterceptTouchEvent，只会在按下时调用一次
			                                                        //如果放在onTouchEvent就会不断被调用，就会出现卡顿现象
			result = true;
		}
		return result;//会调用onTouchEvent
	}
	
	private float downX,downY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//如果当前有打开的，则下面的逻辑不能执行
		//点击滑动之前先判断该item是否可以滑动
		//如果有其他的item已经打开则不能滑动
		if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
			requestDisallowInterceptTouchEvent(true);
			return true;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			//1.获取x和y方向移动的距离
			float moveX = event.getX();
			float moveY = event.getY();
			float delatX = moveX - downX;//x方向移动的距离
			float delatY = moveY - downY;//y方向移动的距离
			if(Math.abs(delatX)>Math.abs(delatY)){//水平移动
				//表示移动是偏向于水平方向，那么应该SwipeLayout应该处理，请求listview不要拦截
				requestDisallowInterceptTouchEvent(true);//请求父类不要处理触摸事件
			}
			//更新downX，downY
			downX = moveX;
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP: break;
		}
		viewDragHelper.processTouchEvent(event);//要将事件交给viewDragHelper处理
		return true;
	}

	private Callback callback = new Callback() {  //回调类
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==contentView||child==deleteView; //两个View都可以进行滑动处理
		}
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteWidth;
		} //水平移动范围，虽然定义了也没有什么用
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child==contentView){
				if(left>0)left = 0;  //限制不能向右移动
				if(left<-deleteWidth)left = -deleteWidth; //限制左边的移动范围
			}else if (child==deleteView) {
				if(left>contentWidth)left = contentWidth; //限制右边的极限（范围）
				if(left<(contentWidth-deleteWidth))left = contentWidth-deleteWidth;//限制左边的极限（范围）
			}
			return left;
		}
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {//伴随移动，当一个View移动时，另一个View也随着移动
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView==contentView){
				//手动移动deleteView
				deleteView.layout(deleteView.getLeft()+dx,deleteView.getTop()+dy,
						deleteView.getRight()+dx, deleteView.getBottom()+dy);
			}else if (deleteView==changedView) {
				//手动移动contentView
				contentView.layout(contentView.getLeft()+dx,contentView.getTop()+dy,
						contentView.getRight()+dx, contentView.getBottom()+dy);
			}
			//判断开和关闭的逻辑
			if(contentView.getLeft()==0 && currentState!=SwipeState.Close){//说明应该将state更改为关闭
				//当左边距等于0，就将当前的状态记录为关
				//之所以要加上currentState!=SwipeState.Close的判断
				// 是因为有可能是本来就是关的，只拉出来一点点，之后被回弹了，这个时候就不会修改了，因为本来就是关的了
				currentState = SwipeState.Close;
				//回调接口关闭的方法
				if(listener!=null){
					listener.onClose(getTag());
				}
				//说明当前的SwipeLayout已经关闭，需要让Manager清空一下
				SwipeLayoutManager.getInstance().clearCurrentLayout();
			}else if (contentView.getLeft()==-deleteWidth && currentState!=SwipeState.Open) {
				//说明应该将state更改为开
				currentState = SwipeState.Open;

				//回调接口打开的方法
				if(listener!=null){
					listener.onOpen(getTag());
				}
				//当前的Swipelayout已经打开，需要让Manager记录一下
				SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
			}
		}
		@Override//手指释放时
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteWidth/2){
				//应该打开deleteView
				open();
			}else {
				//应该关闭deleteView
				close();
			}
		}
	};
	/**
	 * 打开deleteView的方法
	 */
	public void open() {
		viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}
	/**
	 * 关闭deleteView的方法
	 */
	public void close() {
		viewDragHelper.smoothSlideViewTo(contentView,0,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	};
	public void computeScroll() {
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}


	//将打开和关闭的状态传递（暴露）给使用者，当打开的时候和关闭的时候，让item执行调用者自己写的onOpen和onClose
	private OnSwipeStateChangeListener listener;
	public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener){
		this.listener = listener;
	}
	
	public interface OnSwipeStateChangeListener{
		void onOpen(Object tag);
		void onClose(Object tag);
	}
	
}
