package com.shuwang.yuanditucao;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TuCaoAdapter extends ArrayAdapter<TuCao> {
	private class ViewHolder{
		public TextView contentView;
		
	}
	
	
	private final Context context;
	public TuCaoAdapter(Context context, List<TuCao> items) {
		super(context, R.layout.brief_row,items);//<----要把数组传入构造函数，这样下面的getItem才能工作
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		TuCao item = this.getItem(position);
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.brief_row, parent, false);
			holder = new ViewHolder();
			holder.contentView = (TextView)convertView.findViewById(R.id.textViewContent);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.contentView.setText(item.getContent());
		return convertView;
	}
	
}
