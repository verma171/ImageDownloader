package utils;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by ravi on 29-Mar-17.
 */

/**
 * Handler to execute HTTP request.
 */
public class MyWorkerThread extends HandlerThread {

    private Handler mWorkerHandler;

    public MyWorkerThread(String name) {
        super(name);
    }

	/**
     * post runnable task to handlerthread messageqeue.
     * @param task
     */
    public void postTask(Runnable task){
        mWorkerHandler.post(task);
    }

	/**
     * prepare handler to post task in handlertask messagequeue
     */
    public void prepareHandler(){
        mWorkerHandler = new Handler(getLooper());
    }
}