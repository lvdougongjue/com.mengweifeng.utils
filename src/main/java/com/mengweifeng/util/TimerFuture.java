package com.mengweifeng.util;

/**
 * @author ASME
 *
 *         2011-7-25
 */
public interface TimerFuture {

	/**
	 * 获取跟它关联的计时任务
	 * 
	 * @return 计时任务
	 */
	public TimerTask getTimerTask();

	/**
	 * 计时任务是否已经到期<br>
	 * 对于interval类型的任务,Cancel掉后才返回true
	 * 
	 * @return 任务是否已经到期
	 */
	public boolean isExpired();

	/**
	 * 计时任务是否已经取消
	 * 
	 * @return 任务是否已经取消
	 */
	public boolean isCancelled();

	/**
	 * 取消计时任务
	 */
	public void cancel();
}
