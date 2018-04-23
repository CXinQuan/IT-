package com.example.lenovo.it2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.it2.sl.MyLinearLayoutManager;
import com.example.lenovo.it2.sl.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FirstActivity extends Activity {
    private ListView listView;
    private List<Person> persons;
    private MyListAdapter myListAdapter;
    private Button refresh;
    private ProgressBar progressBar;
    private ScheduledExecutorService scheduledExecutorService;
    private LinearLayout ll_guanggao ;
    private TextView tv_guanggao ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        init();
        ll_guanggao = (LinearLayout) findViewById(R.id.ll_guanggao);
        tv_guanggao = (TextView) findViewById(R.id.tv_guanggao);
        donghua(tv_guanggao);
    }

    @Override
    protected void onStart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new RefershTask(), 0, 4, TimeUnit.SECONDS);
        super.onStart();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //重新刷新界面
                    //  delete(data.getStringExtra("id"));
                    //修改IsOK
                    set_OK(data.getStringExtra("id"));
                    // persons.remove(data.getIntExtra("position",0));
                    int position = data.getIntExtra("position", 0);
                    persons.get(position).setIsOK(true);
                    myListAdapter.notifyDataSetChanged();
                    listView.setSelection(position);
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        persons = new ArrayList<Person>();
        listView = (ListView) findViewById(R.id.listView_first);
        Bmob.initialize(this, "4a4ca1fb561ab94dcb1cf7dae60ef009");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        query();
        refresh = (Button) findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                query();
                myListAdapter.notifyDataSetChanged();
            }
        });
        //由于persons的 数据是从网络上获取的，其操作是在子线程中完成的，在init方法是在主线程中完成的
        //所以当子线程在获取数据时（query（方法）），属于耗时操作，所以，主线程会继续执行，但是此时persons还没获取到，所以setAdaper中的数据是为空的
        //要使setAdaper中的数据不为空，则应该确保query子线程完成之后才setAdapter
        // myListAdapter=new MyListAdapter(persons);
        // listView.setAdapter(myListAdapter);
        if (persons == null) {
            Toast.makeText(this, "系统网络错误,使用初始数据进行测试！", Toast.LENGTH_SHORT).show();
            initdata();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FirstActivity.this, DetailActivity.class);
                intent.putExtra("name", persons.get(position).getName());
                intent.putExtra("phonenumber", persons.get(position).getPhonenumber());
                intent.putExtra("yewu", persons.get(position).getYewu());
                intent.putExtra("position", position);
                intent.putExtra("id", persons.get(position).getObjectId());
                intent.putExtra("time", persons.get(position).getCreatedAt());
                startActivityForResult(intent, 1);
            }
        });
    }

    class MyListAdapter extends BaseAdapter {
        private List<Person> person_list;

        public MyListAdapter(List<Person> person_list) {
            this.person_list = person_list;
        }

        public int getCount() {
            //  Log.d("person_list.size()的数量是",person_list.size()+"");
            return person_list.size();
        }

        public Object getItem(int position) {
            return person_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(FirstActivity.this).inflate(R.layout.item_layout, null);
            }
            holder = ViewHolder.getViewHolder(convertView);

            holder.name.setText(person_list.get(position).getName() + "");
            if (person_list.get(position).getIsOK()) {
                holder.tv_issongda.setText("已送达");
            } else {
                holder.tv_issongda.setText("未送达");
            }

            AnimatorSet animatorSet = new AnimatorSet();//组合动画
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(convertView, "scaleX", 0f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(convertView, "scaleY", 0f, 1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(convertView, "rotationX", 0, 180, 0);
            animatorSet.setDuration(400);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.playTogether(scaleX, scaleY, rotation);//多个动画同时开始
            animatorSet.start();

            return convertView;
        }
    }

    static class ViewHolder {
        TextView name;
        TextView tv_issongda;

        public ViewHolder(View converView) {
            name = (TextView) converView.findViewById(R.id.tv_name);
            tv_issongda = (TextView) converView.findViewById(R.id.tv_issongda);
        }

        public static ViewHolder getViewHolder(View converView) {
            ViewHolder holder = (ViewHolder) converView.getTag();
            if (holder == null) {
                holder = new ViewHolder(converView);
                converView.setTag(holder);
            }
            return holder;
        }
    }

    public void query() {//ArrayList<Person>
        progressBar.setVisibility(View.VISIBLE);
        BmobQuery<Person> query = new BmobQuery<Person>();
        //  ArrayList<Person> personlist=new ArrayList<Person>();
        // query.addWhereEqualTo("playerName", "比目"); //查询playerName叫“比目”的数据
        //返回1000条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(10000);
        query.addWhereEqualTo("IsOK", false);
        //执行查询方法
        //query.addWhereLessThan("Time", time);
        query.findObjects(new FindListener<Person>() {
            public void done(List<Person> object, BmobException e) {
                if (e == null) {
                    Log.d("查询成功：共" + object.size(), "条数据。");
                    persons = object;
                    Log.d("persons：共" + persons.size(), "条数据。");
                    //将sheAdapter放在这里就能能保证子线程已经执行完毕
                    myListAdapter = new MyListAdapter(persons);
                    progressBar.setVisibility(View.GONE);
                    listView.setAdapter(myListAdapter);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
        //  return personlist;
//        Message m = new Message();
//        m.what = 0;
//        handler.sendMessage(m);

    }

    public void delete(String id) {
        Person person = new Person();
        person.setObjectId(id);
        person.delete(new UpdateListener() {
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "成功");
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    public void set_OK(String id) {
        Person person = new Person();
        person.setIsOK(true);
        person.setTime(System.currentTimeMillis());
        person.update(id, new UpdateListener() {
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "修改成功");
                    Toast.makeText(FirstActivity.this, "处理修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("bmob", "修改失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(FirstActivity.this, "处理修改失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initdata() {
        persons = new ArrayList<Person>();
        persons.add(new Person("张三", "1353362984", "转账", 123456, "0"));//Person(String name, String phonenumber, String yewu, long time)
        persons.add(new Person("李四", "1353362984", "转账", 123457, "0"));
        persons.add(new Person("赵武", "1353362984", "转账", 123458, "0"));
        persons.add(new Person("陈六", "1353362984", "转账", 123459, "0"));
        persons.add(new Person("王二", "1353362984", "转账", 1234510, "0"));
        persons.add(new Person("吴晓明", "1353362984", "转账", 1234511, "0"));
        persons.add(new Person("林小红", "1353362984", "转账", 1234512, "0"));
        persons.add(new Person("郭林", "1353362984", "转账", 1234513, "0"));
        persons.add(new Person("郑子琪", "1353362984", "转账", 1234514, "0"));
        persons.add(new Person("黄家驹", "1353362984", "转账", 1234515, "0"));
        persons.add(new Person("周杰伦", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("余文乐", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("吴奇隆", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("苏有朋", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("张家辉", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("古天乐", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("彭于晏", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("张三", "1353362984", "转账", 123456, "0"));//Person(String name, String phonenumber, String yewu, long time)
        persons.add(new Person("李四", "1353362984", "转账", 123457, "0"));
        persons.add(new Person("赵武", "1353362984", "转账", 123458, "0"));
        persons.add(new Person("陈六", "1353362984", "转账", 123459, "0"));
        persons.add(new Person("王二", "1353362984", "转账", 1234510, "0"));
        persons.add(new Person("吴晓明", "1353362984", "转账", 1234511, "0"));
        persons.add(new Person("林小红", "1353362984", "转账", 1234512, "0"));
        persons.add(new Person("郭林", "1353362984", "转账", 1234513, "0"));
        persons.add(new Person("郑子琪", "1353362984", "转账", 1234514, "0"));
        persons.add(new Person("黄家驹", "1353362984", "转账", 1234515, "0"));
        persons.add(new Person("周杰伦", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("余文乐", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("吴奇隆", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("苏有朋", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("张家辉", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("古天乐", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("彭于晏", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("张三", "1353362984", "转账", 123456, "0"));//Person(String name, String phonenumber, String yewu, long time)
        persons.add(new Person("李四", "1353362984", "转账", 123457, "0"));
        persons.add(new Person("赵武", "1353362984", "转账", 123458, "0"));
        persons.add(new Person("陈六", "1353362984", "转账", 123459, "0"));
        persons.add(new Person("王二", "1353362984", "转账", 1234510, "0"));
        persons.add(new Person("吴晓明", "1353362984", "转账", 1234511, "0"));
        persons.add(new Person("林小红", "1353362984", "转账", 1234512, "0"));
        persons.add(new Person("郭林", "1353362984", "转账", 1234513, "0"));
        persons.add(new Person("郑子琪", "1353362984", "转账", 1234514, "0"));
        persons.add(new Person("黄家驹", "1353362984", "转账", 1234515, "0"));
        persons.add(new Person("周杰伦", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("余文乐", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("吴奇隆", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("苏有朋", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("张家辉", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("古天乐", "1353362984", "转账", 1234516, "0"));
        persons.add(new Person("彭于晏", "1353362984", "转账", 1234516, "0"));

    }

    int back_number = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back_number++;
            // 返回键操作失败
            if (back_number == 2) {
                // Toast.makeText(MainActivity.this, "是否退出系统", 1).show();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            Toast.makeText(FirstActivity.this, "再次点击退出！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private android.os.Handler handler4 = new android.os.Handler() {
        public void handleMessage(Message msg) {
            back_number = 0;
        }
    };

    private class RefershTask implements Runnable {
        public void run() {
            handler4.obtainMessage().sendToTarget();  //通过发信息通知更新
        }
    }

//    private android.os.Handler handler = new android.os.Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    progressDialog.dismiss();
//                    break;
//                default:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };

    public void donghua(final View view) {
        int dx=ll_guanggao.getMeasuredWidth();


        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        MyLinearLayoutManager.width=width;
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        Log.d("屏幕宽度", "width:" + width);
        int x_begin=-tv_guanggao.getLeft();


        final TranslateAnimation ta = new TranslateAnimation(x_begin,width , 0, 0);
        ta.setDuration(8000);
        ta.setFillAfter(true);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(ta);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(ta);
    }


}
