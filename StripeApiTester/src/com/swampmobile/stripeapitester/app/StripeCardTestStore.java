package com.swampmobile.stripeapitester.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.util.CardNumberFormatter;
import com.swampmobile.stripeapitester.models.StripeCardTest;

/**
 * Holds all valid and invalid card details and wraps them in StripeCardTest
 * objects which add some useful data for displaying tests.
 * 
 * @author Matt
 *
 */
public class StripeCardTestStore 
{
	private static StripeCardTestStore instance;
	
	public static StripeCardTestStore getInstance()
	{
		if(instance == null)
			instance = new StripeCardTestStore();
		
		return instance;
	}
	
	
	
	private ArrayList<StripeCardTest> validCardTests;
	private ArrayList<StripeCardTest> invalidCardTests;
	
	private StripeCardTestStore()
	{
		
	}
	
	/**
	 * Tests taken directly from Stripe documentation:
	 * https://stripe.com/docs/testing
	 * 
	 * The following errors cannot be tested on a client machine (like an Android device):
	 * - card_declined
	 * - expired_card
	 * - processing_error
	 */
	public void buildTests()
	{
		final Calendar cal = Calendar.getInstance();
		final int validMonth = cal.get(Calendar.MONTH) + 1; //calendar starts at 0, Stripe starts at 01
		final int validYear = cal.get(Calendar.YEAR) + 2;
		
		validCardTests = new ArrayList<StripeCardTest>();
		invalidCardTests = new ArrayList<StripeCardTest>();
		
		//--------- PASSING TESTS --------//
		int count = 0;
		
		// Visa 4242 4242 4242 4242
		validCardTests.add(new GoodTest(count++, new Card("4242 4242 4242 4242", validMonth, validYear, "")));
		
		// Visa 4012 8888 8888 1881
		validCardTests.add(new GoodTest(count++, new Card("4012 8888 8888 1881", validMonth, validYear, "")));
		
		// MasterCard 5555 5555 5555 4444
		validCardTests.add(new GoodTest(count++, new Card("5555 5555 5555 4444", validMonth, validYear, "")));
		
		// MasterCard 5105 1051 0510 5100
		validCardTests.add(new GoodTest(count++, new Card("5105 1051 0510 5100", validMonth, validYear, "")));
				
		// American Express 3782 822463 10005
		validCardTests.add(new GoodTest(count++, new Card("3782 822463 10005", validMonth, validYear, "")));
				
		// American Express 3714 496353 98431
		validCardTests.add(new GoodTest(count++, new Card("3714 496353 98431", validMonth, validYear, "")));
				
		// Discover 6011 1111 1111 1117
		validCardTests.add(new GoodTest(count++, new Card("6011 1111 1111 1117", validMonth, validYear, "")));
				
		// Discover 6011 0009 9013 9424
		validCardTests.add(new GoodTest(count++, new Card("6011 0009 9013 9424", validMonth, validYear, "")));
				
		// Diners Club 3056 9309 0259 04
		validCardTests.add(new GoodTest(count++, new Card("3056 9309 0259 04", validMonth, validYear, "")));
		
		// Diners Club 3852 0000 0232 37
		validCardTests.add(new GoodTest(count++, new Card("3852 0000 0232 37", validMonth, validYear, "")));
				
		// JCB 3530 1113 3330 0000
		validCardTests.add(new GoodTest(count++, new Card("3530 1113 3330 0000", validMonth, validYear, "")));
		
		// JCB 3566 0020 2036 0505
		validCardTests.add(new GoodTest(count++, new Card("3566 0020 2036 0505", validMonth, validYear, "")));

		//------- FAILING TESTS ---------//
		final int invalidMonth = 0; //calendar starts at 0, Stripe starts at 01
		final int invalidYear = cal.get(Calendar.YEAR) - 1;
		final String invalidCVC = "99";
		Card card;
		
		// Bad number
		invalidCardTests.add(new BadTest(count++, new Card("4242 4242 4242 4241", validMonth, validYear, "")));
		
		// Bad month
		invalidCardTests.add(new BadTest(count++, new Card("4242 4242 4242 4242", invalidMonth, validYear, "")));
		
		//Bad year
		invalidCardTests.add(new BadTest(count++, new Card("4242 4242 4242 4242", validMonth, invalidYear, "")));
		
		//Bad month and year
		invalidCardTests.add(new BadTest(count++, new Card("4242 4242 4242 4242", invalidMonth, invalidYear, "")));
		
		//Bad cvc
		invalidCardTests.add(new BadTest(count++, new Card("4242 4242 4242 4242", validMonth, validYear, invalidCVC)));
	}
	
	
	
	public StripeCardTest getCardById(long id)
	{
		for(StripeCardTest cardTest : validCardTests)
		{
			if(cardTest.getId() == id)
				return cardTest;
		}
		
		for(StripeCardTest cardTest : invalidCardTests)
		{
			if(cardTest.getId() == id)
				return cardTest;
		}
		
		return null;
	}
	
	public List<StripeCardTest> getValidCardTests()
	{
		return validCardTests;
	}
	
	public List<StripeCardTest> getInvalidCardTests()
	{
		return invalidCardTests;
	}
	
	
	/**
	 * Test for a card that is expected to pass.
	 * 
	 * @author Matt
	 *
	 */
	private class GoodTest implements StripeCardTest
	{		
		private long id;
		private Card card;
		
		public GoodTest(long id, Card card)
		{
			this.id = id;
			this.card = card;
		}
		
		@Override
		public long getId() { return id; }

		@Override
		public String getDescription() { return "This card is expected to PASS Stripe's API validation."; }

		@Override
		public Card getCard() 
		{
			return card;
		}

		@Override
		public boolean isExpectedResult(Object object) { return (object instanceof Token); }	
	}
	
	/**
	 * Test for a card that is expected to fail.
	 * 
	 * @author Matt
	 *
	 */
	private class BadTest implements StripeCardTest
	{		
		private long id;
		private Card card;
		
		public BadTest(long id, Card card)
		{
			this.id = id;
			this.card = card;
		}
		
		@Override
		public long getId() { return id; }

		@Override
		public String getDescription() { return "This card is expected to FAIL Stripe's API validation"; }

		@Override
		public Card getCard() 
		{
			return card;
		}

		@Override
		public boolean isExpectedResult(Object object) { return !(object instanceof Token); }	
	}
}
