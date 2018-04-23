package com.example.lenovo.it2.sl;

public class SwipeLayoutManager {
	private SwipeLayoutManager(){}
	private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
	
	public static SwipeLayoutManager getInstance(){
		return mInstance;
	}
	
	private SwipeLayout currentLayout;//用来记录当前打开的SwipeLayout
	public void setSwipeLayout(SwipeLayout layout){
		this.currentLayout = layout;
	}
	
	/**
	 * 清空当前所记录的已经打开的layout
	 */
	public void clearCurrentLayout(){
		currentLayout = null;
	}
	
	/**
	 * 关闭当前已经打开的SwipeLayout
	 */
	public void closeCurrentLayout(){
		if(currentLayout!=null){
			currentLayout.close();
		}
	}
	/**
	 * 判断当前是否可以执行侧滑，如果没有打开的，则可以滑动。
	 * 如果有打开的，则判断打开的layout和当前按下的layout是否是同一个，是同一个才可以侧滑
	 * @return
	 */
	public boolean isShouldSwipe(SwipeLayout swipeLayout){
		if(currentLayout==null){
			//说明当前没有打开的layout，那一定可以打开
			return true;
		}else {
			//说明有打开的layout，如果是同一个才可以侧滑，这样可以避免多个item进行滑动，这里主要是关闭才会return true
			return currentLayout==swipeLayout;
		}
	}
}
