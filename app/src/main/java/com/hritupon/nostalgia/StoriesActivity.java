package com.hritupon.nostalgia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
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

public class StoriesActivity extends AppCompatActivity {

    public static final String STORIES = "Stories";
    private static final int TOTAL_ITEM_EACH_LOAD = 5;
    private int currentPage = 0;
    private Toolbar mToolbar;
    RecyclerView recyclerViewStories;
    private ProgressBar mProgressBar;
    LinearLayoutManager layoutManager;
    List<Story> storyList = new ArrayList<>();;
    StoriesListAdapter adapter;
    String oldestStoryId ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        mToolbar = (Toolbar)findViewById(R.id.stories_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        recyclerViewStories = (RecyclerView)findViewById(R.id.listview_stories);
        adapter = new StoriesListAdapter(StoriesActivity.this, storyList);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewStories.setLayoutManager(layoutManager);
        recyclerViewStories.setItemAnimator(new DefaultItemAnimator());
        recyclerViewStories.setAdapter(adapter);
        recyclerViewStories.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) { // when we have reached end of RecyclerView this event fired
                loadMoreData();
            }
        });
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.stories_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;
            case R.id.homeAsUp:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.stories_activity_menu_calendar_view:{
                Toast.makeText(StoriesActivity.this,"Switch to calendar view", Toast.LENGTH_SHORT).show();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void loadData() {
        Query query=null;
        if(currentPage==0) {
            query = FirebaseDatabase.getInstance().getReference(STORIES).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .limitToLast(TOTAL_ITEM_EACH_LOAD)
                    .orderByChild("timeStamp");
        }else{
            query = FirebaseDatabase.getInstance().getReference(STORIES).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .limitToLast(TOTAL_ITEM_EACH_LOAD)
                    .endAt(oldestStoryId)
                    .orderByChild("timeStamp");

        }
        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChildren()){
                            Toast.makeText(StoriesActivity.this, "No more Stories", Toast.LENGTH_SHORT).show();
                            currentPage--;
                        }
                        String currentOldestStoryId=oldestStoryId;
                        List<Story> tempStories=new ArrayList<Story>();
                        for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                            Story story =storySnapshot.getValue(Story.class);
                            tempStories.add(story);
                        }
                        if(currentPage>0){
                            if(tempStories.size()>0)tempStories.remove(tempStories.size()-1);
                        }
                        if(tempStories.size()==0){
                            Toast.makeText(StoriesActivity.this, "No more Stories", Toast.LENGTH_SHORT).show();
                        }else {
                            Collections.reverse(tempStories);
                            oldestStoryId = tempStories.get(tempStories.size()-1).getTimeStamp();
                            if(currentOldestStoryId.equals(oldestStoryId)){
                                Toast.makeText(StoriesActivity.this, "No more Stories", Toast.LENGTH_SHORT).show();
                                //currentPage--;
                            }else {
                                storyList.addAll(tempStories);
                                if (null != adapter) adapter.notifyDataSetChanged();
                            }
                        }
                        mProgressBar.setVisibility(RecyclerView.GONE);

                    }

                    @Override public void onCancelled(DatabaseError databaseError) {
                        mProgressBar.setVisibility(RecyclerView.GONE);
                    }
                });
    }

    private void loadMoreData(){
        currentPage++;
        loadData();
        mProgressBar.setVisibility(RecyclerView.VISIBLE);
    }
}
