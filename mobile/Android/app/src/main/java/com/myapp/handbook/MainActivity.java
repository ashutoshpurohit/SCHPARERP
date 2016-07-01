package com.myapp.handbook;

import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.myapp.handbook.data.HandBookDbHelper;

public class MainActivity extends AppCompatActivity {
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ShareActionProvider shareActionProvider;
    private String[] titles;
    private static final String TAG = "MyActivity";
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mDownstreamBroadcastReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteOpenHelper notificationHelper = new HandBookDbHelper(this);
        Notifications.setDb(notificationHelper.getWritableDatabase());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = getResources().getStringArray(R.array.titles);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawerList = (ListView)findViewById(R.id.drawer);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Populate the ListView
        /*drawerList.setAdapter(new ArrayAdapter<String>(this,
                                                       android.R.layout.simple_list_item_activated_1, titles));*/
        drawerList.setAdapter(new CustomNavAdapter(
                this,
                titles,
                new Integer[]{
                        R.drawable.ic_action_name,
                        R.drawable.ic_action_name,
                        R.drawable.ic_action_name,
                        R.drawable.ic_action_name,
                        R.drawable.ic_action_name

                }));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        //Display the correct fragment.
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position",0);
            //setActionBarTitle(currentPosition);
            myToolbar.setTitle("Handbook");
            selectItem(currentPosition);
        } else {
            Intent intent =getIntent();
            if(intent!=null)
            {
                int type = intent.getIntExtra("requestType",0);
                if(type==HttpConnectionUtil.GCM_NOTIFICATION)
                    currentPosition=1;
                else
                    currentPosition=0;
                selectItem(currentPosition);
            }
            else {
                selectItem(0);
            }
        }
        //Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                                                 R.string.open_drawer, R.string.close_drawer) {
                //Called when a drawer has settled in a completely closed state
                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                }
                //Called when a drawer has settled in a completely open state.
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }
            };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        android.support.v4.app.FragmentManager fragMan = getSupportFragmentManager();
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                        if (fragment instanceof TopFragment) {
                            currentPosition = 0;
                        }
                        if (fragment instanceof NotesFragment) {
                            currentPosition = 1;
                        }
                        if (fragment instanceof PastaFragment) {
                            currentPosition = 2;
                        }
                        if (fragment instanceof StudentFeedbackFragment) {
                            currentPosition = 3;
                        }
                        if (fragment instanceof TeacherNoteFragment) {
                            currentPosition = 4;
                        }

                        setActionBarTitle(currentPosition);
                        drawerList.setItemChecked(currentPosition, true);
                    }
                }
        );
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                  //  mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                   // mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
     registerReceiver();
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        getSupportActionBar().setElevation(0f);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        currentPosition = position;
        Fragment fragment;
        switch(position) {
        case 1:
            fragment = new NotesFragment();
            break;
        case 2:
            fragment = new PastaFragment();
            break;
        case 3:
            fragment = new StudentFeedbackFragment();
            break;
        case 4:
            fragment = new TeacherNoteFragment();
            break;
        default:
            fragment = new TopFragment();
        }
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.replace(R.id.main_fragment, fragment, "visible_fragment");
        ft.addToBackStack(null);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Set the action bar title
        setActionBarTitle(position);
        //Close drawer
        drawerLayout.closeDrawer(drawerList);
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                //Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
       /* MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setIntent("This is example text");*/
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_settings:
            //Code to run when the settings item is clicked
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
