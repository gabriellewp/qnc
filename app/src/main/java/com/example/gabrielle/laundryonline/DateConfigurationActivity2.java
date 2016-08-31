package com.example.gabrielle.laundryonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.gabrielle.laundryonline.db.UserAddressDetails;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gabrielle on 7/23/2016.
 */
public class DateConfigurationActivity2 extends AppCompatActivity {
    private CalendarAdapter myCalendarAdapter1,myCalendarAdapter2;
    private HourAdapter myHourAdapter1,myHourAdapter2;
    private RecyclerView rvCalendar1, rvHour1, rvCalendar2, rvHour2;
    private int positionCalendar1,positionHour1, positionCalendar2, positionHour2;
    private Intent intentToConfigDate, intentConfirmSchedule;
    private LinearLayout calendar2Layout, hour2Layout;
    private TextView schedule1TextView, schedule2TextView;
    private String schedule1Date, schedule1Hour,schedule2Date,schedule2Hour,scheduleDate1FDStr, scheduleDate2FDStr;
    private ImageButton prevButton, nextButton;
    ArrayList<CustomDate> dates;
    ArrayList<Integer> hours;
    //int firstCalendar1, lastCalendar1, firstHour1, lastHour1, firstCalendar2, lastCalendar2, firstHour2, lastHour2;
    int first, last;
    private Switch mySwitch;
    private HashMap<String, String> hashMap;
    /**
     * Created by gabrielle on 7/21/2016.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dateconf2);
        intentToConfigDate = getIntent();
        intentConfirmSchedule = new Intent(this,ConfirmScheduleActivity.class);
        hashMap = (HashMap<String, String>)intentToConfigDate.getSerializableExtra("map");
       // Log.v("HashMapTest", hashMap.get("key")); //label,address,detail
        getDates();
        getHours();
        positionCalendar1 = 0; positionHour1 = 0; positionCalendar2 = 0; positionHour2 = 0;
        rvCalendar1 = (RecyclerView) findViewById(R.id.pager);
        rvHour1 = (RecyclerView) findViewById(R.id.pagerhour1);
        rvCalendar2 = (RecyclerView) findViewById(R.id.pager2);
        rvHour2 = (RecyclerView) findViewById(R.id.pagerhour2);
        myCalendarAdapter1 = new CalendarAdapter(dates);
        myCalendarAdapter2 = new CalendarAdapter(dates);
        myHourAdapter1 = new HourAdapter(hours);
        myHourAdapter2 = new HourAdapter(hours);
        calendar2Layout =(LinearLayout) findViewById(R.id.calendar2layout);
        schedule1TextView = (TextView) findViewById(R.id.schedule1);
        schedule2TextView =(TextView) findViewById(R.id.schedule2);
        hour2Layout = (LinearLayout) findViewById(R.id.hour2layout);
        prevButton = (ImageButton) findViewById(R.id.prev_button);
        nextButton = (ImageButton) findViewById(R.id.next_button);
        final LinearLayoutManager llmCalendar1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //llmCalendar1.setOrientation(LinearLayoutManager.VERTICAL);
        final LinearLayoutManager llmHour1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //llmHour1.setOrientation(LinearLayoutManager.VERTICAL);
        final LinearLayoutManager llmCalendar2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //llmCalendar2.setOrientation(LinearLayoutManager.VERTICAL);
        final LinearLayoutManager llmHour2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //llmHour2.setOrientation(LinearLayoutManager.VERTICAL);
        rvCalendar1.setLayoutManager(llmCalendar1);
        rvCalendar1.setItemAnimator(new DefaultItemAnimator());
        rvCalendar1.setAdapter(myCalendarAdapter1);
        rvCalendar1.addOnItemTouchListener(new RecyclerTouchListener(this, rvCalendar1, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomDate cd = dates.get(position);
                schedule1Date = cd.getDay()+","+cd.getDate()+" "+cd.getMonth();
                scheduleDate1FDStr = cd.getYear()+"/"+cd.getMonthDigit()+"/"+cd.getDate()+" ";
//                for(int i =0; i<dates.size();i++){
//                    Log.d("i",i+"");
//                    Log.d("calendaradapter1concc",myCalendarAdapter1.container.getChildCount()+"yyy");
//                    Log.d("calendaradapter1congi",myCalendarAdapter1.getItemCount()+"zzzz");
//                   myCalendarAdapter1.container.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.dateUnselected));
//                }
                //rvCalendar1setBackgrounColor(getResources().getColor(R.color.dateUnselected));
//                View v1 = rvCalendar1.getLayoutManager().getChildAt(positionCalendar1);
//                v1.setBackgroundColor(getResources().getColor(R.color.dateUnselected));
//                positionCalendar1 = position;
//                View v2 = rvCalendar1.getLayoutManager().getChildAt(position);
//                Log.d("positionCalendar1",positionCalendar1+"");
//                v2.setBackgroundColor(getResources().getColor(R.color.dateSelected));

                //Toast.makeText(getApplicationContext(),lo.getTakenDate(),Toast.LENGTH_SHORT).show();
                //showDialog();
                 //view.setBackgroundColor(getResources().getColor(R.color.dateSelected));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        rvHour1.setLayoutManager(llmHour1);
        rvHour1.setItemAnimator(new DefaultItemAnimator());
        rvHour1.setAdapter(myHourAdapter1);
        rvHour1.addOnItemTouchListener(new RecyclerTouchListener(this, rvHour1, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int hour = hours.get(position);
                schedule1Hour = ",JAM "+hour+":00-"+(hour+1)+":00";
                schedule1TextView.setText(schedule1Date+schedule1Hour);
                schedule1Hour = String.valueOf(hour);

                //CustomDate cd = dates.get(position);
                //rvCalendar1setBackgrounColor(getResources().getColor(R.color.dateUnselected));
//                View v1 = rvCalendar1.getLayoutManager().getChildAt(positionCalendar1);
//                v1.setBackgroundColor(getResources().getColor(R.color.dateUnselected));
//                positionCalendar1 = position;
//                View v2 = rvCalendar1.getLayoutManager().getChildAt(position);
//                Log.d("positionCalendar1",positionCalendar1+"");
//                v2.setBackgroundColor(getResources().getColor(R.color.dateSelected));

                //Toast.makeText(getApplicationContext(),lo.getTakenDate(),Toast.LENGTH_SHORT).show();
                //showDialog();
                //view.setBackgroundColor(getResources().getColor(R.color.dateSelected));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        rvCalendar2.setLayoutManager(llmCalendar2);
        rvCalendar2.setItemAnimator(new DefaultItemAnimator());
        rvCalendar2.setAdapter(myCalendarAdapter2);
        rvCalendar2.addOnItemTouchListener(new RecyclerTouchListener(this, rvCalendar2, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomDate cd = dates.get(position);
                schedule2Date = cd.getDay()+","+cd.getDate()+" "+cd.getMonth();
                scheduleDate2FDStr = cd.getYear()+"/"+cd.getMonthDigit()+"/"+cd.getDate()+" ";
                //view.setBackgroundColor(Color.GREEN);
                //Toast.makeText(getApplicationContext(),lo.getTakenDate(),Toast.LENGTH_SHORT).show();
                //showDialog();
                //  view.setBackgroundColor(getResources()(R.color.placeUnselectedBg));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        rvHour2.setLayoutManager(llmHour2);
        rvHour2.setItemAnimator(new DefaultItemAnimator());
        rvHour2.setAdapter(myHourAdapter2);
        rvHour2.addOnItemTouchListener(new RecyclerTouchListener(this, rvHour2, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int hour = hours.get(position);
                schedule2Hour = ",JAM "+hour+":00-"+(hour+1)+":00";
                schedule2TextView.setText(schedule2Date+schedule2Hour);
                schedule2Hour = String.valueOf(hour);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ImageButton prevCalendar1 = (ImageButton) findViewById(R.id.prevCalendar1);
        prevCalendar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               myCalendarAdapter1.tryMoveSelection(llmCalendar1,-1);

                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        ImageButton nextCalendar1 = (ImageButton) findViewById(R.id.nextCalendar1);
        nextCalendar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rvCalendar1.getLayoutManager().scrollToPosition(rvCalendar1.getAdapter().getItemCount());
                myCalendarAdapter1.tryMoveSelection(llmCalendar1,1);
                //mRecyclerView.getLayoutManager().scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        ImageButton prevHour1 = (ImageButton) findViewById(R.id.prevHour1);
        prevHour1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHourAdapter1.tryMoveSelection(llmHour1,-1);
                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        ImageButton nextHour1 = (ImageButton) findViewById(R.id.nextHour1);
        nextHour1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rvCalendar1.getLayoutManager().scrollToPosition(rvCalendar1.getAdapter().getItemCount());
                //rvHour1.smoothScrollToPosition(last+1);
                //mRecyclerView.getLayoutManager().scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                myHourAdapter1.tryMoveSelection(llmHour1,1);
            }
        });
        ImageButton prevCalendar2 = (ImageButton) findViewById(R.id.prevCalendar2);
        prevCalendar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendarAdapter2.tryMoveSelection(llmCalendar2,-1);

                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        ImageButton nextCalendar2 = (ImageButton) findViewById(R.id.nextCalendar2);
        nextCalendar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rvCalendar1.getLayoutManager().scrollToPosition(rvCalendar1.getAdapter().getItemCount());
                //rvCalendar2.smoothScrollToPosition(last+1);
                //mRecyclerView.getLayoutManager().scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                myCalendarAdapter2.tryMoveSelection(llmCalendar2,1);
            }
        });
        ImageButton prevHour2 = (ImageButton) findViewById(R.id.prevHour2);
        prevHour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHourAdapter2.tryMoveSelection(llmHour2,-1);

                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        ImageButton nextHour2 = (ImageButton) findViewById(R.id.nextHour2);
        nextHour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rvCalendar1.getLayoutManager().scrollToPosition(rvCalendar1.getAdapter().getItemCount());
                //rvHour2.smoothScrollToPosition(last+1);
                //mRecyclerView.getLayoutManager().scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                myHourAdapter2.tryMoveSelection(llmHour2,1);
            }
        });
        mySwitch = (Switch) findViewById(R.id.weekSwitch);
        mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        calendar2Layout.setVisibility(View.INVISIBLE);
        hour2Layout.setVisibility(View.INVISIBLE);
        schedule2TextView.setVisibility(View.INVISIBLE);
        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    mySwitch.setTextColor(Color.GREEN);
                    calendar2Layout.setVisibility(View.VISIBLE);
                    hour2Layout.setVisibility(View.VISIBLE);
                    schedule2TextView.setVisibility(View.VISIBLE);

                }else{
                    mySwitch.setTextColor(Color.WHITE);
                    calendar2Layout.setVisibility(View.INVISIBLE);
                    hour2Layout.setVisibility(View.INVISIBLE);
                    schedule2TextView.setVisibility(View.INVISIBLE);
                }

            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentConfirmSchedule.putExtra("map",hashMap);
                intentConfirmSchedule.putExtra("date1",schedule1Date);
                intentConfirmSchedule.putExtra("hour1",schedule1Hour);
                intentConfirmSchedule.putExtra("date2", schedule2Date);
                intentConfirmSchedule.putExtra("hour2",schedule2Hour);
                scheduleDate1FDStr = scheduleDate1FDStr+Integer.parseInt(schedule1Hour)+":00:00";
                intentConfirmSchedule.putExtra("dateTime1",scheduleDate1FDStr);
                scheduleDate2FDStr = scheduleDate2FDStr+Integer.parseInt(schedule2Hour)+":00:00";
                intentConfirmSchedule.putExtra("dateTime2",scheduleDate2FDStr);
                startActivity(intentConfirmSchedule);
                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });

    }
    private void getDates() {
        dates = new ArrayList<CustomDate>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        for (int index = 1; index < 10; index++) {

            CustomDate date = new CustomDate();
            date.setDate("" + calendar.get(Calendar.DATE));
            date.setDay(getDay(calendar.get(Calendar.DAY_OF_WEEK)));
            date.setYear("" + calendar.get(Calendar.YEAR));
            date.setMonth("" + getMonth(calendar.get(Calendar.MONTH)));
            date.setMonthDigit(calendar.get(Calendar.MONTH)+1);
//            date.setFormattedDate(calendar.get(Calendar.YEAR) + "-"
//                    + (calendar.get(Calendar.MONTH) + 1) + "-"
//                    + calendar.get(Calendar.DATE));

            dates.add(date);

            calendar.add(Calendar.DATE, 1);

        }


        //return dates;
    }

    private void getHours(){
        hours = new ArrayList<Integer>();
        for(int index=8;index<=17;index++){
            hours.add(index);
        }
        //return hours;
    }
    private String getDay(int index) {
        switch (index) {
            case Calendar.SUNDAY:
                return "SUN";
            case Calendar.MONDAY:
                return "MON";
            case Calendar.TUESDAY:
                return "TUE";
            case Calendar.WEDNESDAY:
                return "WED";
            case Calendar.THURSDAY:
                return "THUR";
            case Calendar.FRIDAY:
                return "FRI";
            case Calendar.SATURDAY:
                return "SAT";
        }
        return "";
    }

    private String getMonth(int index) {
        switch (index) {
            case Calendar.JANUARY:
                return "JANUARY";
            case Calendar.FEBRUARY:
                return "FEBRUARY";
            case Calendar.MARCH:
                return "MARCH";
            case Calendar.APRIL:
                return "APRIL";
            case Calendar.MAY:
                return "MAY";
            case Calendar.JUNE:
                return "JUNE";
            case Calendar.JULY:
                return "JULY";
            case Calendar.AUGUST:
                return "AUGUST";
            case Calendar.SEPTEMBER:
                return "SEPTEMBER";
            case Calendar.OCTOBER:
                return "OCTOBER";
            case Calendar.NOVEMBER:
                return "NOVEMBER";
            case Calendar.DECEMBER:
                return "DECEMBER";
        }
        return "";
    }
    public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder>{
        private List<CustomDate> customDateList;
        //private SparseBooleanArray selectedItems;
        private int currentItemPos;
        public ViewGroup container;
        private int selectedItem = 0;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView day, date, month;
            public LinearLayout ll;
            int pos;
            public MyViewHolder(View view) {
                super(view);
                View v = getWindow().getDecorView().getRootView();
                ll = (LinearLayout) v.findViewById(R.id.calendaritemlinearlayout);
               // AbsoluteLayout root = (AbsoluteLayout) view.findViewById(R.id.calendaritemlinearlayout);
                day = (TextView) view.findViewById(R.id.calendar_item_day);
                date = (TextView) view.findViewById(R.id.calendar_item_date);
                month = (TextView)view.findViewById(R.id.calendar_item_month);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Redraw the old selection and the new
                        notifyItemChanged(selectedItem);
                        selectedItem = getLayoutPosition();
                        notifyItemChanged(selectedItem);
                    }
                });

            }

        }

        public CalendarAdapter(List<CustomDate> customDates) {
            this.customDateList = customDates;
        }
        private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
            int nextSelectItem = selectedItem + direction;

            // If still within valid bounds, move the selection, notify to redraw, and scroll
            if (nextSelectItem >= 0 && nextSelectItem < getItemCount()) {
                notifyItemChanged(selectedItem);
                selectedItem = nextSelectItem;
                notifyItemChanged(selectedItem);
                lm.scrollToPosition(selectedItem);
                return true;
            }

            return false;
        }
        @Override
        public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_pager_item, parent, false);
            container = parent;
            return new MyViewHolder(itemView);

        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            CustomDate cd = customDateList.get(position);
            holder.day.setText(cd.getDay());
            holder.month.setText(cd.getMonth());
            holder.date.setText(cd.getDate());
            holder.itemView.setSelected(selectedItem == position);
            //holder.itemView.setSelected(selectedItems.get(position, false));
            //holder.ll.setSelected(selectedItems.get(position, false));

        }
        @Override
        public int getItemCount(){
            return customDateList.size();
        }

    }
    public class HourAdapter extends RecyclerView.Adapter<HourAdapter.MyViewHolder>{
        private List<Integer> hourList;
        public ViewGroup container;
        private int selectedItem = 0;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView hour;

            public MyViewHolder(View view) {
                super(view);
                hour = (TextView) view.findViewById(R.id.hour_item);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Redraw the old selection and the new
                        notifyItemChanged(selectedItem);
                        selectedItem = getLayoutPosition();
                        notifyItemChanged(selectedItem);

                    }
                });
            }

        }
        private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
            int nextSelectItem = selectedItem + direction;

            // If still within valid bounds, move the selection, notify to redraw, and scroll
            if (nextSelectItem >= 0 && nextSelectItem < getItemCount()) {
                notifyItemChanged(selectedItem);
                selectedItem = nextSelectItem;
                notifyItemChanged(selectedItem);
                lm.scrollToPosition(selectedItem);
                return true;
            }

            return false;
        }
        public HourAdapter(List<Integer> hours) {
            this.hourList = hours;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hour_pager_item, parent, false);
            container = parent;
            return new MyViewHolder(itemView);

        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Integer h = hourList.get(position);
            holder.hour.setText(String.valueOf(h));
            holder.itemView.setSelected(selectedItem == position);

        }
        @Override
        public int getItemCount(){
            Log.d("hourlist",hourList.size()+"");
            return hourList.size();
        }


    }
    public class CustomDate{
        private String day_string;
        private String month_string;
        private String date;
        private String monthDigit;
        private String year;
        public CustomDate(){
            day_string="";
            month_string="";
            date="";
            year="";
            monthDigit="";
        }
        public void setDate(String _date){
            this.date = _date;
        }
        public void setDay(String _day){
            day_string = _day;
        }
        public void setMonth(String _month){
            month_string = _month;
        }
        public void setYear(String _year){
            this.year = _year;
        }
        public void setMonthDigit(int monthDigit){
            this.monthDigit = String.valueOf(monthDigit);
        }
        public String getDate(){
            return date;
        }
        public String getYear(){
            return year;
        }
        public String getDay(){
            return day_string;
        }
        public String getMonth(){
            return month_string;
        }
        public String getMonthDigit(){return monthDigit;}

    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private DateConfigurationActivity2.ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final DateConfigurationActivity2.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public interface ClickListener{
        void onClick (View view, int position);
        void onLongClick(View view, int position);
    }


}
