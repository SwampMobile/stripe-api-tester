package com.swampmobile.stripeapitester.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.swampmobile.stripeapitester.R;
import com.swampmobile.stripeapitester.adapters.StripeCardTestAdapter;
import com.swampmobile.stripeapitester.app.App;
import com.swampmobile.stripeapitester.fragments.StripeApiKeyDialogFragment;

/**
 * Presents a list of cards.  Each card may be selected to test that
 * card's validity against the Stripe API.
 * 
 * @author Matt
 *
 */
@EActivity(R.layout.activity_cardtestlist)
public class CardTestListActivity extends Activity
{
	@ViewById(R.id.list)
	ListView list;
	private StripeCardTestAdapter adapter;
	
	@AfterViews
	void afterViews()
	{
		adapter = new StripeCardTestAdapter(getApplicationContext());
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> list, View view, int position, long id) 
			{
				// Card selected, launch the card test activity
				Intent intent = new Intent(CardTestListActivity.this, CardTestActivity_.class);
				if(id >= 0)
					intent.putExtra(CardTestActivity_.EXTRA_CARD_TEST_ID, id);
				startActivity(intent);
				CardTestListActivity.this.overridePendingTransition(R.anim.slide_in_to_left, R.anim.slide_out_to_left);
			}
			
		});
		
		// If no Stripe key is set, we need to ask for one
		if(!App.getInstance().isStripeKeySet())
			StripeApiKeyDialogFragment.newInstance().show(getFragmentManager(), "api_key");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Add settings button to change Stripe API key after original save
		getMenuInflater().inflate(R.menu.settings, menu);
		
		return true;
	}
	
	@OptionsItem(R.id.action_settings)
	void onSettingsClick()
	{
		StripeApiKeyDialogFragment.newInstance().show(getFragmentManager(), "api_key");
	}
}
