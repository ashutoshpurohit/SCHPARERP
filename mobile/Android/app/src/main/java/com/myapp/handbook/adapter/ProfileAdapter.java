package com.myapp.handbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.R;
import com.myapp.handbook.domain.RoleProfile;
import com.squareup.picasso.Picasso;

/**
 * Created by SAshutosh on 10/9/2016.
 */

public class ProfileAdapter extends ArrayAdapter<RoleProfile> implements View.OnClickListener {
    Context context;
    int layoutResourceId;
    RoleProfile [] roles=null;
    String selectedProfileId;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param roles  The objects to represent in the ListView.
     */
    public ProfileAdapter(Context context, int resource, RoleProfile[] roles) {
        super(context, resource, roles);
        this.context=context;
        this.layoutResourceId=resource;
        this.roles=roles;
        selectedProfileId = HttpConnectionUtil.getSelectedProfileId();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View currentRow =convertView;
        if(currentRow==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            currentRow = inflater.inflate(layoutResourceId,parent,false);
            currentRow.setOnClickListener(this);

        }
        ImageView imageView = (ImageView) currentRow.findViewById(R.id.profile_image);
        TextView profileName = (TextView)currentRow.findViewById(R.id.profile_name);
        TextView profileRole = (TextView)currentRow.findViewById(R.id.profile_role);
        TextView profileId = (TextView)currentRow.findViewById(R.id.profileId);
        RoleProfile profile= roles[position];

        profileName.setText(profile.getFirstName()+ " "+ profile.getLastName());
        profileRole.setText(profile.getRole());
        profileId.setText(profile.getId());
        Picasso.with(getContext())
                .load(profile.getImageUrl())
                .placeholder(R.drawable.contact_picture_placeholder)
                .error(R.drawable.contact_picture_error)
                //.networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView);

        if(profile.getId().equals(selectedProfileId)){
            currentRow.setBackgroundColor(Color.GRAY);
        }
        else{
            currentRow.setBackgroundColor(Color.WHITE);
        }
        return currentRow;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        TextView profileId = (TextView)v.findViewById(R.id.profileId);
        selectedProfileId = profileId.getText().toString();
        HttpConnectionUtil.setSelectedProfileId(selectedProfileId);
        notifyDataSetChanged();
    }
}
