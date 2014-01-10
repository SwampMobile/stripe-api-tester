package com.swampmobile.stripeapitester.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;
import com.swampmobile.stripeapitester.R;
import com.swampmobile.stripeapitester.app.App;
import com.swampmobile.stripeapitester.app.StripeCardTestStore;

/**
 * DialogFragment that prompts the user for a public Stripe API key.
 * 
 * This dialog validates the given key with the Stripe server.
 * 
 * @author Matt
 *
 */
public class StripeApiKeyDialogFragment extends DialogFragment
{
	public static StripeApiKeyDialogFragment newInstance()
	{
		StripeApiKeyDialogFragment frag = new StripeApiKeyDialogFragment();
		
		return frag;
	}

	
	
	private EditText apiKeyEditText;
	private Button saveButton;
	private TextView errorTextView;
	
	private String key = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.dialog_stripe_key, container);
		apiKeyEditText = (EditText)view.findViewById(R.id.edittext_api_key);
		saveButton = (Button)view.findViewById(R.id.button_save);
		errorTextView = (TextView)view.findViewById(R.id.textview_error);
		
		getDialog().setTitle("Stripe Key");
		
		saveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				saveKey();
			}
		});
		
		clearErrors();
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		getDialog().setCancelable(false);
		
		apiKeyEditText.setText( getActivity().getSharedPreferences("stripe_auth", Context.MODE_PRIVATE).getString("api_key", "") );
	}
	
	private void saveKey()
	{
		clearErrors();
		
		key = apiKeyEditText.getText().toString();
		
		if(key.length() == 0)
		{
			showPleaseEnterTextError();
			return;
		}
		
		try {
			saveButton.setEnabled(false);
			App.getInstance().getStripe().setDefaultPublishableKey(key);
			App.getInstance().getStripe().createToken(StripeCardTestStore.getInstance().getValidCardTests().get(0).getCard(), new TokenCallback()
			{

				@Override
				public void onError(Exception error) 
				{
					showBadKeyError();
					saveButton.setEnabled(true);
				}

				@Override
				public void onSuccess(Token token) 
				{
					App.getInstance().setStripeKey(key);
					saveButton.setEnabled(true);
					dismiss();
				}
				
			});
		} 
		catch (AuthenticationException e) 
		{
			showBadKeyError();
			saveButton.setEnabled(true);
			return;
		}
	}
	
	private void clearErrors()
	{
		errorTextView.setText("");
		errorTextView.setVisibility(View.GONE);
	}
	
	private void showPleaseEnterTextError()
	{
		errorTextView.setText("Please enter your key");
		errorTextView.setVisibility(View.VISIBLE);
	}
	
	private void showBadKeyError()
	{
		errorTextView.setText("Key was rejected!");
		errorTextView.setVisibility(View.VISIBLE);
	}
}
