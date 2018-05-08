package raydom.use_map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LeeSunjun on 2017-09-18.
 */

public class AddBoardingActivity extends Activity {

    String lt;
    String lg;

    EditText title;
    EditText content;

    String url;

    ArrayList<Integer> id_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_ui);

        SendData board_send = new SendData();

        board_send.sendData11("http://52.79.121.208/board/text_read.php","11");

        id_list = new ArrayList<Integer>();

        Intent intent = getIntent();

        lt = intent.getStringExtra("here_lt");
        lg = intent.getStringExtra("here_lg");

        String json = board_send.sendData2("http://52.79.121.208/board/board_read.php",lt,lg);

        Log.d("broad","send over : " +json);

        parse_board(json);

        url = "http://52.79.121.208/board/board_write.php";

        LinearLayout LL = (LinearLayout)findViewById(R.id.board_Tcontent);
        LL.setVisibility(View.GONE);
        LL = (LinearLayout)findViewById(R.id.board_T_add);
        LL.setVisibility(View.GONE);

    }

    public void add_board(View v) {
        EditText title_add = (EditText) findViewById(R.id.board_title_add);
        String title = title_add.getText().toString();

        EditText userID_add = (EditText) findViewById(R.id.board_writer_add);
        String userID = userID_add.getText().toString();

        EditText content_add = (EditText) findViewById(R.id.board_content_add);
        String content = content_add.getText().toString();

        url = "http://52.79.121.208/board/board_write.php";

        SendData send_board = new SendData();
        send_board.sendData10(url,lt,lg,title,userID,content);

        initialize();
    }

    public void cancel_board(View v) {
        initialize();
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

    public void parse_board(String json) {

        try {
            JSONArray JA = new JSONArray(json);
            Log.d("json", "All contents : " + JA);

            for (int i = 0; i < JA.length(); i++) {

                JSONObject c = JA.getJSONObject(i);

                int id = Integer.parseInt(c.getString("boardID"));
                id_list.add(id);

                String title = c.getString("main");

                set_btn(i,title);

                Log.d("board","id : "+id_list.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }
    }

    public void parse_content(String json) {

        String userID = "Nothing";
        String title = "Nothing";
        String content = "Nothing";

        try {
            JSONArray JA = new JSONArray(json);
            Log.d("json", "All contents : " + JA);

            for (int i = 0; i < JA.length(); i++) {

                JSONObject c = JA.getJSONObject(i);

                userID = c.getString("userID");
                title = c.getString("main");
                content = c.getString("text");

                Log.d("board","userid : "+userID);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JUN", e.toString());
        }

        TextView title_txt = (TextView)findViewById(R.id.board_title);
        title_txt.setText(title);

        TextView userID_txt = (TextView)findViewById(R.id.board_writer);
        userID_txt.setText(userID);

        TextView content_txt = (TextView)findViewById(R.id.board_content);
        content_txt.setText(content);

        LinearLayout LL = (LinearLayout)findViewById(R.id.board_title_list);
        LL.setVisibility(View.GONE);
        LL = (LinearLayout)findViewById(R.id.board_Tcontent);
        LL.setVisibility(View.VISIBLE);
    }

    public void set_btn(int count , String title) {
        if(count == 0) {
            Button btn = (Button)findViewById(R.id.btn1);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 1) {
            Button btn = (Button)findViewById(R.id.btn2);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 2) {
            Button btn = (Button)findViewById(R.id.btn3);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 3) {
            Button btn = (Button)findViewById(R.id.btn4);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 4) {
            Button btn = (Button)findViewById(R.id.btn5);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 5) {
            Button btn = (Button)findViewById(R.id.btn6);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 6) {
            Button btn = (Button)findViewById(R.id.btn7);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        } else if (count == 7) {
            Button btn = (Button)findViewById(R.id.btn8);
            btn.setVisibility(View.VISIBLE);

            btn.setText(title);
        }
    }

    public void btn1_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(0));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn2_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(1));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn3_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(2));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn4_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(3));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn5_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(4));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn6_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(5));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn7_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(6));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void btn8_clicked(View v) {
        SendData board_send = new SendData();

        String boardID = Integer.toString(id_list.get(7));

        String json = board_send.sendData11("http://52.79.121.208/board/text_read.php",boardID);

        Log.d("board",json);

        parse_content(json);
    }

    public void add_board_clicked(View v) {
        LinearLayout LL = (LinearLayout)findViewById(R.id.board_title_list);
        LL.setVisibility(View.GONE);
        LL = (LinearLayout)findViewById(R.id.board_Tcontent);
        LL.setVisibility(View.GONE);
        LL = (LinearLayout)findViewById(R.id.board_T_add);
        LL.setVisibility(View.VISIBLE);
    }

    public void back_clicked(View v) {
        LinearLayout LL = (LinearLayout)findViewById(R.id.board_title_list);
        LL.setVisibility(View.VISIBLE);
        LL = (LinearLayout)findViewById(R.id.board_Tcontent);
        LL.setVisibility(View.GONE);
        LL = (LinearLayout)findViewById(R.id.board_T_add);
        LL.setVisibility(View.GONE);
    }
}