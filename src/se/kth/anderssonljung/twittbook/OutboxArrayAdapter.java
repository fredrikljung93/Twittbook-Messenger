package se.kth.anderssonljung.twittbook;

import se.kth.anderssonljung.twittbook.entities.Message;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OutboxArrayAdapter extends ArrayAdapter<Message> {
	private final Context context;
	private final Message[] values;

	public OutboxArrayAdapter(Context context, Message[] values) {
		super(context, R.layout.outboxrowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater
				.inflate(R.layout.outboxrowlayout, parent, false);
		TextView textView = (TextView) rowView
				.findViewById(R.id.outboxrowlayoutTextview);
		textView.setText(values[position].getReceiver() + ": "
				+ values[position].getSubject());
		return rowView;
	}

}
