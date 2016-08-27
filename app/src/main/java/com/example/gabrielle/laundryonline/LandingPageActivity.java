package com.example.gabrielle.laundryonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gabrielle.laundryonline.db.User;
import com.example.gabrielle.laundryonline.db.UserAddressDetails;
import com.facebook.CallbackManager;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by gabrielle on 6/10/2016.
 */
public class LandingPageActivity extends AppCompatActivity{
    private SessionManager session;
    private User user1;
    private User user2;
    private UserAddressDetails uad1;
    private CallbackManager callbackManager;
    private ViewPager viewPager;
    private ImagePagerAdapter myImagePageAdapter;

    private TextView[] dots;
    private LinearLayout dotsLayout;
    private int currentPage;
    private int[] mResources= {
        R.drawable.image1_1,
                R.drawable.image2_1,
                R.drawable.image3_1,
                R.drawable.image4_1

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()==true){
            Intent intenttoShowOption = new Intent(this,ShowOptionActivity.class);
            startActivity(intenttoShowOption);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_landing_page);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == mResources.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
//        Timer swipeTimer = new Timer();
//        swipeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, 3000, 3000);
        //dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        myImagePageAdapter = new ImagePagerAdapter(this);
        viewPager.setAdapter(myImagePageAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                //addBottomDots(position);


            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


        Button loginButton = (Button) findViewById(R.id.landingpageloginbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginActivity();
            }
        });
        Button signupButton = (Button) findViewById(R.id.landingpagesignupbutton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpActivity();
            }
        });

        currentPage = 0;

        //addBottomDots(0);



    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[4];



        //dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_active));
    }
    public void showLoginActivity(){
        Intent intentLogin = new Intent(this,LoginActivity.class);
        startActivity(intentLogin);

    }
    public void showSignUpActivity(){
        Intent intentCreateNewAccount = new Intent(this,CreateNewAccountActivity.class);
        startActivity(intentCreateNewAccount);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    class ImagePagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public ImagePagerAdapter(Context context) {
            mContext = context;
           // mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);
           // addBottomDots(position);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
