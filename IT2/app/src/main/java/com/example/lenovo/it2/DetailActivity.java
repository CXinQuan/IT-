package com.example.lenovo.it2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.it2.sl.Person;

public class DetailActivity extends Activity {
    private TextView tvDetailName;
    private TextView tvDetailNumber;
    private TextView tvDetailYewu;
    private Button btn_finish;
    private TextView tv_time;

    private void initView() {
        tvDetailName = (TextView) findViewById(R.id.tv_detail_name);
        tvDetailNumber = (TextView) findViewById(R.id.tv_detail_number);
        tvDetailYewu = (TextView) findViewById(R.id.tv_detail_yewu);
        btn_finish=(Button)findViewById(R.id.btn_finish);
        tv_time=(TextView)findViewById(R.id.tv_time);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailactivity);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        final String phonenumber = intent.getStringExtra("phonenumber");
        String yewu = intent.getStringExtra("yewu");
        final String id=intent.getStringExtra("id");
        String time=intent.getStringExtra("time");
        initView();
        tvDetailName.setText(name);
        tvDetailNumber.setText(phonenumber);
        tvDetailYewu.setText(yewu);
        tv_time.setText(time);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //提交到 云端 修改操作
                //delete(persons.get(position).getObjectId());
                Intent i=getIntent();
                int position=i.getIntExtra("position",0);
                Intent intent=new Intent();
                intent.putExtra("id",id);
                intent.putExtra("position",position);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }



}
