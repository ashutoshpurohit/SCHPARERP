package com.myapp.handbook.Tasks;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.handbook.Listeners.SelectionChangeListener;
import com.myapp.handbook.R;
import com.myapp.handbook.domain.RoleProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SAshutosh on 12/29/2016.
 */

public class UpdateNavigationViewHeader implements SelectionChangeListener {

    private final List<RoleProfile> roles;
    private final NavigationView navigationView;
    private final Context context;

    public UpdateNavigationViewHeader(List<RoleProfile> roleProfiles, NavigationView view, Context ctx)
    {
        this.roles = roleProfiles;
        this.navigationView= view;
        this.context = ctx;
    }
    @Override
    public void onSelectionChanged(String selectedProfileId) {
        updateNavViewHeader(selectedProfileId,roles,navigationView,context);
    }
    private void updateNavViewHeader(String selectedProfileId, List<RoleProfile> roles, NavigationView navigationView, Context context) {

        NavigationView navView =  navigationView;
        if(roles!=null && roles.size() > 0)
        {
            View header= navView.getHeaderView(0);
            TextView schoolName = (TextView) header.findViewById(R.id.schoolName);
            ImageView profileImage = (ImageView)header.findViewById(R.id.school_logo);
            TextView email = (TextView)header.findViewById(R.id.email);
            RoleProfile selectedProfile = findProfileById(roles,selectedProfileId);

            if(selectedProfile!=null) {
                String imagePath=selectedProfile.getImageUrl();
                if(imagePath==null|| TextUtils.isEmpty(imagePath.trim())) {
                    Picasso.with(context)
                            .load(R.drawable.contact_picture_placeholder)
                            .placeholder(R.drawable.contact_picture_placeholder)
                            //.networkPolicy(NetworkPolicy.OFFLINE)
                            .into(profileImage);
                }
                else {

                    Picasso.with(context)
                            .load(selectedProfile.getImageUrl())
                            .placeholder(R.drawable.contact_picture_placeholder)
                            .into(profileImage);
                }
                schoolName.setText(selectedProfile.getFirstName() + " " + selectedProfile.getLastName());
                email.setText(selectedProfile.getStd());
            }

        }

    }

    private RoleProfile findProfileById(List<RoleProfile> roles, String selectedProfileId) {
        RoleProfile profile =null;
        for(int i=0;i<roles.size();i++){
            if(roles.get(i).getId().equals(selectedProfileId)){
                profile= roles.get(i);
                break;
            }

        }
        return  profile;
    }

}
