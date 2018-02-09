package com.myapp.handbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    public static final String MESSAGE_TYPE = "messageType";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    //  private int messageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        DiaryNotesFragment diaryNotesFragment = new DiaryNotesFragment();
        HomeWorkNotesFragment homeWorkNotesFragment = new HomeWorkNotesFragment();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        int messageType = intent.getIntExtra(MESSAGE_TYPE, 1);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, messageType);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (toolbar != null)
            toolbar.setTitle("School Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            int type = b.getInt(MESSAGE_TYPE);
            Log.d("NotesActivity", "Message type is" + type);
        }

    }


    private void setupViewPager(ViewPager viewPager, int messageType) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DiaryNotesFragment(), "Diary Notes");
        adapter.addFragment(new HomeWorkNotesFragment(), "Home Work");

        viewPager.setAdapter(adapter);
        if (messageType == HttpConnectionUtil.HOMEWORK_TYPE) {
            viewPager.setCurrentItem(1);
        } else if (messageType == HttpConnectionUtil.DIARY_NOTE_TYPE) {
            viewPager.setCurrentItem(0);
        }

    }

    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            LaunchNotesFragment();
        }


        return super.onOptionsItemSelected(item);
    }

    private void LaunchNotesFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        //Bundle extras = intent.getExtras();
        intent.putExtra("position", 1);
        startActivity(intent);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
