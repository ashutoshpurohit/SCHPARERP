package com.myapp.handbook;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;

import io.fabric.sdk.android.Fabric;

import static android.support.v7.app.AppCompatDelegate.FEATURE_ACTION_MODE_OVERLAY;

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
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mDownstreamBroadcastReceiver;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteOpenHelper notificationHelper = new HandBookDbHelper(this);
        Notifications.setDb(notificationHelper.getWritableDatabase());
        //notificationHelper.onCreate(notificationHelper.getWritableDatabase());
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        this.requestWindowFeature(FEATURE_ACTION_MODE_OVERLAY);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        HttpConnectionUtil.setSharedPreferences(sharedPreferences);
        titles = getResources().getStringArray(R.array.titles);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Check if logged in

        if(sharedPreferences.getBoolean(QuickstartPreferences.LOGGED_IN, false) == false){
            //Launch the digits app
            Intent intent = new Intent(getBaseContext(),com.myapp.handbook.login.Login.class);
            startActivity(intent);
            return;
        }
        else{
            String mobileNumber = sharedPreferences.getString(QuickstartPreferences.LOGGED_MOBILE,"");
            //If valid mobile
            if(mobileNumber.length()>0)
            {
                HttpConnectionUtil.setMobilenumber(mobileNumber);
            }
            else{
                //Redirect user back to login Screen
                Intent intent = new Intent(getBaseContext(),com.myapp.handbook.login.Login.class);
                startActivity(intent);
            }
        }

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position",0);
            //setActionBarTitle(currentPosition);
            myToolbar.setTitle("Handbook");
            myToolbar.setTitleTextColor(Color.WHITE);
            //selectItem(currentPosition);

        } else {

            currentPosition=0;
            //selectItem(0);

        }
        Intent fragmentIntent =getIntent();
        currentPosition= fragmentIntent.getIntExtra("position",0);
        selectItem(currentPosition);


        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Intent intent;

                Fragment fragment=null;
                android.support.v4.app.FragmentTransaction fragmentTransaction=null;
                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        fragment = new HomeFragment();
                        ((HomeFragment)fragment).setNavigationView(navigationView);
                        currentPosition=0;
                        break;
                    case R.id.profiles:
                        fragment = new TopFragment();
                        ((TopFragment)fragment).setNavigationView(navigationView);
                        currentPosition=7;
                        break;
                    case R.id.notifications:
                        Toast.makeText(getApplicationContext(),"Stared Selected",Toast.LENGTH_SHORT).show();
                        fragment = new NotesFragment();
                        currentPosition=1;
                        break;
                    case R.id.schoolCalendar:
                        //Toast.makeText(getApplicationContext(),"Send Selected",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext() , CalendarEventsActivity.class);
                        //Can pass student/teacherid from here
                        //intent.putExtra("ID",rowId);
                        startActivity(intent);

                        return true;
                    case R.id.feedback:

                        if(RoleProfile.getProfile(HttpConnectionUtil.getProfiles(),
                                HttpConnectionUtil.getSelectedProfileId()).getProfileRole()== RoleProfile.ProfileRole.STUDENT) {
                            fragment = new StudentFeedbackFragment();
                            currentPosition=3;
                        }
                        else {
                            fragment = new TeacherNoteFragment();
                            currentPosition=4;
                        }

                        break;

                    case R.id.contactSchool:
                        Toast.makeText(getApplicationContext(),"Contact School Selected",Toast.LENGTH_SHORT).show();
                        fragment = new SchoolContactFragment();
                        currentPosition=5;
                        break;

                    case R.id.timetable:
                        //Toast.makeText(getApplicationContext(),"Timetable Selected",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext() ,TimeTableActivity.class);
                        //Can pass student/teacherid from here
                        //intent.putExtra("ID",rowId);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        Toast.makeText(getApplicationContext(),"Logging out",Toast.LENGTH_SHORT).show();
                        Logout();

                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        break;
                }
                if(fragment!=null) {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();


                }
                setActionBarTitle(currentPosition);
                return true;

            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,myToolbar,R.string.open_drawer, R.string.close_drawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);

            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        android.support.v4.app.FragmentManager fragMan = getSupportFragmentManager();
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                        if (fragment instanceof TopFragment) {
                            currentPosition = 7;
                        }

                        if(fragment instanceof HomeFragment ){
                            currentPosition= 0;
                        }
                        if (fragment instanceof NotesFragment) {
                            currentPosition = 1;
                        }

                        if (fragment instanceof StudentFeedbackFragment) {
                            currentPosition = 3;
                        }
                        if (fragment instanceof TeacherNoteFragment) {
                            currentPosition = 4;
                        }
                        if(fragment instanceof SchoolContactFragment){
                            currentPosition=5;
                        }

                        setActionBarTitle(currentPosition);
                        //drawerList.setItemChecked(currentPosition, true);
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

    private void Logout() {

        sharedPreferences.edit().putBoolean(QuickstartPreferences.LOGGED_IN, false).apply();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent){
        Bundle b = intent.getExtras();
        int type = b.getInt("requestType");
        if(type==HttpConnectionUtil.GCM_NOTIFICATION) {
            currentPosition = 1;
            selectItem(currentPosition);
        }
        else if(type==HttpConnectionUtil.EVENT_NOTIFICATION){
            //Launch event activity
            Intent newIntent = new Intent(getApplicationContext() , CalendarEventsActivity.class);
            //Can pass student/teacherid from here
            //intent.putExtra("ID",rowId);
            startActivity(newIntent);

        }
        else if(type==HttpConnectionUtil.TIMETABLE_NOTIFICATION){
            //Launch timetable activity
            Intent newIntent = new Intent(getApplicationContext() ,TimeTableActivity.class);
            //Can pass student/teacherid from here
            //intent.putExtra("ID",rowId);
            startActivity(newIntent);

        }
        else {
            currentPosition = 0;
            selectItem(currentPosition);
        }


    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        currentPosition = position;
        Fragment fragment;
        switch(position) {
        case 1:
            fragment = new NotesFragment();

            break;

        case 3:
            //Route to teacher or student
            if(RoleProfile.getProfile(HttpConnectionUtil.getProfiles(),HttpConnectionUtil.getSelectedProfileId()).getProfileRole()== RoleProfile.ProfileRole.STUDENT)
                 fragment = new StudentFeedbackFragment();
            else
                fragment = new TeacherNoteFragment();

            break;

        case 5:
            fragment = new SchoolContactFragment();
            break;
            case 7:
                fragment = new TopFragment();
                ((TopFragment)fragment).setNavigationView(navigationView);
                break;

        default:
            fragment = new HomeFragment();
            ((HomeFragment)fragment).setNavigationView(navigationView);
        }
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.replace(R.id.frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commitAllowingStateLoss();
        //Set the action bar title
        setActionBarTitle(position);
        //Close drawer
        //drawerLayout.closeDrawer(drawerList);
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
       // boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        //menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(actionBarDrawerToggle!=null)
            actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
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
        if(item.getItemId()== R.id.action_search){
            startSearch(null,false,null,false);
            return true;
        }
        else if(item.getItemId()==R.id.action_refresh){
            SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(this);

            SQLiteDatabase db = handbookDbHelper.getReadableDatabase();
            HttpConnectionUtil.clearAllPreferences(db,sharedPreferences);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result){
        super.onActivityResult(requestCode,resultCode,result);
    }

    boolean doubleBackToExitPressedOnce = false;

/*    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }*/

}
