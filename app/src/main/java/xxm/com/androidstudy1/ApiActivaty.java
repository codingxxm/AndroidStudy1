package xxm.com.androidstudy1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import xxm.com.androidstudy1.util.HttpRequestor;

public class ApiActivaty extends AppCompatActivity {

    private String jurl = "https://www.sojson.com/open/api/weather/json.shtml?city=";

    private HttpRequestor requestor = new HttpRequestor();


    private String content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        initView();
        bindGetMsg();
    }

    private void initView(){
        Intent intent = getIntent();
        final String city = intent.getStringExtra("city");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String rs = getMsg(city);
                System.out.println(rs);
                if (!rs.isEmpty()) {
                    JSONObject object = JSONObject.parseObject(rs);
                    JSONObject o1 = object.getJSONObject("data");
                    JSONObject o2 = o1.getJSONObject("yesterday");
                    String str = o2.getString("high") + ":" + o2.getString("low");
                    Message message = handler.obtainMessage();
                    message.obj = str;
                    handler.sendMessage(message);
                    //handler.sendMessageDelayed(message, 2000);
                }
            }
        }).start();
    }

    private String getMsg(String city){
        String result  = "";
        try {
            result = requestor.doGet(jurl + city);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Object object = msg.obj;
            TextView bjtq = (TextView) findViewById(R.id.bjtq);
            bjtq.setText(object.toString());
        }
    };

    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Object object = msg.obj;
            TextView cityMsg = (TextView) findViewById(R.id.cityMsg);
            cityMsg.setText(object.toString());
        }
    };

    private void bindGetMsg(){
        Button button = (Button)findViewById(R.id.getCity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputCity = (EditText)findViewById(R.id.inputCity);
                final String editMsg = inputCity.getText().toString();
                if(editMsg.isEmpty()||editMsg==null){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ApiActivaty.this);
                    builder.setMessage("不能为空啊");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String rs = getMsg(editMsg);
                            System.out.println(rs);
                            if (!rs.isEmpty()) {
                                JSONObject object = JSONObject.parseObject(rs);
                                JSONObject o1 = object.getJSONObject("data");
                                JSONObject o2 = o1.getJSONObject("yesterday");
                                String str = editMsg + ":" + o2.getString("high") + ":" + o2.getString("low");
                                Message message = handler.obtainMessage();
                                message.obj = str;
                                handler1.sendMessage(message);
                                //handler.sendMessageDelayed(message, 2000);
                            }
                        }
                    }).start();
                }
            }
        });
    }
}
