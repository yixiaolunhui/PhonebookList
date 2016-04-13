package com.dalong.phonebooklist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dalong.phonebooklist.R;
import com.dalong.phonebooklist.entity.Contact;
import com.dalong.phonebooklist.view.PinnedSectionListView;

import java.util.List;

/**
 * Created by zhouweilong on 16/4/13.
 */
public class PhoneBookAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

    public Context context;
    public List<Contact>  mList;

    public PhoneBookAdapter(Context context, List<Contact> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void changeData(List<Contact> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType==0?true:false;
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).isTitle()){
            return 0;
        }else{
            return 1;
        }

    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.view_phone_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.mTitle=(TextView)convertView.findViewById(R.id.id_phone_title);
            viewHolder.mName=(TextView)convertView.findViewById(R.id.id_phone_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Contact mContact=mList.get(position);
        if(mContact.isTitle()){
            viewHolder.mTitle.setVisibility(View.VISIBLE);
            viewHolder.mName.setVisibility(View.GONE);
            viewHolder.mTitle.setText(mContact.getNameSort());
        }else {
            viewHolder.mTitle.setVisibility(View.GONE);
            viewHolder.mName.setVisibility(View.VISIBLE);
            viewHolder.mName.setText(mContact.getBookMarkName());
        }

        return convertView;
    }


    class ViewHolder {
        TextView mTitle;
        TextView mName;
    }

}
