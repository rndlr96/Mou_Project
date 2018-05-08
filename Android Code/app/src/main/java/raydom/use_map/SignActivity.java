package raydom.use_map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by LEe_SunJun on 2017-05-18.
 */

/*
 *sign_up button 이 눌린경우 회원가입을 위한 web site 호출
 */
public class SignActivity extends Activity {
    String name;
    String id;
    String pw;
    String mailAddress;
    String birthdate;
    String gender;
    String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_view);
    }

    private void initialize(){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0,300);
    }

    public void male_checked(View v) {
        Button button;

        button = (Button)findViewById(R.id.male_button);
        button.setBackgroundResource(R.drawable.gray_filled_button);

        button = (Button)findViewById(R.id.female_button);
        button.setBackgroundResource(R.drawable.empty_button);

        gender = "m";
    }

    public void female_checked(View v) {
        Button button;

        button = (Button)findViewById(R.id.male_button);
        button.setBackgroundResource(R.drawable.empty_button);

        button = (Button)findViewById(R.id.female_button);
        button.setBackgroundResource(R.drawable.gray_filled_button);

        gender = "f";
    }

    public void sign_clicked(View v) {
        SendData send;
        send = new SendData();

        EditText edit;

        String pw_cf;

        edit = (EditText)findViewById(R.id.id_in);
        id = edit.getText().toString();
        edit = (EditText)findViewById(R.id.pw_in);
        pw = edit.getText().toString();
        edit = (EditText)findViewById(R.id.pw_check_in);
        pw_cf = edit.getText().toString();
        edit = (EditText)findViewById(R.id.mail_in);
        mailAddress = edit.getText().toString();
        edit = (EditText)findViewById(R.id.birth_in);
        birthdate = edit.getText().toString();
        edit = (EditText)findViewById(R.id.name_in);
        name = edit.getText().toString();
        edit = (EditText)findViewById(R.id.phone_in);
        phone_number = edit.getText().toString();

        send.sendData5("http://52.79.121.208/join/temp.php",name,id,pw,mailAddress,birthdate,gender,phone_number);
        Log.d("1515","signal : "+send.get_check());

        while(send.get_check() == -1) {
            ;
        }

        if (send.get_check() == 0) {
            Toast.makeText(SignActivity.this, "INTERNET CONNECT ERROR, TRY AGAIN", Toast.LENGTH_SHORT).show();
            send.reset_checking();
        } else if (!pw.equals(pw_cf)){
            Toast.makeText(SignActivity.this, "PLEASE CHECK YOUR PW & CONFIRM PW", Toast.LENGTH_SHORT).show();
        } else if (send.get_check() == 203) {
            Toast.makeText(SignActivity.this, "YOUR ID IS ARLEADY EXIST, PLEASE CHANGE YOUR ID", Toast.LENGTH_SHORT).show();
            send.reset_checking();
        } else if (send.get_check() == 200) {
            Toast.makeText(SignActivity.this, "SIGN UP COMPLETE", Toast.LENGTH_SHORT).show();
            send.reset_checking();
            initialize();
        }
    }
}
