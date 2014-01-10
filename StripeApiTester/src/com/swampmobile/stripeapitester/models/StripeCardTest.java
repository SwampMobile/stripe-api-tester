package com.swampmobile.stripeapitester.models;

import com.stripe.android.model.Card;

/**
 * Represents a validation test for a given Stripe Card.  A StripeCardTest can provide 
 * a description of itself and can also tell you if a given object is the expected return 
 * value from the Stripe online API (token or error).
 * 
 * @author Matt
 *
 */
public interface StripeCardTest 
{
	long getId();
	String getDescription();
	Card getCard();
	boolean isExpectedResult(Object object);
}
