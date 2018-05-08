package raydom.use_map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.auth.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    Context context = this; // MapsActivity 의 context

    SQLiteDatabase db; // MoU DB
    DBHandler controller; // DB Helper for MoU
    DBOpenHelper helper;

    int total = 0;

    //data(tmp)

    private double LG,LT,DIY_LG,DIY_LT;

    private BackPressCloseHandler backPressCloseHandler;

    LinearLayout mark_info;

    boolean mark_info_open = false;

    SendData send_mark;

    String gpa_url_send;
    String review_url;

    ImageView mark_image;

    Marker here; // my loc
    Marker DIY; // adding loc
    Marker tmp_marker;

    String Name;
    String ID;
    String userPic = "";

    TextView mark_name;

    private GoogleMap mMap;

    ToggleButton mGPS;

    int category = 0;
    int markid;
    int add_type = -1;

    private float mDeclination; // get sensor's declination value
    private float[] mRotationMatrix = new float[9]; // get rotation value
    private SensorManager sensorManager; //to manage sensor
    private Sensor sensor; // sensor
    float bearing;

    String current_marker_name;
    EditText text;

    Geocoder geocoder;
    EditText et;

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(MapsActivity.this, "결과가 성공이 아님.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode == 1) { // get the User ID that login
            final TextView nameView = (TextView) findViewById(R.id.name);
            final TextView id = (TextView) findViewById(R.id.id);

            Name = data.getStringExtra("NAME");
            ID = data.getStringExtra("ID");

            nameView.setText(Name);
            id.setText(ID);

            mark_info.setVisibility(View.GONE);
            mark_info_open = false;

        } else if (requestCode == 4) {

           /* double lt = Double.parseDouble(data.getStringExtra("result_lt"));
            double lg = Double.parseDouble(data.getStringExtra("result_lg"));
            int ct = data.getIntExtra("result_category",0);

            String title = data.getStringExtra("result_title");

            int bm = 0;
            int id = 0 - total;
            String url = "http://i.imgur.com/NmPyWw4.png";

            controller.insert(id,lt, lg, title, url, bm, ct);*/

            Toast.makeText(this, "Marker Adding is success", Toast.LENGTH_SHORT).show();
        } else if( requestCode == 7){
            userPic = data.getStringExtra("profilePic");

            controller.set_profile(userPic);

            ImageView iv = (ImageView) findViewById(R.id.profilPic);
            Picasso.with(context)
                    .load(userPic)
                    .transform(new CropCircleTransformation())
                    .into(iv);
            Log.d("UserProfile", "maps url is " + userPic);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controller = new DBHandler(getApplicationContext());
        startActivity(new Intent(this,ParsingActivity.class)); // getData from resource;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Cursor c = controller.get_login_info();
        int check = c.getCount();

        if(check == 0)
            startActivityForResult(new Intent(this,LoginActivity.class),1);
        else {
            c.moveToNext();

            ID = c.getString(c.getColumnIndex("ID"));

            Log.d("login",ID);

            Name = c.getString(c.getColumnIndex("name"));
            userPic = c.getString(c.getColumnIndex("profile"));
        }

        LT = 37.49639;
        LG = 126.956889;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_view);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.action_bar,null);
        actionBar.setCustomView(mCustomView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        send_mark = new SendData();

        mark_info = (LinearLayout) findViewById(R.id.mark_info);

        mark_info.setVisibility(View.GONE);
        mark_info_open = false;

        mark_name = (TextView)findViewById(R.id.mark_name);

        mGPS = (ToggleButton)findViewById(R.id.mGPS);
        mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_off_1));

        mark_image = (ImageView)findViewById(R.id.mark_image);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //make senseormanager available to use sensor service
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // make sensor available to use for magnetic field

        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    if(mGPS.isChecked()){
                        mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_on_1));
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);

                        here.setVisible(true);
                    }
                    else{
                        lm.removeUpdates(mLocationListener);
                        mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_off_1));

                        show_mark(category);

                        here.setVisible(false);
                    }
                }catch(SecurityException ex){

                }
            }
        });

        View t = findViewById(R.id.cover);
        t.setVisibility(View.GONE);

        Log.d("HashKey", "1");
        getAppKeyHash();

        geocoder = new Geocoder(this);
        et = (EditText)findViewById(R.id.Address);

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);
        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);

        RelativeLayout searching_box = (RelativeLayout)findViewById(R.id.searching_box);
        if (searching_box.getVisibility() == View.VISIBLE)
            searching_box.setVisibility(View.GONE);

        TextView cover = (TextView)findViewById(R.id.cover);
        if(cover.getVisibility() == View.VISIBLE){
            cover.setVisibility(View.GONE);
            view_visible();
            return;
        }

        if(mark_info_open) {
            mark_info.setVisibility(View.GONE);
            mark_info_open = false;
            return;
        }

        WebView w = (WebView)findViewById(R.id.webView);
        if(w.getVisibility() == View.VISIBLE){
            w.setVisibility(View.GONE);
            view_visible();
            return;
        }

        backPressCloseHandler.onBackPressed();
    }

    private void getClick() { //터치를 통해 좌표를 받아오는 함수;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point){

                DIY_LT = point.latitude;
                DIY_LG = point.longitude;

                DIY = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(300)).position(new LatLng(point.latitude,point.longitude)).title("HERE?").zIndex(2.0f));
                DIY.setVisible(true);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DIY_LT,DIY_LG), 17.0f));

                mMap.setOnMapClickListener(null);

                View t = findViewById(R.id.cover);
                t.setVisibility(View.GONE);

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
                alt_bld
                        .setMessage("This is the right location that you want?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){ // left button
                                double latitude = here.getPosition().latitude;
                                double longitude = here.getPosition().longitude;
                                Toast.makeText(getApplicationContext(), "Yes. The position is "+latitude+longitude , Toast.LENGTH_SHORT).show();
                                dialog.cancel();

                                if(add_type == 2) {
                                    add_type = -1;
                                    add_ok();
                                } else if (add_type == 3) {
                                    add_type = -1 ;

                                    Intent intent = new Intent(context, AddBoardingActivity.class);

                                    Cursor c = controller.get_login_info();
                                    c.moveToNext();

                                    ID = c.getString(c.getColumnIndex("ID"));

                                    intent.putExtra("ID", ID);

                                    Log.d("board","userID : "+  ID);

                                    intent.putExtra("LT",Double.toString(DIY_LT));
                                    intent.putExtra("LG",Double.toString(DIY_LG));
                                    startActivity(intent);

                                    view_visible();
                                }
                            }


                        })
                        .setNegativeButton("NO",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) { // right button
                                mMap.clear();
                                dialog.cancel();

                                view_visible();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                alert.setTitle("Adding Sequenec");
                alert.setIcon(R.drawable.main_logo);
                alert.getWindow().setGravity(Gravity.BOTTOM);
                alert.show();
            }
        });
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            LG = longitude;
            LT = latitude;

            here.remove();

            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT,LG)).title("Title").zIndex(10.0f));
            here.setVisible(true);

            if(mGPS.isChecked()) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LT, LG), 17.0f));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private SensorEventListener mSensorEventListener = new SensorEventListener(){ //listen sensorevent
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){//when event is magnetic field event
                //Toast.makeText(MapsActivity.this,"OK",Toast.LENGTH_SHORT).show();
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values); // store event value to matrix
                float[] orientation = new float[3]; // for orientation
                SensorManager.getOrientation(mRotationMatrix,orientation);//make matrix oriented
                bearing = (float)Math.toDegrees(orientation[0])+mDeclination; //caculate bearing

            }
            else{
                // Toast.makeText(MapsActivity.this,"waiting",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void updatemap(float bearing) {
        //CameraPosition oldPos = mMap.getCameraPosition();

        LatLng loc = new LatLng(LT, LG); //get current location
        CameraPosition oldPos = CameraPosition.fromLatLngZoom(loc, mMap.getCameraPosition().zoom); //get cameraposition from loc
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();// build pos with bearing
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        startActivity(new Intent(this,SplashActivity.class));

        mMap = googleMap;

        LatLng Begin = new LatLng(37.49639,126.956889);

        here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(Begin).title("You").zIndex(10.0f));
        here.setVisible(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Begin));
        mMap.setMinZoomPreference(5);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 17.0f));

                tmp_marker = marker;

                if (marker.getPosition().latitude == here.getPosition().latitude && marker.getPosition().longitude == here.getPosition().longitude) {
                    return false;

                }else if (add_type == 1 && marker.getZIndex() == 2) {
                    Toast.makeText(context, "You cant change DIY marker to personal marker", Toast.LENGTH_SHORT).show();
                    add_type = -1;
                }
                else if (add_type == 1) {
                    personal_add();
                    add_type = -1;

                    mMap.clear();

                    here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("Title").zIndex(10.0f));
                    show_mark(category);

                    return false;

                } else if (marker.getZIndex() == 2.0) {
                    mark_info.setVisibility(View.VISIBLE);
                    mark_info_open = true;

                    Cursor c = controller.get_diy(tmp_marker.getPosition().latitude, tmp_marker.getPosition().longitude);
                    startManagingCursor(c);

                    String pic_url = "";

                    if(c.getCount() != 0) {
                        c.moveToNext();
                        pic_url = c.getString(c.getColumnIndex("url"));
                    }

                    if(!pic_url.isEmpty())
                        Picasso.with(context)
                                .load(pic_url)
                                .transform(new CropCircleTransformation())
                                .into(mark_image);

                    final SendData diy_check = new SendData();

                    final String checking_url = "http://52.79.121.208/diy/vote.php";

                    c = controller.get_diy(marker.getPosition().latitude,marker.getPosition().longitude);

                    int tmp_id = 0;

                    if(c.getCount() != 0) {
                        c.moveToNext();

                        tmp_id = Integer.parseInt(c.getString(c.getColumnIndex("id")));
                    }

                    final int diy_id = tmp_id;

                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
                    alt_bld
                            .setMessage("Is this marker really in that location?")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) { // left button
                                    diy_check.sendData12(checking_url,Integer.toString(diy_id),Integer.toString(category),"1");
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) { // right button
                                    diy_check.sendData12(checking_url,Integer.toString(diy_id),Integer.toString(category),"0");
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = alt_bld.create();
                    alert.setTitle("Check Validation");
                    alert.setIcon(R.drawable.main_logo);
                    alert.getWindow().setGravity(Gravity.BOTTOM);
                    alert.show();

                } else {
                    mark_info.setVisibility(View.VISIBLE);
                    mark_info_open = true;

                    Cursor c = controller.select_marker(tmp_marker.getPosition().latitude, tmp_marker.getPosition().longitude);
                    startManagingCursor(c);

                    c.moveToNext();
                    markid = c.getInt(c.getColumnIndex("id"));

                    Log.d("review", "id : " + markid);

                    SendData tmp_send = new SendData();
                    String res = tmp_send.sendData4(gpa_url_send, markid);

                    Log.d("review", "here");

                    if (!parse_gpa(res).isEmpty()) {
                        show_stars(Double.parseDouble(parse_gpa(res)));
                    }

                    Log.d("gpa", "mark id : " + markid);

                    //마커 정보 보여주는 listener 구현 부
                    Picasso.with(context)
                            .load(get_url(marker.getPosition().latitude, marker.getPosition().longitude))
                            .transform(new CropCircleTransformation())
                            .into(mark_image);

                    TextView marker_name = (TextView) findViewById(R.id.mark_name);
                    Log.d("name_tag", get_name(marker.getPosition().latitude, marker.getPosition().longitude));
                    marker_name.setText(get_name(marker.getPosition().latitude, marker.getPosition().longitude));

                    return false;
                }

                return false;
            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {

                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    mark_info.setVisibility(View.GONE);
                    mark_info_open = false;

                    mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_off_1));
                    mGPS.setChecked(false);

                    Log.d("TEST","Good1");
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
                }
            }
        });

    }

    public static void hideSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
    }

    public void menu_clicked(View v) {

        Cursor c = controller.get_login_info();
        int check = c.getCount();

        if(check == 0)
            startActivityForResult(new Intent(this,LoginActivity.class),1);
        else {
            c.moveToNext();

            ID = c.getString(c.getColumnIndex("ID"));

            Log.d("login",ID);

            Name = c.getString(c.getColumnIndex("name"));
            userPic = c.getString(c.getColumnIndex("profile"));

            TextView id = (TextView)findViewById(R.id.id);
            id.setText(ID);
            TextView name =(TextView)findViewById(R.id.name);
            name.setText(Name);

            if(!userPic.isEmpty()) {
                ImageView profile = (ImageView)findViewById(R.id.profilPic);
                Picasso.with(context)
                        .load(userPic)
                        .transform(new CropCircleTransformation())
                        .into(profile);
            } else {
                ImageView profile = (ImageView)findViewById(R.id.profilPic);
                Picasso.with(context)
                        .load("https://i.imgur.com/kZgHxcZ.png")
                        .transform(new CropCircleTransformation())
                        .into(profile);
            }

        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.GONE)
            drawer.setVisibility(View.VISIBLE);
        else if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);
    }

    public void search_clicked(View v) {

        RelativeLayout searching_box = (RelativeLayout)findViewById(R.id.searching_box);
        if(searching_box.getVisibility() == View.GONE)
            searching_box.setVisibility(View.VISIBLE);
        else if (searching_box.getVisibility() == View.VISIBLE)
            searching_box.setVisibility(View.GONE);
    }

    public void toilet_b_clicked(View v){

        SendData tmp_send = new SendData();
        tmp_send.sendData12("http://52.79.121.208/diy/vote.php","22","3","0");

        if(category != 1) {
            no_effect();

            category = 1;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);

    }

    public void wifi_b_clicked(View v){

        if(category != 2) {
            no_effect();

            category = 2;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);
    }

    public void smoking_b_clicked(View v){

        if(category != 3) {
            no_effect();

            category = 3;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);

        gpa_url_send = "http://52.79.121.208/review/smoke/smoke_review_mark.php";
        review_url = "http://52.79.121.208/review/smoke/smoke_review_read.php";

    }

    public void SOG_b_clicked(View v){

        if(category != 4) {
            no_effect();

            category = 4;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);
    }

    public void Bookmark_b_clicked(View v){

        if(category != 5) {
            no_effect();

            category = 5;

        } else {
            no_effect();
        }

        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);
    }

    public void personal_b_clicked(View v) {
        mMap.clear();

        no_effect();

        Cursor c = controller.select_all_personal();
        startManagingCursor(c);

        Log.d("ddbb", "get Cursor");

        while (c.moveToNext()) {
            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
            double lt = c.getDouble(c.getColumnIndex("latitude"));
            double lg = c.getDouble(c.getColumnIndex("longitude"));
            String name = c.getString(c.getColumnIndex("name"));

            Log.d("personal" , "getting point : " + lt + lg + name);

            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .position(new LatLng(lt, lg)).title(name).zIndex(1.0f));
        }

        LinearLayout drawer = (LinearLayout)findViewById(R.id.drawer);

        if(drawer.getVisibility() == View.VISIBLE)
            drawer.setVisibility(View.GONE);
    }

    public void board_b_clicked(View v) {
        Intent intent = new Intent(context,AddBoardingActivity.class);

        intent.putExtra("here_lt",Double.toString(here.getPosition().latitude));
        intent.putExtra("here_lg",Double.toString(here.getPosition().longitude));

        startActivity(intent);
    }

    public void setting_clicked(View v){
        Intent intent = new Intent(getBaseContext(), Setting.class);
        intent.putExtra("userID",ID);

        LinearLayout drawer = (LinearLayout) findViewById(R.id.drawer);
        drawer.setVisibility(View.GONE);

        startActivityForResult(intent,1);
    }

    public void add_ok(){

        show_mark(category);

        DIY.setVisible(false);

        view_visible();

        Intent ADD = new Intent(this,ADD_Marker.class);

        ADD.putExtra("Result_LT",Double.toString(DIY_LT));
        ADD.putExtra("Result_LG",Double.toString(DIY_LG));

        startActivityForResult(ADD,4);
    }

    public void personal_add() {

        Cursor c = controller.select_marker(tmp_marker.getPosition().latitude, tmp_marker.getPosition().longitude);
        startManagingCursor(c);

        c.moveToNext();
        int id = c.getInt(c.getColumnIndex("id"));
        Log.d("ddbb","id : " + id);
        int ct = c.getInt(c.getColumnIndex("category"));
        Log.d("ddbb","ct : " + ct);
        int bm = c.getInt(c.getColumnIndex("bm"));
        Log.d("ddbb","bm : " + bm);

        controller.set_personal(id,ct,bm);

        if(bm == 0 ) {
            tmp_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        } else {
            tmp_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    public void DIY_add_clicked(View v){
        if(ID.compareTo("Guest") != 0) {

            LinearLayout drawer = (LinearLayout) findViewById(R.id.drawer);

            if (drawer.getVisibility() == View.VISIBLE)
                drawer.setVisibility(View.GONE);

            if (Session.getCurrentSession().isClosed()) {
                Intent intent = new Intent(getBaseContext(), kakaoActivity.class);
                startActivityForResult(intent, 7);
                //startActivity(intent);
            } else {

                Toast.makeText(this, "Click screen you want to add", Toast.LENGTH_SHORT).show();

                mMap.clear();

                View t = findViewById(R.id.cover);
                t.setVisibility(View.VISIBLE);
                view_invisible();

                add_type = 2;

                getClick();
            }
        }else{
            Toast.makeText(this, "You have to log in with Mou ID", Toast.LENGTH_LONG).show();
        }
    }

    public void bm_add_clicked(View v) {
        add_type = 1;
        addMarkerDialog();
    }

    private void addMarkerDialog(){ // Select my position marker or exist market
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld
                .setMessage("What type of marker do you want to add?")
                .setCancelable(false)
                .setPositiveButton("My Position", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){ // left button
                        View v = null; // for call menu_clicked
                        menu_clicked(v);
                        double latitude = here.getPosition().latitude;
                        double longitude = here.getPosition().longitude;
                        Toast.makeText(getApplicationContext(), ""+latitude+longitude , Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        getMarkerName();
                    }
                })
                .setNegativeButton("Exist marker",new DialogInterface.OnClickListener(){
                     public void onClick(DialogInterface dialog, int id) { // right button
                         View v = null; // for call menu_clicked
                         menu_clicked(v);
                         selectMarkerTypeDialog();
                         dialog.cancel();
                     }
                });
        AlertDialog alert = alt_bld.create();
        alert.setTitle("Select Type");
        alert.setIcon(R.drawable.main_logo);
        alert.show();
    }

    private void selectMarkerTypeDialog(){ //Select market type
        final String[] type = {"TOILET", "WIFI","SMOKING AREA","STATUE OF GIRL"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("Select Marker Type");
        alt_bld.setIcon(R.drawable.main_logo);
        alt_bld.setSingleChoiceItems(type, -1, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int item){
                Toast.makeText(getApplicationContext(), "type is " + type[item], Toast.LENGTH_SHORT).show();
                if(type[item].compareTo("TOILET") == 0){ // toilet selected
                    category = 1;
                    show_mark(category);

                }else if(type[item].compareTo("WIFI") == 0){ // wifi selected
                    category = 2;
                    show_mark(category);
                }else if(type[item].compareTo("SMOKING AREA") == 0){ // smoking area selected
                    category = 3;
                    show_mark(category);
                }else if(type[item].compareTo("STATUE OF GIRL") == 0){ // statue of girl selected
                    category = 4;
                    show_mark(category);
                }
                dialog.cancel();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void getMarkerName(){
        LayoutInflater li = LayoutInflater.from(context); //make layoutInflater
        View v = li.inflate(R.layout.input_name_dialog, null); // set inflater to input_name_dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context); // make alert dialog
        alertDialogBuilder.setView(v); // set dialog view to input_name_dialog

        text = (EditText) v.findViewById(R.id.editTextDialogUserInput); // get id from input_name_dialog


       alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                new DialogInterface.OnClickListener(){ // ok clicked
                    public void onClick(DialogInterface dialog, int num){
                        current_marker_name  = text.getText().toString(); // store input to narkername variance
                        Toast.makeText(getApplicationContext(), "name is "+current_marker_name, Toast.LENGTH_SHORT).show();

                        controller.insert_personal(here.getPosition().latitude,here.getPosition().longitude,current_marker_name);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener(){ //cancle clicked
                            public void onClick(DialogInterface dialog, int num){
                                dialog.cancel(); //cancle dialog
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create(); //create dialog
        alertDialog.show();
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HashKey", "1");
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("HashKey", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    public void searching_clicked(View v){
        mGPS.setChecked(false);
        List<Address> list = null;
        String str = et.getText().toString();
        hideSoftKeyboard(this);

        try{
            list = geocoder.getFromLocationName(str,10);
        }catch(IOException e){
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size() == 0) {
                //******************************************
                et.setText("일치하는 주소가 없습니다.");
                //********************************************
            } else {
                Address addr = list.get(0);
                LatLng loc = new LatLng(addr.getLatitude(),addr.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                mMap.setMinZoomPreference(17);
            }
        }
    }

    public void no_effect(){
        mark_info.setVisibility(View.GONE);
        mark_info_open = false;

        Button tmp_btn = (Button)findViewById(R.id.board_add_button);
        tmp_btn.setVisibility(View.GONE);

        mMap.clear();

        category = 0;
    }

    public void view_invisible(){
        mark_info.setVisibility(View.GONE);
        mark_info_open = false;

        View b;

        b = findViewById(R.id.mGPS);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.Address);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.Search);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.chat_button);
        b.setVisibility(View.GONE);
    }

    public void view_visible(){
        View b;

        b = findViewById(R.id.mGPS);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.Address);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.Search);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.chat_button);
        b.setVisibility(View.VISIBLE);
    }

    public void detail_clicked(View v){

        SendData tmp_send = new SendData();

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("Reviews",tmp_send.sendData7(review_url,Integer.toString(markid)));

        intent.putExtra("Url",get_url(tmp_marker.getPosition().latitude,tmp_marker.getPosition().longitude));
        intent.putExtra("UserID", ID);
        intent.putExtra("MarkID", Integer.toString(markid));
        intent.putExtra("Category", Integer.toString(category));

        if(userPic.isEmpty())
            intent.putExtra("UserPic", "");
        else {
            intent.putExtra("UserPic", userPic);
        }

        Log.d("Url_send",get_url(tmp_marker.getPosition().latitude,tmp_marker.getPosition().longitude));

        startActivity(intent);
    }

    public void ChattingClicked(View v){
        if(ID.compareTo("Guest") !=0 ) {
            Intent intent = new Intent(this, GroupChatList.class);
            intent.putExtra("ID", ID);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "You have to Log in first", Toast.LENGTH_SHORT).show();
        }
    }


    // delete
    public void delete (double id) {
        db = helper.getWritableDatabase();
        db.delete("MarKer", "id=?", new String[]{String.valueOf(id)});
        Log.i("ddbb","정상적으로 삭제 되었습니다.");
    }

    // select

    public void show_mark(int category) {

        if(category == 5) {
            Cursor c = controller.select_category(category);
            startManagingCursor(c);

            Log.d("ddbb", "get Cursor");

            while (c.moveToNext()) {
                // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
                double lt = c.getDouble(c.getColumnIndex("latitude"));
                double lg = c.getDouble(c.getColumnIndex("longitude"));
                String name = c.getString(c.getColumnIndex("name"));
                int bm = c.getInt(c.getColumnIndex("bm"));

                if (bm == 1) {
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                }
            }

        } else {

            mMap.addMarker(new MarkerOptions().position(new LatLng(126.928117, 37.483867)).title("").zIndex(1.0f));

            Log.d("ddbb", "in show");
            Cursor c = controller.select_category(category);
            startManagingCursor(c);

            Log.d("ddbb", "get Cursor");

            while (c.moveToNext()) {
                // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
                int id = c.getInt(c.getColumnIndex("id"));
                double lt = c.getDouble(c.getColumnIndex("latitude"));
                double lg = c.getDouble(c.getColumnIndex("longitude"));
                int bm = c.getInt(c.getColumnIndex("bm"));

                if (id < 0) {
                    if (bm == 1) {
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                    } else {
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                    }
                } else {
                    if (bm == 1) {
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                    } else {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                    }
                }
            }

            c = controller.select_all_diy();

            while (c.moveToNext()) {
                // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
                double lt = c.getDouble(c.getColumnIndex("latitude"));
                double lg = c.getDouble(c.getColumnIndex("longitude"));
                String name = c.getString(c.getColumnIndex("name"));
                String ct = c.getString(c.getColumnIndex("category"));
                int valid = c.getInt(c.getColumnIndex("valid"));

                int m_ct = Integer.parseInt(ct);

                if(m_ct == category) {
                    if(valid == 1) {
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(new LatLng(lt, lg)).title(name).zIndex(2.0f));
                    }
                }
            }

        }
    }

    void show_stars(double gpa_num) {
        Log.d("gpa" , "values is " + gpa_num);
        ImageView starPoint = (ImageView)findViewById(R.id.gpa_star);

        if(gpa_num < 0.5) {
            starPoint.setImageResource(R.drawable.star_0);
        } else if (gpa_num < 1.5) {
            starPoint.setImageResource(R.drawable.star_1);
        } else if (gpa_num < 2.5) {
            starPoint.setImageResource(R.drawable.star_2);
        } else if (gpa_num < 3.5) {
            starPoint.setImageResource(R.drawable.star_3);
        } else if (gpa_num < 4.5) {
            starPoint.setImageResource(R.drawable.star_4);
        } else {
            starPoint.setImageResource(R.drawable.star_5);
        }
    }

    public String get_url(double latitude, double longitude) {
        Cursor c = controller.select_category(category);

        while (c.moveToNext()) {
            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
            int id = c.getInt(c.getColumnIndex("id"));
            double lt = c.getDouble(c.getColumnIndex("latitude"));
            double lg = c.getDouble(c.getColumnIndex("longitude"));
            String name = c.getString(c.getColumnIndex("name"));
            String url = c.getString(c.getColumnIndex("url"));
            int bm = c.getInt(c.getColumnIndex("bm"));

            if(latitude == lt && longitude == lg) {
                return url;
            }
        }

        return "http://i.imgur.com/NmPyWw4.png";
    }

    public String get_name(double latitude, double longitude) {
        Cursor c = controller.select_category(category);

        while (c.moveToNext()) {
            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
            int id = c.getInt(c.getColumnIndex("id"));
            double lt = c.getDouble(c.getColumnIndex("latitude"));
            double lg = c.getDouble(c.getColumnIndex("longitude"));
            String name = c.getString(c.getColumnIndex("name"));
            String url = c.getString(c.getColumnIndex("url"));
            int bm = c.getInt(c.getColumnIndex("bm"));

            if(latitude == lt && longitude == lg) {
                return name;
            }
        }

        return "No Name";
    }

    public String parse_gpa(String json) {

        String gpa = " ";

        try {
            JSONArray JA = new JSONArray(json);

            for (int i = 0; i < JA.length(); i++) {

                JSONObject c = JA.getJSONObject(i);
                Log.d("gpa", "All contents : " + c);
                gpa = c.getString("GPA");
            }

            Log.d("gpa", gpa);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }

        return gpa;
    }


    /*
     * anysc task 방식으로 php와의 통신(데이터를 받아옴)
     */
    public void getData(String url) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            String myJSON;

            public String getMyJSON(){
                return  myJSON;
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream() , "UTF-8"));
                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url);

        //parse_board(g.getMyJSON());
    }

}