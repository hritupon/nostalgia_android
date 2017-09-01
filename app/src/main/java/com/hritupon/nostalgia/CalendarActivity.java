package com.hritupon.nostalgia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import me.nlmartian.silkcal.DatePickerController;
import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.SimpleMonthAdapter;

public class CalendarActivity extends Fragment implements DatePickerController {

    public static final String STORIES = "Stories";
    private static final int TOTAL_ITEM_EACH_LOAD = 5;
    private List<Story> stories = new ArrayList<>();
    private int currentPage = 0;
    private long oldestStoryId;

    private DayPickerView calendarView;
    private TextView selectedDateTextView;
    private RecyclerView recyclerView;
    private DateWiseListItemAdapter adapter;
    private ProgressBar mProgressBar;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.activity_calendar, container, false);
        calendarView = (DayPickerView)view.findViewById(R.id.calendar_view);
        calendarView.setController(this);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbarDateItemList);
        recyclerView = (RecyclerView) view.findViewById(R.id.date_story_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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
        return view;
    }

    @Override
    public void onStart()  {
        super.onStart();
        //selectedDateTextView = (TextView)findViewById(R.id.selected_calendar_date_id);

        loadData();

    }


    @Override
    public int getMaxYear() {
        return 0;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        final String dateString = day+"/"+month+"/"+year;
        Toast.makeText(getActivity(),"Showing Your Stories of "+dateString,Toast.LENGTH_SHORT).show();
        stories.clear();
        final long startOfDay=getStartOfDay(year,month,day);
        final long endOfDay=getEndOfDay(year,month,day);
        Query query = FirebaseDatabase.getInstance().getReference(STORIES).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .startAt(startOfDay)
                .endAt(endOfDay)
                .orderByChild("timeStamp");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Story> tempStories=new ArrayList<Story>();
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    Story story =storySnapshot.getValue(Story.class);
                    if(story.getTimeStamp()>=startOfDay && story.getTimeStamp()<=endOfDay){
                        tempStories.add(story);
                    }

                }
                if(tempStories.size()==0){
                    Toast.makeText(getActivity(), "You added no stories on "+dateString, Toast.LENGTH_SHORT).show();
                }else{
                    Collections.reverse(tempStories);
                    stories.addAll(tempStories);
                    if (null != adapter) adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //selectedDateTextView.setText(dateString);
    }

    public long getEndOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }

    public long getStartOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    public long getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }

    public long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }
    private void loadData() {

        long startOfDay=getStartOfDay();
        long endOfDay=getEndOfDay();

        Query query=null;
        if(currentPage==0) {
            query = FirebaseDatabase.getInstance().getReference(STORIES).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .startAt(startOfDay)
                    .endAt(endOfDay)
                    .orderByChild("timeStamp");
        }else{
            query = FirebaseDatabase.getInstance().getReference(STORIES).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .startAt(startOfDay)
                    .endAt(oldestStoryId)
                    .orderByChild("timeStamp");

        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(getActivity(), "No more Stories", Toast.LENGTH_SHORT).show();
                    currentPage--;
                }
                long currentOldestStoryId=oldestStoryId;
                List<Story> tempStories=new ArrayList<Story>();
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    Story story =storySnapshot.getValue(Story.class);
                    tempStories.add(story);
                }
                if(currentPage>0){
                    if(tempStories.size()>0)tempStories.remove(tempStories.size()-1);
                }
                if(tempStories.size()==0){
                    Toast.makeText(getActivity(), "No more Stories", Toast.LENGTH_SHORT).show();
                }else {
                    Collections.reverse(tempStories);
                    oldestStoryId = tempStories.get(tempStories.size()-1).getTimeStamp();
                    if(currentOldestStoryId==oldestStoryId){
                        Toast.makeText(getActivity(), "No more Stories", Toast.LENGTH_SHORT).show();
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