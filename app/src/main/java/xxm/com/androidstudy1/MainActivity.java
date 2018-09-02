package xxm.com.androidstudy1;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn;
    private EditText username;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        username = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText4);
        loginBtn = (Button) findViewById(R.id.button);
        loginBtn.setOnClickListener(this);
        loginBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("MainActivity", "长按了按钮");
                Toast.makeText(MainActivity.this,"长按了按钮",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
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
    }
}
