package menion.android.whereyougo.utils;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {
	
	private static UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		LogWriter.log("error.log", ex);
		defaultHandler.uncaughtException(thread, ex);
	}

}
