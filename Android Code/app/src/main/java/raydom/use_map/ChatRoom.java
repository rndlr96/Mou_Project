package raydom.use_map;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017-09-15.
 */

public class ChatRoom extends AppCompatActivity {

    ListView listView;
    private DatabaseReference root ;
    private String temp_key;
    private String user_name,room_name;
    private String time;
    SimpleDateFormat sdf;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        sdf = new SimpleDateFormat("MM-dd HH:mm");
        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        Button fab = (Button) findViewById(R.id.fab);
        setTitle(room_name);

        listView = (ListView) findViewById(R.id.list);

        final EditText input = (EditText) findViewById(R.id.input);

        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        showAllOldMessgaes();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatRoom.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String,Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference message_root = root.child(temp_key);
                    Map<String,Object> map2 = new HashMap<String, Object>();
                    map2.put("name",user_name);
                    map2.put("msg",input.getText().toString());
                    time = sdf.format(new Date()).toString();
                    map2.put("time",time);
                    message_root.updateChildren(map2);
                    input.setText("");
                }
            }
        });
    }
    private void showAllOldMessgaes(){
        MessageAdapter adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_in_message,root);
        listView.setAdapter(adapter);
    }
    public String getUserName(){
        return user_name;
    }
}
