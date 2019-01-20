package xxm.com.androidstudy1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import xxm.com.androidstudy1.util.Const;
import xxm.com.androidstudy1.util.MyHttpClient;

public class MainActivity extends AppCompatActivity{

    private ListView meetingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        meetingListView = findViewById(R.id.MeetingListView);
        meetingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("aaaaaa");
            }
        });
        final Map<String, String> params = new HashMap<>();
        params.put("currentGroupId", "1");
        MyHttpClient.asyncGet(Const.GET_MEETING_LIST, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String res = body.string();
                Message message = handler.obtainMessage();
                message.obj = res;
                handler.sendMessage(message);
            }
        });
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Object obj = msg.obj;
            JSONObject json = JSONObject.parseObject(obj.toString());
            JSONArray arr = json.getJSONArray("meetingList");
            String[] data = new String[arr.size()];
            for(int i=0 ; i<arr.size() ; i++){
                JSONObject temp = arr.getJSONObject(i);
                String title = temp.getString("title");
                data[i] = title;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, data);
            meetingListView.setAdapter(adapter);
        }
    };
    /*public void onClick(View view) {
        System.out.println("aaaaaaaaaaaaa");
        switch (view.getId()){
            case R.id.button:
                String name,pwd;
                String username1 = "codingxxm";
                String userpwd1 = "wojiushiwo";

                name = username.getText().toString();
                pwd = password.getText().toString();

                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                if (name.equals(username1) && pwd.equals(userpwd1)){
                    Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }else {
                    if((name==null||name.isEmpty())&&(pwd==null||pwd.isEmpty())){
                        Toast.makeText(this,"用户名密码为空",Toast.LENGTH_SHORT).show();
                    }else{
                        builder.setMessage("登陆失败");
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
                break;
            default:
                break;
        }
    }*/
}
