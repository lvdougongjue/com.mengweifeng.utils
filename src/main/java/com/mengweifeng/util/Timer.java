package com.mengweifeng.util;

/**
 * @author ASME
 *
 *         2011-7-25
 */
public interface Timer {

	/**
	 * 开始计时
	 */
	public void startup();

	/**
	 * 停止计时
	 */
	public void shutdown();

	/**
	 * 添加一个计时任务
	 * 
	 * @param task
	 *            计时任务
	 * @return TimerFuture
	 */
	public TimerFuture timing(TimerTask task);

}
