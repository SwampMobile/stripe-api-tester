package com.swampmobile.stripeapitester.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stripe.android.util.CardNumberFormatter;
import com.swampmobile.stripeapitester.R;
import com.swampmobile.stripeapitester.app.StripeCardTestStore;
import com.swampmobile.stripeapitester.models.StripeCardTest;

/**
 * Adapter for displaying a list of card tests.  This adapter presents header views for:
 * - "custom card"
 * - "valid cards"
 * - "invalid cards"
 * 
 * This adapter injects a "custom card" item as the first card.  The rest of the card
 * tests are retrieved from {@code StripeCardTestStore}.
 * 
 * @author Matt
 *
 */
public class StripeCardTestAdapter extends BaseAdapter
{
	private static final String TAG = "StripeCardTestAdapter";
	
	private static final int ITEM_TYPE_HEADER = 0;
	private static final int ITEM_TYPE_CARD = 1;
	
	private Context context;
	private LayoutInflater inflater;
	private List<StripeCardTest> validCardTests;
	private List<StripeCardTest> invalidCardTests;
	private StripeCardTest cardTest;
	private Holder holder;
	private Integer month, year;
	private String monthOrBlank, yearOrBlank, cvc;

	public StripeCardTestAdapter(Context context)
	{
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.notifyDataSetChanged();
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		this.validCardTests = StripeCardTestStore.getInstance().getValidCardTests();
		this.invalidCardTests = StripeCardTestStore.getInstance().getInvalidCardTests();
		
		super.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() 
	{
		return 1 + 1 + 1 + validCardTests.size() + 1 + invalidCardTests.size(); // +1's are used to show order of items in this list
	}

	@Override
	public Object getItem(int position) 
	{
		if(position == 0) // "Custom Card" header
			return "Custom Card";
		else if(position == 1) // Custom card item
			return null;
		else if(position == 2) // "Valid Cards" header
			return "Valid Cards";
		else if(position < (validCardTests.size() + 3)) // Valid card items
			return validCardTests.get(position - 3);
		else if(position == (3 + validCardTests.size())) // "Invalid Cards" header
			return "Invalid Cards";
		else
			return invalidCardTests.get(position - validCardTests.size() - 4);
	}
	
	@Override
	public int getViewTypeCount()
	{
		return 2;
	}
	
	@Override
	public int getItemViewType(int position)
	{
		if(position == 0) // "Custom Card" header
			return ITEM_TYPE_HEADER;
		else if(position == 1) // Custom card item
			return ITEM_TYPE_CARD;
		else if(position == 2) // "Valid Cards" header
			return ITEM_TYPE_HEADER;
		else if(position < (validCardTests.size() + 3)) // Valid card items
			return ITEM_TYPE_CARD;
		else if(position == (3 + validCardTests.size())) // "Invalid Cards" header
			return ITEM_TYPE_HEADER;
		else
			return ITEM_TYPE_CARD;
	}

	@Override
	public long getItemId(int position) 
	{
		// Headers return an id of -99
		if( !(getItem(position) instanceof StripeCardTest) )
			return -99;
		
		if(position == 1) // Custom card given id of -1
			return -1;
		else // All other cards have their own id
			return ((StripeCardTest)getItem(position)).getId();
	}

	private View createListItemView(LayoutInflater inflater, int type)
	{
		View view = null;
		if(type == ITEM_TYPE_HEADER)
		{
			view = inflater.inflate(R.layout.listitem_cardtest_header, null);
		}
		else if(type == ITEM_TYPE_CARD)
		{
			view = inflater.inflate(R.layout.listitem_cardtest, null);
			
			holder = new Holder();
			holder.typeTextView = (TextView) view.findViewById(R.id.textview_type);
			holder.numberTextView = (TextView) view.findViewById(R.id.textview_number);
			holder.expTextView = (TextView) view.findViewById(R.id.textview_exp);
			holder.cvcTextView = (TextView) view.findViewById(R.id.textview_cvc);
			view.setTag(holder);
			
			return view;
		}
		
		return view;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup list) 
	{
		Log.i(TAG, "Item position: " + position);
		
		if(convertView == null)
		{
			convertView = createListItemView(inflater, getItemViewType(position));
		}
		
		if(getItemViewType(position) == ITEM_TYPE_HEADER)
		{
			TextView header = (TextView)convertView;
			header.setText( ((String)getItem(position)).toUpperCase() );
			
			if(header.getText().toString().equalsIgnoreCase("custom card"))
				header.setBackgroundColor(0xFFAAAAAA);
			else if(header.getText().toString().equalsIgnoreCase("valid cards"))
				header.setBackgroundColor(0xFF66FF66);
			else if(header.getText().toString().equalsIgnoreCase("invalid cards"))
				header.setBackgroundColor(0xFFFF6666);
		}
		else if(getItemViewType(position) == ITEM_TYPE_CARD)
		{
			Log.i(TAG, "convertView: " + convertView);
			holder = (Holder)convertView.getTag();
			
			if(position > 1)
			{
				cardTest = (StripeCardTest)getItem(position);
				month = cardTest.getCard().getExpMonth();
				monthOrBlank = month == null ? "__" : String.format("%02d", month);
				year = cardTest.getCard().getExpYear();
				yearOrBlank = year == null ? "__" : year + "";
				cvc = cardTest.getCard().getCVC();
				
				Log.i(TAG, "Test: " + cardTest + ", card: " + cardTest.getCard() + ", type: " + cardTest.getCard().getType() + ", textview: " + holder.typeTextView);
				holder.typeTextView.setText(cardTest.getCard().getType());
				holder.numberTextView.setText( CardNumberFormatter.format(cardTest.getCard().getNumber(), false) );
				holder.expTextView.setText(String.format("%s / %s", monthOrBlank, yearOrBlank));
				holder.cvcTextView.setText(cvc == null || cvc.length() == 0 ? "____" : cvc);
			}
			else
			{
				// Custom card item
				holder.typeTextView.setText("Custom");
				holder.numberTextView.setText("xxxx-xxxx-xxxx-xxxx");
				holder.expTextView.setText("__ / __");
				holder.cvcTextView.setText("____");
			}
		}
		
		return convertView;
	}

	
	// Holder pattern to improve list performance
	private class Holder
	{
		public TextView typeTextView;
		public TextView numberTextView;
		public TextView expTextView;
		public TextView cvcTextView;
	}
}
