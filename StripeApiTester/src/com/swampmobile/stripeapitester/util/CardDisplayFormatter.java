package com.swampmobile.stripeapitester.util;

import com.stripe.android.util.CardNumberFormatter;

/**
 * Utility class to display card details to the user in a the desired format.
 * 
 * @author Matt
 *
 */
public class CardDisplayFormatter 
{
	public static String formatType(String type)
	{
		if(type == null || type.length() == 0)
			return "Custom";
		else
			return type;
	}
	
	public static String formatNumber(String number)
	{
		if(number == null || number.length() == 0)
			return "xxxx-xxxx-xxxx-xxxx";
		else
			return CardNumberFormatter.format(number, false);
	}
	
	public static String formatExp(Integer month, Integer year)
	{
		String monthOrBlank = month == null ? "__" : String.format("%02d", month);
		String yearOrBlank = year == null ? "__" : year + "";
		
		return String.format("%s / %s", monthOrBlank, yearOrBlank);
	}
	
	public static String formatCvc(String cvc)
	{
		if(cvc == null || cvc.length() == 0)
			return "____";
		else
			return cvc;
	}
	
}
