package xxm.com.androidstudy1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class IndexActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        TextView textView = (TextView)findViewById(R.id.textView4);
        textView.setText("欢迎你"+name);

        String[] data  = {"AAAA","BBBB","CCCC","DDDDD","EEEE","FFFFF"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(IndexActivity.this, R.layout.support_simple_spinner_dropdown_item, data);
        final ListView listView = (ListView)findViewById(R.id.ListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String)listView.getItemAtPosition(i);
                if(value.equals("DDDDD")){
                    Intent intent1 = new Intent(IndexActivity.this,ApiActivaty.class);
                    intent1.putExtra("city","北京");
                    startActivity(intent1);
                }
            }
        });
    }
}
