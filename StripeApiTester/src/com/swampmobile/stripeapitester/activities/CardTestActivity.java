package com.swampmobile.stripeapitester.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.swampmobile.stripeapitester.R;
import com.swampmobile.stripeapitester.app.App;
import com.swampmobile.stripeapitester.app.StripeCardTestStore;
import com.swampmobile.stripeapitester.models.StripeCardTest;
import com.swampmobile.stripeapitester.util.CardDisplayFormatter;

/**
 * Activity used to test the functionality of Stripe's online card validation
 * API.  If this Activity is provided with the ID of a card test, that card
 * test will be run and the results displayed.  If this Activity is not provided
 * with a test ID it will allow the user to enter custom card details, send
 * those details to the Stripe API, and then view the results.
 * 
 * @author Matt
 *
 */
@EActivity(R.layout.activity_cardtest)
public class CardTestActivity extends Activity
{
	public static final String EXTRA_CARD_TEST_ID = "card_test_id";
	
	@ViewById(R.id.textview_type)
	TextView cardTypeTextView;
	@ViewById(R.id.textview_number)
	TextView cardNumberTextView;
	@ViewById(R.id.textview_exp)
	TextView cardExpTextView;
	@ViewById(R.id.textview_cvc)
	TextView cardCvcTextView;
	
	@ViewById(R.id.regular_card_display)
	View regulardCardDisplay;
	@ViewById(R.id.textview_expectation)
	TextView testExpectationTextView;
	
	@ViewById(R.id.custom_card_display)
	View customCardDisplay;
	@ViewById(R.id.edittext_number)
	EditText numberEditText;
	@ViewById(R.id.edittext_month)
	EditText monthEditText;
	@ViewById(R.id.edittext_year)
	EditText yearEditText;
	@ViewById(R.id.edittext_cvc)
	EditText cvcEditText;
	
	@ViewById(R.id.textview_results)
	TextView resultsTextView;
	
	private StripeCardTest cardTest;
	
	@AfterViews
	void afterViews()
	{
		// Style actionbar
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		
		// Grab the test we're supposed to show, or setup for a "Custom Card" if id is negative
		long cardTestId = getIntent().getLongExtra(EXTRA_CARD_TEST_ID, -1);
		if(cardTestId >= 0)
			cardTest = StripeCardTestStore.getInstance().getCardById(cardTestId);
		else
			cardTest = null;
		
		// Update visual state for card test
		updateCardView();
	}
	
	private boolean isUsingCustomCard()
	{
		return cardTest == null;
	}
	
	private void updateCardView()
	{
		if(!isUsingCustomCard())
		{
			customCardDisplay.setVisibility(View.GONE);
			regulardCardDisplay.setVisibility(View.VISIBLE);
			
			Card card = cardTest.getCard();
			cardTypeTextView.setText(CardDisplayFormatter.formatType(card.getType()));
			cardNumberTextView.setText(CardDisplayFormatter.formatNumber(card.getNumber()));
			cardExpTextView.setText(CardDisplayFormatter.formatExp(card.getExpMonth(), card.getExpYear()));
			cardCvcTextView.setText(CardDisplayFormatter.formatCvc(card.getCVC()));
			
			testExpectationTextView.setText(cardTest.getDescription());
		}
		else
		{
			regulardCardDisplay.setVisibility(View.GONE);
			customCardDisplay.setVisibility(View.VISIBLE);
			
			cardTypeTextView.setText(CardDisplayFormatter.formatType(null));
			cardNumberTextView.setText(CardDisplayFormatter.formatNumber(null));
			cardExpTextView.setText(CardDisplayFormatter.formatExp(null, null));
			cardCvcTextView.setText(CardDisplayFormatter.formatCvc(null));
		}
	}
	
	@Click(R.id.button_run_test)
	void onRunTestClick()
	{
		if(!isUsingCustomCard())
		{
			Card card = cardTest.getCard();
			
			App.getInstance().getStripe().createToken(card, new TokenCallback()
			{

				@Override
				public void onError(Exception error) 
				{
					StringBuilder builder = new StringBuilder();
					builder.append("Stripe says that this card is INVALID");
					builder.append("\n\n");
					builder.append(error.getLocalizedMessage());
					builder.append("\n\n");
					
					if(cardTest.isExpectedResult(error))
					{
						// We expected an error
						builder.append("That's what we expected, carry on.");
					}
					else
					{
						// We did not expect an error
						builder.append("Uh oh, this card was supposed to pass.  There is a problem.");
					}
					
					resultsTextView.setText(builder.toString());
				}

				@Override
				public void onSuccess(Token token) 
				{
					StringBuilder builder = new StringBuilder();
					builder.append("Stripe says that this card is VALID");
					builder.append("\n\n");
					
					if(cardTest.isExpectedResult(token))
					{
						// We expected a token
						builder.append("That's what we expected, carry on.");
					}
					else
					{
						// We did not expect a token
						builder.append("Uh oh, this card was supposed to be rejected.  There is a problem.");
					}
					
					resultsTextView.setText(builder.toString());
				}
				
			});
		}
		else
		{
			// Package user's desired card details and send to Stripe
			
			String monthString = monthEditText.getText().toString();
			Integer month = monthString.length() > 0 ? Integer.valueOf(monthString) : null;
			
			String yearString = yearEditText.getText().toString();
			Integer year = yearString.length() > 0 ? Integer.valueOf(yearString) : null;
			
			Card card = new Card(numberEditText.getText().toString(), month, year, cvcEditText.getText().toString());
			
			App.getInstance().getStripe().createToken(card, new TokenCallback()
			{
				@Override
				public void onError(Exception error) 
				{
					StringBuilder builder = new StringBuilder();
					builder.append("Stripe says that this card is INVALID");
					builder.append("\n\n");
					builder.append(error.getLocalizedMessage());
					
					resultsTextView.setText(builder.toString());
				}

				@Override
				public void onSuccess(Token token) 
				{
					StringBuilder builder = new StringBuilder();
					builder.append("Stripe says that this card is VALID");
					builder.append("\n\n");
					builder.append("Here is the token that Stripe sent back:");
					builder.append("\n\n");
					builder.append(token);
					
					resultsTextView.setText(builder.toString());
				}
				
			});
		}
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_to_right, R.anim.slide_out_to_right);
	}
	
	@OptionsItem(android.R.id.home)
	void onUpClick()
	{
		onBackPressed();
	}
}
