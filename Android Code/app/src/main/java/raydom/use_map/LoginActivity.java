package raydom.use_map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

// finish

public class LoginActivity extends Activity {
    ArrayList<String> m_ID = new ArrayList<String>();
    ArrayList<String> m_PW = new ArrayList<String>();

    SendData send_login;

    DBHandler controller;

    String lg;
    String lt;
    String name;

    String myJSON;

    EditText ID;
    EditText PW;

    String myName;
    String myID;

    private BackPressCloseHandler backPressCloseHandler;

    /*
    * ID,PW를 입력하는 EditText control & 코드상의 변수와 연결
    * php server 정보를 전달해는 객체 SendData 생성(type 지정)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        ID = (EditText)findViewById(R.id.id_edit);
        PW = (EditText)findViewById(R.id.pw_edit);

        send_login = new SendData();

        controller = new DBHandler(getApplicationContext());

        parsing_data("login_info.csv");
    }

    /*
    * 뒤로가기 버튼 비활성화
     */

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        //backPressCloseHandler.onBackPressed();
    }

    /*
    *login 버튼 클릭시, 아이디와 비밀번호 존재여부 및 확인후 로그인 or 로그인 실패
     */
    public void login_clicked(View v){

        boolean checking = check_id_pw();

        if(checking) {
            Intent intent = new Intent();
            intent.putExtra("ID",myID);
            intent.putExtra("NAME", myName);
            setResult(RESULT_OK,intent);

            controller.insert_login(myID,myName,"");

            String url = "http://52.79.121.208/favorite/private_read.php";

            SendData personal_getting = new SendData();

            String personal_data = personal_getting.sendData9(url,myID);

            Log.d("personal" , personal_data);

            parse_personal(personal_data);

            initialize();
        }
    }

    /*
    *ID PW 없이 로그인하는 기능(private data & marker add 불가)
     */

    public void guest_clicked(View v){
        Toast.makeText(LoginActivity.this,"Guest Mode",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("ID","Guest");
        intent.putExtra("NAME", "Guest");

        controller.insert_login("Guest","Guest","");

        setResult(RESULT_OK,intent);

        initialize();
    }

    /*
    *회원가입을 위한 web site pop up
     */
    public void sign_clicked(View v){
        //php통신을 위한 web site 들어가기
        startActivity(new Intent(this,SignActivity.class));
    }

    /*
    *입력된 ID,PW가 올바른지 사
    *login_Clicked 에서 호출
     */
    public boolean check_id_pw() {         //ID,PW 검사
        parse_id_name(send_login.sendData1("http://52.79.121.208/login.php", ID.getText().toString(), PW.getText().toString()));

        while(send_login.get_check() == -1) {
            ;
        }

        if (send_login.get_check() == 0) {
            Toast.makeText(LoginActivity.this, "INTERNET CONNECT ERROR, TRY AGAIN", Toast.LENGTH_SHORT).show();
            send_login.reset_checking();
            return false;
        } else if (send_login.get_check() == 200) {
            send_login.reset_checking();
            return true;
        } else if (send_login.get_check() == 204) {
            send_login.reset_checking();
            Toast.makeText(LoginActivity.this, "Please check your ID & PW", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return false;
        }
    }

    /*
    *test code
     */
    public void parsing_data(String file_name){
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open(file_name)));

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                m_ID.add(nextLine[1]);
                m_PW.add(nextLine[2]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse_id_name(String json) {

        Log.d("gpa","contents : " + 123);

        try {
            JSONArray JA = new JSONArray(json);

            for (int i = 0; i < JA.length(); i++) {

                JSONObject c = JA.getJSONObject(i);
                Log.d("gpa","All contents : " + c);
                myID = c.getString("id");
                myName = c.getString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }
    }

    public void parse_personal(String json) {

        try {
            JSONArray JA = new JSONArray(json);

            Log.d("personal" , "length : " + JA.length());

            for (int i = 0; i < JA.length(); i++) {

                JSONObject c = JA.getJSONObject(i);
                Log.d("personal","All contents : " + c);

                lg = c.getString("longitude");
                lt = c.getString("latitude");
                name = c.getString("MarkerName");

                Log.d("personal", "name : " + name);

                controller.insert_personal(Double.parseDouble(lt),Double.parseDouble(lg),"");

                Log.d("personal", "complete");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }

        Log.d("personal", "end");
    }

    /*
    *activity 종료
     */
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
