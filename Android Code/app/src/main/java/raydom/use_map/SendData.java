package raydom.use_map;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * php server로 데이터를 전송
 */

public class SendData {
    int type = -1; // 1 : toilet
                   // 2 : wifi
                   // 3 : smoking
                   // 4 : landmark

    String LG_TAG = "longitude"; //longitude
    String LT_TAG = "latitude"; //latitude

    String uID_TAG = "ID"; //id
    String uPW_TAG = "PW"; //pw

    String mark_name_TAG = "space_name"; //name
    String mark_option1_TAG = ""; //option1
    String mark_option2_TAG = ""; //option2
    String mark_image_TAG = "image"; //file

    String comment_TAG = ""; //comment
    String grade_TAG = ""; //grade

    String sign_name_TAG = "name";
    String sign_id_TAG = "id";
    String sign_pw_TAG = "pwd";
    String sign_mail_TAG = "email";
    String sign_birth_TAG = "birthDay";
    String sign_gender_TAG = "sex";
    String sign_phone_TAG = "PhoneNumber";

    String user_ID_TAG = "userID";
    String mark_ID_TAG = "markID";
    String gpa_TAG = "GPA";
    String review_TAG = "review";

    String userID_TAG = "userID";
    String MarkerName_TAG = "MarkerName";

    String title_TAG = "main";
    String content_TAG = "text";

    String valid_TAG = "valid";
    String category_TAG = "category";

    int check = -1;

    void reset_checking() {
        check = -1;
    }

    SendData(){}

    SendData(int type) {
        this.type = type;
    }

    public int get_check(){
        return check;
    }

