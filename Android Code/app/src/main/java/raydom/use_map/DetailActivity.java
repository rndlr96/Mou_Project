package raydom.use_map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LeeSunjun on 2017-08-16.
 */

public class DetailActivity extends Activity {

    Context context = this;

    String UserID;
    String MarkID;
    String Category;
    String UserPic;
    String Reviews;

    String star_point;
    String review;

    TextView User_name;
    ImageView GPA;
    TextView review_content;

    ArrayList<String> names;
    ArrayList<Integer> stars;
    ArrayList<String> review_gorup;

    DBHandler controller;

    String userPic;

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,Intent data) {
        if( requestCode == 7){
            userPic = data.getStringExtra("profilePic");
            ImageView iv = (ImageView) findViewById(R.id.user_icon);

            controller.set_profile(userPic);

            Picasso.with(context)
                    .load(userPic)
                    .transform(new CropCircleTransformation())
                    .into(iv);
            Log.d("UserProfile", "maps url is " + userPic);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        controller = new DBHandler(getApplication());

        names = new ArrayList<String>();
        stars = new ArrayList<Integer>();
        review_gorup = new ArrayList<String>();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));

        ImageView main_image = (ImageView)findViewById(R.id.main_image);
        ImageView profile = (ImageView)findViewById(R.id.user_icon) ;

        Intent intent = getIntent();
        String url = intent.getStringExtra("Url");
        UserID = intent.getStringExtra("UserID");
        MarkID = intent.getStringExtra("MarkID");
        Category = intent.getStringExtra("Category");
        UserPic = intent.getStringExtra("UserPic");
        Reviews = intent.getStringExtra("Reviews");

        Log.d("review","whole" + Reviews);

        Log.d("review","come");

        parse_reviews(Reviews);
        Log.d("review","classify");

        set_Reviews();
        Log.d("review","show all");

        Toast.makeText(this, UserID + " / " + MarkID + " / " + Category, Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK,intent);

        Log.d("url_arrived",url);

        Picasso.with(context)
                .load(url)
                .into(main_image);

        if(!UserPic.isEmpty()) {
            Picasso.with(context)
                    .load(UserPic)
                    .transform(new CropCircleTransformation())
                    .into(profile);
        } else {
            Picasso.with(context)
                    .load("https://i.imgur.com/kZgHxcZ.png")
                    .transform(new CropCircleTransformation())
                    .into(profile);
        }

        TextView name = (TextView)findViewById(R.id.text_userID);
        name.setText(UserID);

        //initialize();
    }

    private void initialize(){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0,1500);
    }

    public void star0_setting() {
        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));

        star_point = "0";
    }

    public void star1_clicked(View v) {
        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));

        star_point = "1";
    }

    public void star2_clicked(View v) {
        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));

        star_point = "2";
    }

    public void star3_clicked(View v) {
        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));

        star_point = "3";
    }

    public void star4_clicked(View v) {
        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.blank_star));

        star_point = "4";
    }

    public void star5_clicked(View v) {
        ImageButton star;

        star = (ImageButton) findViewById(R.id.star1);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star2);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star3);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star4);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));
        star = (ImageButton) findViewById(R.id.star5);
        star.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_star));

        star_point = "5";
    }

    public void review_submit_clicked(View v) {
        EditText cm = (EditText)findViewById(R.id.comment);
        review = cm.getText().toString();

        cm.setText("");

        Log.d("cm",review);

        if(MarkID.equals("Guest")) {

            Toast.makeText(this, "You have to Login for Reviewing", Toast.LENGTH_SHORT).show();

            this.finish();
        } else {
            if (Session.getCurrentSession().isClosed()) {

                Intent intent = new Intent(getBaseContext(), kakaoActivity.class);
                startActivityForResult(intent, 7);

            } else {
                SendData sendReview = new SendData();

                String updated_review = "";

                if (!get_url(Integer.parseInt(Category)).isEmpty())
                    updated_review = sendReview.sendData6(get_url(Integer.parseInt(Category)), MarkID, UserID, star_point, review);

                while(sendReview.get_check() == -1) {
                    ;
                }

                reset_Arraylist();

                parse_reviews(updated_review);
                Log.d("review","classify");

                set_Reviews();
                Log.d("review","show all");

                star0_setting();
            }
        }
    }

    public void reset_Arraylist() {
        names.clear();
        review_gorup.clear();
        stars.clear();

        User_name = (TextView)findViewById(R.id.name_1);
        GPA = (ImageView)findViewById(R.id.star_point_1);
        review_content = (TextView)findViewById(R.id.review_1);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_2);
        GPA = (ImageView)findViewById(R.id.star_point_2);
        review_content = (TextView)findViewById(R.id.review_2);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_3);
        GPA = (ImageView)findViewById(R.id.star_point_3);
        review_content = (TextView)findViewById(R.id.review_3);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_4);
        GPA = (ImageView)findViewById(R.id.star_point_4);
        review_content = (TextView)findViewById(R.id.review_4);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_5);
        GPA = (ImageView)findViewById(R.id.star_point_5);
        review_content = (TextView)findViewById(R.id.review_5);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_6);
        GPA = (ImageView)findViewById(R.id.star_point_6);
        review_content = (TextView)findViewById(R.id.review_6);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_7);
        GPA = (ImageView)findViewById(R.id.star_point_7);
        review_content = (TextView)findViewById(R.id.review_7);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_8);
        GPA = (ImageView)findViewById(R.id.star_point_8);
        review_content = (TextView)findViewById(R.id.review_8);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_9);
        GPA = (ImageView)findViewById(R.id.star_point_9);
        review_content = (TextView)findViewById(R.id.review_9);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");

        User_name = (TextView)findViewById(R.id.name_10);
        GPA = (ImageView)findViewById(R.id.star_point_10);
        review_content = (TextView)findViewById(R.id.review_10);

        User_name.setText("");
        GPA.setImageResource(android.R.color.transparent);
        review_content.setText("");
    }

    public String get_url(int category) {
        if(category == 1)
            return "http://52.79.121.208/review/toilet/toilet_review_write.php";
        else if (category == 2)
            return "http://52.79.121.208/review/wifi/wifi_review_write.php";
        else if (category == 3)
            return "http://52.79.121.208/review/smoke/smoke_review_write.php";
        else if (category == 4)
            return "http://52.79.121.208/review/statue/statue_review_write.php";

        return "";
    }

    public void parse_reviews(String myJSON) {

        try {
            JSONArray JA = new JSONArray(myJSON);

            Log.d("review","length : "+JA.length());

            for (int i = 0; i < JA.length(); i++) {

                JSONObject c = JA.getJSONObject(i);

                names.add(c.getString("userID"));
                stars.add(Integer.parseInt(c.getString("GPA")));
                review_gorup.add(c.getString("review"));

                Log.d("review","classify"+i);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }
    }

    public void set_Reviews(){
        int num = 0;

        Log.d("review","show 0");

        if(!(num < names.size()))
            return;

        User_name = (TextView)findViewById(R.id.name_1);
        Log.d("review","show 1.1");
        GPA = (ImageView)findViewById(R.id.star_point_1);
        Log.d("review","show 1.2");
        review_content = (TextView)findViewById(R.id.review_1);

        Log.d("review","show 1");

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        Log.d("review","show 2");

        num++;

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_2);
        GPA = (ImageView)findViewById(R.id.star_point_2);
        review_content = (TextView)findViewById(R.id.review_2);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        Log.d("review","show 22");

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_3);
        GPA = (ImageView)findViewById(R.id.star_point_3);
        review_content = (TextView)findViewById(R.id.review_3);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        Log.d("review","show 33");

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_4);
        GPA = (ImageView)findViewById(R.id.star_point_4);
        review_content = (TextView)findViewById(R.id.review_4);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        Log.d("review","show 44");

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_5);
        GPA = (ImageView)findViewById(R.id.star_point_5);
        review_content = (TextView)findViewById(R.id.review_5);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_6);
        GPA = (ImageView)findViewById(R.id.star_point_6);
        review_content = (TextView)findViewById(R.id.review_6);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_7);
        GPA = (ImageView)findViewById(R.id.star_point_7);
        review_content = (TextView)findViewById(R.id.review_7);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_8);
        GPA = (ImageView)findViewById(R.id.star_point_8);
        review_content = (TextView)findViewById(R.id.review_8);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_9);
        GPA = (ImageView)findViewById(R.id.star_point_9);
        review_content = (TextView)findViewById(R.id.review_9);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;

        if(!(num < names.size()))
            return;

        User_name = (TextView) findViewById(R.id.name_10);
        GPA = (ImageView)findViewById(R.id.star_point_10);
        review_content = (TextView)findViewById(R.id.review_10);

        User_name.setText(names.get(num));
        GPA.setImageResource(get_star_id(stars.get(num)));
        review_content.setText(review_gorup.get(num));

        num++;
    }

    public int get_star_id(int num){
        if(num == 1) {
            return R.drawable.star_1;
        } else if (num == 2) {
            return R.drawable.star_2;
        } else if (num == 3) {
            return R.drawable.star_3;
        } else if (num == 4) {
            return R.drawable.star_4;
        } else if (num == 5) {
            return R.drawable.star_5;
        } else {
            return R.drawable.star_0;
        }
    }
}
