package com.example.uva_app.utils;

import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class HttpSender {
	
	
	private static Thread tasksThread = null;
	private static BlockingQueue<HttpAsyncTask> httpBlockingQueue = new LinkedBlockingQueue<HttpAsyncTask>();
	
	
	private static void execute(HttpAsyncTask httpAsyncTask)
	{
		if (tasksThread == null)
		{
			tasksThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true)
					{
						HttpAsyncTask curTask = null;
						try
						{
							curTask = httpBlockingQueue.take();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if (curTask != null)
						{
							curTask.execute();
						}
					}
					
				}
			});
			
			tasksThread.start();
		}
		
		try {
			httpBlockingQueue.put(httpAsyncTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void post(String urlStr, Object uploadObject, IResponse httpresponse, int httpRequestType) throws MalformedURLException
	{
		execute(new HttpAsyncTask(urlStr, uploadObject, httpresponse, httpRequestType));
	}
	
	
	public static void get(String urlStr, IResponse httpresponse, int httpRequestType) throws MalformedURLException
	{
		execute(new HttpAsyncTask(urlStr, null, httpresponse, httpRequestType));
	}

}
