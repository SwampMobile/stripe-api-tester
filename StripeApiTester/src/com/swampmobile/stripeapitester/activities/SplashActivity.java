package com.swampmobile.stripeapitester.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.EActivity;
import com.swampmobile.stripeapitester.R;

/**
 * Splash screen.  Shows Stripe API Tester icon, waits 2 seconds, then launches
 * the app.
 * 
 * @author Matt
 *
 */
@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity 
{
	private boolean waitIsStarted = false;
	private boolean waitIsDone = false;
	private boolean isActivityActive = false;
	
	private AsyncTask<Void, Void, Void> splashTask = new AsyncTask<Void, Void, Void>()
	{

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			SplashActivity.this.onWaitFinished();
		}
		
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	isActivityActive = true;
    	
    	if(waitIsDone)
    	{
    		startApp();
    	}
    	else if(!waitIsStarted)
    	{
    		splashTask.execute();
    		waitIsStarted = true;
    	}
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	isActivityActive = false;
    }
    
    private void onWaitFinished()
    {
    	waitIsDone = true;
    	
    	if(isActivityActive)
    	{
    		startApp();
    	}
    }
    
    private void startApp()
    {
    	CardTestListActivity_.intent(this).start();
    }
    
}