    //send ID,PW data
    public String sendData1(String url, String id, String pw){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String res = " ";

            int rcode = -1;

            public String get_result() {
                return res;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result4 : " + params[1]);
                    byte[] outputInBytes = params[1].getBytes("UTF-8");

                    Log.d("Ray","result5 : " + outputInBytes);

                    OutputStream os = conn.getOutputStream();

                    os.write(outputInBytes);
                    os.flush();
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    res = response.toString();
                    Log.d("Ray","result 2 : "+res);

                    rcode = retCode;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(uID_TAG, id);
                jsonObject.put(uPW_TAG, pw);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","result 3 : "+json);

            util.execute(url,json);

            while(util.rcode == -1) {
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = "["+util.get_result()+"]";

        return result;
    }

    //send LT,LG data
    public String sendData2(String url, String latitude, String longitude){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String result = "";

            int retCode = -1;

            public String get_result() {
                return result;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    result = res;

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(LT_TAG, latitude);
                jsonObject.put(LG_TAG, longitude);

                Log.d("Debug11",jsonObject.toString());
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            util.execute(url,json);

            while(util.get_result().isEmpty()){
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = util.get_result();

        return result;
    }

    //send LT,LG and options for adding marker
    public String sendData3(String url, String latitude, String longitude, String name, String op1, String op2, String file){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String result = "1";

            int rCode = -1;

            public String get_Result() {
                return result;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(20000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result_url : " + params[1] );

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.flush();
                    os.close();

                    Log.d("Ray","result_url2 : " + params[1].getBytes("UTF-8") );

                    int retCode = conn.getResponseCode();

                    rCode = retCode;
                    check = retCode;

                    Log.d("Ray","retcode : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();


                    String res = response.toString();

                    result = res;

                    Log.d("Ray","result : "+result);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {

                Log.d("Url","2");
                jsonObject.put(LT_TAG, latitude);
                jsonObject.put(LG_TAG, longitude);
                jsonObject.put(mark_name_TAG, name);

                mark_option1_TAG = "agency";
                mark_option2_TAG = "locked";

                jsonObject.put(mark_option1_TAG, op1);
                jsonObject.put(mark_option2_TAG, op2);
                Log.d("Url","5");

                Log.d("Url","3");
                if(type == 1) {
                    mark_option1_TAG = "disabled";
                    mark_option2_TAG = "unisex";

                    jsonObject.put(mark_option1_TAG, op1);
                    jsonObject.put(mark_option2_TAG, op2);
                    Log.d("Url","4");
                } else if(type == 2) {
                    mark_option1_TAG = "agency";
                    mark_option2_TAG = "locked";

                    jsonObject.put(mark_option1_TAG, op1);
                    jsonObject.put(mark_option2_TAG, op2);
                    Log.d("Url","5");
                } else if(type == 3) {
                    mark_option1_TAG = "ashytray";
                    mark_option2_TAG = "booth";

                    jsonObject.put(mark_option1_TAG, op1);
                    jsonObject.put(mark_option2_TAG, op2);
                    Log.d("Url","6");
                } else if(type == 4) {
                    mark_option1_TAG = "explanation";

                    jsonObject.put(mark_option1_TAG, op1);
                    Log.d("Url","7");
                }

                Log.d("Url","4");
                jsonObject.put(mark_image_TAG,file);
                Log.d("Url","5");

            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            util.execute(url,json);

            while(get_check() == -1) {
                ;
            }

            Log.d("Url","10");

        } catch (Exception e) {
            e.printStackTrace();
        }

        while(get_check() == -1) {
            ;
        }

        String result = "1";

        while(result == "1")
            result = util.get_Result();

        Log.d("DIY","final result" + util.get_Result());

        return result;
    }

    //send markID for getting gpa
    public String sendData4(String url, int id){

        String rt;

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String res = " ";
            int rcode = -1;

            public String get_result() {
                return res;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;

                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                    rcode = retCode;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(mark_ID_TAG, id);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            util.execute(url,json);

            while(util.rcode == -1) {
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return util.get_result();
    }

    //send information about users for sign up
    public void sendData5(String url, String name, String id, String pw, String mail, String birth, String gender, String phone){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result_url : " + params[1] );

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.flush();
                    os.close();

                    Log.d("Ray","result_url2 : " + params[1].getBytes("UTF-8") );

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(sign_name_TAG, name);
                jsonObject.put(sign_id_TAG, id);
                jsonObject.put(sign_pw_TAG, pw);
                jsonObject.put(sign_mail_TAG, mail);
                jsonObject.put(sign_birth_TAG, birth);
                jsonObject.put(sign_gender_TAG, gender);
                jsonObject.put(sign_phone_TAG, phone);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            new HttpUtil().execute(url,json);
            Log.d("Url","10 : " + json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send review
    public String sendData6(String url, String markID, String userID, String gpa, String review) {

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String res = " ";

            int rcode = -1;

            public String get_result() {
                return res;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result4 : " + params[1]);
                    byte[] outputInBytes = params[1].getBytes("UTF-8");

                    Log.d("Ray","result5 : " + outputInBytes);

                    OutputStream os = conn.getOutputStream();

                    os.write(outputInBytes);
                    os.flush();
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    res = response.toString();
                    Log.d("Ray","result 2 : "+res);

                    rcode = retCode;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(user_ID_TAG, userID);
                jsonObject.put(mark_ID_TAG, markID);
                jsonObject.put(gpa_TAG, gpa);
                jsonObject.put(review_TAG,review);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","result 3 : "+json);

            util.execute(url,json);

            while(util.rcode == -1) {
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return util.get_result();
    }

    //send markID for getting reviews
    public String sendData7(String url, String markID) {

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String res = " ";

            int rcode = -1;

            public String get_result() {
                return res;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result4 : " + params[1]);
                    byte[] outputInBytes = params[1].getBytes("UTF-8");

                    Log.d("Ray","result5 : " + outputInBytes);

                    OutputStream os = conn.getOutputStream();

                    os.write(outputInBytes);
                    os.flush();
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    res = response.toString();
                    Log.d("Ray","result 2 : "+res);

                    rcode = retCode;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(mark_ID_TAG, markID);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","result 3 : "+json);

            util.execute(url,json);

            while(util.rcode == -1) {
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return util.get_result();
    }

    //send LT,LG ,name for personal marker
    public void sendData8(String url, String latitude, String longitude, String name, String userID){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(userID_TAG, userID);
                jsonObject.put(LT_TAG, latitude);
                jsonObject.put(LG_TAG, longitude);
                jsonObject.put(MarkerName_TAG, name);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","upload : " + json);

            new HttpUtil().execute(url,json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send UserID for getting personal marker
    public String sendData9(String url, String userID){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String res = " ";

            int rcode = -1;

            public String get_result() {
                return res;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","personal4 : " + params[1]);
                    byte[] outputInBytes = params[1].getBytes("UTF-8");

                    Log.d("Ray","personal5 : " + outputInBytes);

                    OutputStream os = conn.getOutputStream();

                    os.write(outputInBytes);
                    os.flush();
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","personal 1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();

                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    res = response.toString();
                    Log.d("Ray","personal 2 : "+res);

                    rcode = retCode;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(user_ID_TAG, userID);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","personal 3 : "+json);

            util.execute(url,json);

            while(util.rcode == -1) {
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = util.get_result();

        return result;
    }

    //send LT, LG,ID, content for board
    public void sendData10(String url, String latitude, String longitude, String title, String userID, String content){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(LT_TAG, latitude);
                jsonObject.put(LG_TAG, longitude);
                jsonObject.put(title_TAG,title);
                jsonObject.put(content_TAG,content);
                jsonObject.put(userID_TAG, userID);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","upload : " + json);

            new HttpUtil().execute(url,json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send BoardID for getting board contents
    public String sendData11(String url, String boardID){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String result = "";

            int retCode = -1;

            public String get_result() {
                return result;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    result = res;

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("boardID", boardID);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            util.execute(url,json);

            while(util.get_result().isEmpty()){
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = util.get_result();

        return result;
    }

    //send integer for checking diy is true or false
    public String sendData12(String url, String markID, String category, String valid){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            String result = "";

            int retCode = -1;

            public String get_result() {
                return result;
            }

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    result = res;

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        HttpUtil util = new HttpUtil();

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(valid_TAG, valid);
                jsonObject.put(mark_ID_TAG,markID);
                jsonObject.put(category_TAG,category);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            util.execute(url,json);

            while(util.get_result().isEmpty()){
                ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = util.get_result();

        return result;
    }
}
