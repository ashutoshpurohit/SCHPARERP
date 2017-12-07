package com.myapp.handbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.Listeners.ProfileImageClickListener;
import com.myapp.handbook.Listeners.SelectionChangeListener;
import com.myapp.handbook.R;
import com.myapp.handbook.TopFragment;
import com.myapp.handbook.domain.RoleProfile;

import java.util.List;

/**
 * Created by SAshutosh on 10/9/2016.
 */

public class ProfileAdapter extends ArrayAdapter<RoleProfile> {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    Context context;
    int layoutResourceId;
    RoleProfile [] roles=null;
    String selectedProfileId;
    List<SelectionChangeListener> listeners;
    int selectedPosition;
    ImageView updatedProfileImage;
    String userChoosenTask;
    TopFragment myFragment;
    List<ProfileImageClickListener> mProfileImageClickListeners;

    /**
     * Constructor
     *
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param roles  The objects to represent in the ListView.
     */
    public ProfileAdapter(Context context, int resource, RoleProfile[] roles,
                          List<SelectionChangeListener> selectionChangeListenerList, List<ProfileImageClickListener> profileImageClickListeners) {
        super(context, resource, roles);
        this.context=context;
        this.layoutResourceId=resource;
        this.roles=roles;



        this.listeners=selectionChangeListenerList;
        selectedProfileId = HttpConnectionUtil.getSelectedProfileId();
        for (SelectionChangeListener listener:listeners
             ) {
            listener.onSelectionChanged(selectedProfileId);

        }
        mProfileImageClickListeners = profileImageClickListeners;

    }

    public void setRoles(RoleProfile[] roles) {
        this.roles = roles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View currentRow =convertView;

        if(currentRow==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            currentRow = inflater.inflate(layoutResourceId,parent,false);
            /*currentRow.setOnClickListener(this);*/

        }
        final RoleProfile profile = roles[position];

        TextView profileName = (TextView) currentRow.findViewById(R.id.profile_name);
        TextView profileRole = (TextView) currentRow.findViewById(R.id.profile_role);

        TextView profileStd = (TextView) currentRow.findViewById(R.id.profile_standard);
        TextView profileContactNumber = (TextView) currentRow.findViewById(R.id.profile_contact_number);
        final TextView profileId = (TextView) currentRow.findViewById(R.id.profileId);
        ImageView profileImage = (ImageView) currentRow.findViewById(R.id.profile_image);
        RadioButton selectedIdRadio = (RadioButton) currentRow.findViewById(R.id.radio_select_profile);


        String imagePath = profile.getImageUrl();
        if (imagePath == null || TextUtils.isEmpty(imagePath.trim())) {
            Glide.with(getContext())
                    .load(R.drawable.ic_add_a_photo)
                    //.placeholder(R.drawable.contact_picture_placeholder)

                    .into(profileImage);
        } else {
            Glide.with(getContext())
                    .load(profile.getImageUrl())
                    //.placeholder(R.drawable.contact_picture_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImage);
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (ProfileImageClickListener listener : mProfileImageClickListeners
                        ) {
                    listener.onProfileImageClicked(profile.getId());
                    //notifyDataSetChanged();
                }
//                notifyDataSetChanged();

            }

        });



        selectedIdRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = (Integer) view.getTag();
                selectedProfileId = profileId.getText().toString();
                HttpConnectionUtil.setSelectedProfileId(selectedProfileId);
                for (SelectionChangeListener listener : listeners
                        ) {
                    listener.onSelectionChanged(selectedProfileId);

                }
                notifyDataSetChanged();
            }
        });

        if (profile.getId().equals(selectedProfileId)) {
            selectedIdRadio.setChecked(true);

        } else {
            selectedIdRadio.setChecked(false);
        }
        selectedIdRadio.setTag(position);

        if(profile.getLastName()!=null)
            profileName.setText(profile.getFirstName()+ " "+ profile.getLastName());
        else
            profileName.setText(profile.getFirstName());
        profileRole.setText(profile.getRole());
        profileId.setText(profile.getId());

        profileContactNumber.setText(HttpConnectionUtil.getMobileNumber());
        String tempProfile = profile.getRole();
        if(tempProfile.equals("TEACHER"))
        {
            profileStd.setVisibility(View.GONE);

        }else {
            profileStd.setVisibility(View.VISIBLE);
            profileStd.setText(profile.getStd());
        }

        /* if(profile.getId().equals(selectedProfileId)){
            currentRow.setBackgroundColor(Color.GRAY);
        }
        else{
            currentRow.setBackgroundColor(Color.WHITE);
        }*/
        return currentRow;
    }

    /**
     * Called when a view has been clicked.
     *
     *  The view that was clicked.
     */
/*
    @Override
    public void onClick(View v) {
        TextView profileId = (TextView)v.findViewById(R.id.profileId);
        selectedProfileId = profileId.getText().toString();
        HttpConnectionUtil.setSelectedProfileId(selectedProfileId);
        for (SelectionChangeListener listener:listeners
             ) {
            listener.onSelectionChanged(selectedProfileId);

        }

        notifyDataSetChanged();
    }
*/


}
