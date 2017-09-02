package com.hritupon.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hritupon.nostalgia.models.Story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabbedViewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FloatingActionButton floatingActionButton;
    StoriesActivity storiesActivity;
    CalendarActivity calendarActivity;
    String searchText;
    List<Story> searchResults;
    public static final String STORIES = "Stories";

    public int[] tabIcons = {
            R.drawable.ic_apps_white_24dp,
            R.drawable.ic_date_range_white_24dp

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_view);
        toolbar = (Toolbar)findViewById(R.id.tabbed_toolbar);
        setSupportActionBar(toolbar);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager());

        storiesActivity = new StoriesActivity();
        viewPagerAdapter.addFragments(storiesActivity, "");

        viewPagerAdapter.addFragments(new CalendarActivity(), "");
        calendarActivity = new CalendarActivity();

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TabbedViewActivity.this, SpeechRecordActivity.class));
            }
        });
        setupTabIcons();

    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stories_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        viewPager.setCurrentItem(0);
        searchText = newText.toLowerCase();
        searchResults= new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference(STORIES).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("timeStamp");

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Story> tempStories=new ArrayList<Story>();
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    Story story =storySnapshot.getValue(Story.class);
                    if(story.getDescription().contains(searchText)){
                        tempStories.add(story);
                    }
                }
                if(tempStories.size()==0){
                    Toast.makeText(TabbedViewActivity.this, "No search results found.", Toast.LENGTH_SHORT).show();
                }else{
                    Collections.reverse(tempStories);
                    searchResults.addAll(tempStories);
                    StoriesListAdapter storiesListAdapter = storiesActivity.getAdapter();
                    if(null != storiesListAdapter)storiesListAdapter.setFilter(searchResults);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }
}
