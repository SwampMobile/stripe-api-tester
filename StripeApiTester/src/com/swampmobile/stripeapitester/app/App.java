package com.swampmobile.stripeapitester.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.stripe.android.Stripe;
import com.stripe.exception.AuthenticationException;

public class App extends Application
{
	private static App instance;
	
	public static App getInstance()
	{
		return instance;
	}
	
	
	
	private boolean stripeKeySet = false;
	private Stripe stripe;
	
	public App()
	{
		super();
		
		App.instance = this;
	}
	
	@Override
	public void onCreate()
	{
		stripe = new Stripe();
		
		// If user already saved a key, load it
		SharedPreferences prefs = getSharedPreferences("stripe_auth", Context.MODE_PRIVATE);
		if(prefs.contains("api_key"))
		{
			try {
				stripe.setDefaultPublishableKey(prefs.getString("api_key", ""));
				stripeKeySet = true;
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}
		}
		
		// Initialize all the valid and invalid card tests
		StripeCardTestStore.getInstance().buildTests();
	}
	
	public boolean isStripeKeySet()
	{
		return stripeKeySet;
	}
	
	/**
	 * Does not validate the given stripe key, only saves it
	 * 
	 * @param key
	 * @return
	 */
	public boolean setStripeKey(String key)
	{
		SharedPreferences.Editor edit = getSharedPreferences("stripe_auth", Context.MODE_PRIVATE).edit();
		edit.putString("api_key", key);
		edit.commit();
		
		try {
			stripe.setDefaultPublishableKey(key);
			stripeKeySet = true;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public Stripe getStripe() { return stripe; }
	
	
}
