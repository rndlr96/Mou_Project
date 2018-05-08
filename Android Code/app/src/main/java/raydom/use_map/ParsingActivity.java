package raydom.use_map;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LeeSunjun on 2017-05-27.
 */

    /*
     *php server 로 부터 data를 받아옴
     */

public class ParsingActivity extends Activity {

    SQLiteDatabase db; // MoU DB
    DBHandler controller; // DB Helper for MoU

    String myJSON;
    JSONArray mark_info = null;

    int total = 0;

    ArrayList<String> tmp_json = new ArrayList<String>();

    private static final String TAG_LT = "latitude";
    private static final String TAG_LG = "longitude";
    private static final String TAG_TITLE = "name";
    private static final String TAG_PNG = "url";
    private static final String TAG_ID ="id";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        controller = new DBHandler(getApplicationContext());

        //getData("http://52.79.121.208/publicdata/toilet.php",1);

        //getData("http://52.79.121.208/publicdata/wifi.php",2);

        getData("http://52.79.121.208/publicdata/smoking.php",3);

        //getData("http://52.79.121.208/publicdata/statue.php",4);

        initialize();
    }


    /*
     * csv파일로 부터 데이터 받아옴
     */
    public void parsing_data(String file_name,int category) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open(file_name)));

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {

                Log.d("ddbb", " 1 : " + nextLine[0] + " 2 : " + nextLine[1] + " 3 : " + category);

                controller.insert(total, Double.parseDouble(nextLine[1]), Double.parseDouble(nextLine[0]), "", "http://i.imgur.com/cWOF3ct.jpg", 0, category);

                total++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsing_data2(String file_name,int category) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open(file_name)));

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {

                controller.insert(total, Double.parseDouble(nextLine[2]), Double.parseDouble(nextLine[1]), "", "", 0, category);

                total++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsing_diy() {

    }

    /*
     *php server 로부터 json 형식으로 데이터를 받아올때 tag 구분하여 DB에 저장
     */
    public void parse_php(int category) {

        Log.d("JUN", "length 0    " + myJSON);
        Log.d("JUN", "length 1    " + myJSON.length());

        try {
                Log.d("JUN", "length 2   " + myJSON.length());
                JSONArray JA = new JSONArray(myJSON);
                //JSONObject JA = new JSONObject(myJSON);
                Log.d("JUN", "length 3    " + myJSON.length());
                //JSONArray JA = new JSONArray(tmp_json.get(j));

                mark_info = JA;

                Log.d("JUN", "length 4    " + JA);

                for (int i = 0; i < mark_info.length(); i++) {

                    JSONObject c = mark_info.getJSONObject(i);
                    String ID = c.getString(TAG_ID);
                    String LT = c.getString(TAG_LT);
                    String LG = c.getString(TAG_LG);
                    String NAME = c.getString(TAG_TITLE);
                    String PNG = c.getString(TAG_PNG);

                    //total will be change to ID from server !!!
                    controller.insert(Integer.parseInt(ID), Double.parseDouble(LT), Double.parseDouble(LG), NAME, PNG, 0, category);
                    total++;

                    if(i < 30)
                        Log.d("total123",total + " : " + LT + " " + LG + " " + NAME + " " + PNG + " ");

                }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }
    }


    /*
     * anysc task 방식으로 php와의 통신(데이터를 받아옴)
     */
    public void getData(String url, final int category) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            String tmp;

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
                        //Log.d("JUN", "length 0    " + json);
                        sb.append(json);
                    }

                    //Log.d("JUN", "length 1    " + sb.toString());

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                //Log.d("JUN", "length 0    " + myJSON);
                parse_php(category);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);

    }

    private void initialize(){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0,0);
    }
}
