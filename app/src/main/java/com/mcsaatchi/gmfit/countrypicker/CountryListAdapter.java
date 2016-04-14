package com.mcsaatchi.gmfit.countrypicker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class CountryListAdapter extends BaseAdapter {

	private Context context;
	List<Country> countries;
	LayoutInflater inflater;

	/**
	 *
	 * http://stackoverflow.com/
	 * questions/3042961/how-can-i-get-the-resource-id-of
	 * -an-image-if-i-know-its-name
	 *
	 */
	private int getResId(String drawableName) {

		try {
			Class<R.drawable> res = R.drawable.class;
			Field field = res.getField(drawableName);
			int drawableId = field.getInt(null);
			return drawableId;
		} catch (Exception e) {
			Log.e("COUNTRYPICKER", "Failure to get drawable id.", e);
		}
		return -1;
	}


	public CountryListAdapter(Context context, List<Country> countries) {
		super();
		this.context = context;
		this.countries = countries;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return countries.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cellView = convertView;
		Cell cell;
		Country country = countries.get(position);

		if (convertView == null) {
			cell = new Cell();
			cellView = inflater.inflate(R.layout.country_picker_row, null);
			cell.textView = (TextView) cellView.findViewById(R.id.row_title);
			cell.imageView = (ImageView) cellView.findViewById(R.id.row_icon);
			cellView.setTag(cell);
		} else {
			cell = (Cell) cellView.getTag();
		}

		cell.textView.setText(country.getName());

		// Load drawable dynamically from country code
		String drawableName = "flag_"
				+ country.getCode().toLowerCase(Locale.ENGLISH);
		cell.imageView.setImageResource(getResId(drawableName));
		return cellView;
	}

	static class Cell {
		public TextView textView;
		public ImageView imageView;
	}

}