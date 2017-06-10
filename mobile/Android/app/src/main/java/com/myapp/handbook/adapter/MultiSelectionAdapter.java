package com.myapp.handbook.adapter;

/**
 * Created by sashutosh on 7/4/2016.
 */


import java.util.ArrayList;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.R;


public class MultiSelectionAdapter<T> extends BaseAdapter {


    Context mContext;
    LayoutInflater mInflater;
    ArrayList<T> mList;
    SparseBooleanArray mSparseBooleanArray;

    OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
        }
    };

    public void changeAllItemCheckedState(boolean state){

        for (int i = 0; i < mList.size(); i++) {
            mSparseBooleanArray.put(i,state);
        }
        notifyDataSetChanged();
    }



    public MultiSelectionAdapter(Context context, ArrayList<T> list) {

        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mSparseBooleanArray = new SparseBooleanArray();
        mList = new ArrayList<T>();
        this.mList = list;
    }

    public MultiSelectionAdapter(Context context, ArrayList<T> list, ArrayList<T> selectedItemList){
        this(context,list);
        //Logic to put items from selectedItem list into checked state
        if(selectedItemList!=null && selectedItemList.size()>0)
        {
            for(int i=0;i<selectedItemList.size();i++)
            {
                RoleProfile currentSelectedItem = (RoleProfile) selectedItemList.get(i);
                for(int j=0;j<list.size();j++)
                {
                    RoleProfile currentItem = (RoleProfile) list.get(j);
                    if(currentSelectedItem.getId().equals(currentItem.getId())){
                        mSparseBooleanArray.put(j,true);
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<T> getCheckedItems() {
        ArrayList<T> mTempArry = new ArrayList<T>();
        for (int i = 0; i < mList.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                mTempArry.add(mList.get(i));
            }
        }
        return mTempArry;
    }

    @Override


    public int getCount() {
        return mList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_student_search, null);
        }
        TextView studentName = (TextView) convertView.findViewById(R.id.search_studentName);
        RoleProfile studentProfile = (RoleProfile) mList.get(position);
        String studentFullName = studentProfile.getFirstName();
        studentName.setText(studentFullName);
//        String studentId = studentProfile.getId();
//
//        TextView id = (TextView) convertView.findViewById(R.id.search_studentId);
//        id.setText(studentId);

        CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.chkEnable);
        mCheckBox.setTag(position);
        mCheckBox.setChecked(mSparseBooleanArray.get(position));
        mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        return convertView;


    }


}
