package com.hritupon.nostalgia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import me.nlmartian.silkcal.DatePickerController;
import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.SimpleMonthAdapter;

public class CalendarActivity extends AppCompatActivity implements DatePickerController {

    public static final String STORIES = "Stories";
    private static final int TOTAL_ITEM_EACH_LOAD = 5;
    private List<Story> stories = new ArrayList<>();
    private int currentPage = 0;
    private String oldestStoryId ="";

    private DayPickerView calendarView;
    private TextView selectedDateTextView;
    private RecyclerView recyclerView;
    private DateWiseListItemAdapter adapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        //selectedDateTextView = (TextView)findViewById(R.id.selected_calendar_date_id);
        calendarView = (DayPickerView) findViewById(R.id.calendar_view);
        calendarView.setController(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbarDateItemList);
        recyclerView = (RecyclerView) findViewById(R.id.date_story_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new DateWiseListItemAdapter(this.stories);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) { // when we have reached end of RecyclerView this event fired
                loadMoreData();
            }
        });
        loadData();

    }


    @Override
    public int getMaxYear() {
        return 0;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        String dateString = day+"/"+month+"/"+year;
        Toast.makeText(CalendarActivity.this,"Showing Your Stories of "+dateString,Toast.LENGTH_SHORT).show();
        //selectedDateTextView.setText(dateString);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

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
                    Toast.makeText(CalendarActivity.this, "No more Stories", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CalendarActivity.this, "No more Stories", Toast.LENGTH_SHORT).show();
                }else {
                    Collections.reverse(tempStories);
                    oldestStoryId = tempStories.get(tempStories.size()-1).getTimeStamp();
                    if(currentOldestStoryId.equals(oldestStoryId)){
                        Toast.makeText(CalendarActivity.this, "No more Stories", Toast.LENGTH_SHORT).show();
                        //currentPage--;
                    }else {
                        stories.addAll(tempStories);
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