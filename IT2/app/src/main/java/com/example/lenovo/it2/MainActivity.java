package com.example.lenovo.it2;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.lenovo.it2.sl.Person;
import com.example.lenovo.it2.sl.SwipeLayoutManager;
import java.util.ArrayList;
import java.util.List;

import MyService.InternetService;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends Activity {
    private ListView listview;
    private List<Person> persons;
    private MyListAdapter myListAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "4a4ca1fb561ab94dcb1cf7dae60ef009");
        init();
      //  persons=new ArrayList<Person>();
       // persons=query();
        listview = (ListView) findViewById(R.id.listView);
        myListAdapter=new MyListAdapter();
        listview.setAdapter( myListAdapter);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    //如果垂直滑动，则需要关闭已经打开的layout
                      SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
            }
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("name", persons.get(position).getName());
                intent.putExtra("phonenumber", persons.get(position).getPhonenumber());
                intent.putExtra("yewu", persons.get(position).getYewu());
                intent.putExtra("position",position);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                 //重新刷新界面
                    delete(data.getStringExtra("id"));
                   // persons.remove(data.getIntExtra("position",0));
                    myListAdapter.notifyDataSetChanged();
                    SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
                break;
            default:break;
        }
    }

    public void init() {
        persons = new ArrayList<Person>();
        network();

//        persons.add(new Person("张三", "1353362984", "转账", 123456));//Person(String name, String phonenumber, String yewu, long time)
//        persons.add(new Person("李四", "1353362984", "转账", 123457));
//        persons.add(new Person("赵武", "1353362984", "转账", 123458));
//        persons.add(new Person("陈六", "1353362984", "转账", 123459));
//        persons.add(new Person("王二", "1353362984", "转账", 1234510));
//        persons.add(new Person("吴晓明", "1353362984", "转账", 1234511));
//        persons.add(new Person("林小红", "1353362984", "转账", 1234512));
//        persons.add(new Person("郭林", "1353362984", "转账", 1234513));
//        persons.add(new Person("郑子琪", "1353362984", "转账", 1234514));
//        persons.add(new Person("黄家驹", "1353362984", "转账", 1234515));
//        persons.add(new Person("周杰伦", "1353362984", "转账", 1234516));
//        persons.add(new Person("余文乐", "1353362984", "转账", 1234516));
//        persons.add(new Person("吴奇隆", "1353362984", "转账", 1234516));
//        persons.add(new Person("苏有朋", "1353362984", "转账", 1234516));
//        persons.add(new Person("张家辉", "1353362984", "转账", 1234516));
//        persons.add(new Person("古天乐", "1353362984", "转账", 1234516));
//        persons.add(new Person("彭于晏", "1353362984", "转账", 1234516));

    }

    //判断网络
    Intent networkIntent;
    public void network() {
        networkIntent = new Intent(MainActivity.this, InternetService.class);
        startService(networkIntent);
    }

    class MyListAdapter extends BaseAdapter {
        public int getCount() {
            return persons.size();
        }

        public Object getItem(int position) {
            return persons.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, null);
            }
            holder = ViewHolder.getViewHolder(convertView);

            holder.name.setText(persons.get(position).getName());
            holder.tongzhi.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("name", persons.get(position).getName());
                    intent.putExtra("phonenumber", persons.get(position).getPhonenumber());
                    intent.putExtra("yewu", persons.get(position).getYewu());
                    intent.putExtra("id",persons.get(position).getObjectId());
                    startActivityForResult(intent,1);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    persons.remove(position);
                    delete(persons.get(position).getObjectId());
                    notifyDataSetChanged();
                    SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
            });
            if(position==0){
               // holder.delete.setVisibility(View.VISIBLE);
                holder.tongzhi.setVisibility(View.VISIBLE);
            }else{
              // holder.delete.setVisibility(View.GONE);
               holder.tongzhi.setVisibility(View.GONE);
            }
            holder.delete.setVisibility(View.GONE);
           AnimatorSet animatorSet = new AnimatorSet();//组合动画
           ObjectAnimator scaleX = ObjectAnimator.ofFloat(convertView, "scaleX", 0f, 1f);
           ObjectAnimator scaleY = ObjectAnimator.ofFloat(convertView, "scaleY", 0f, 1f);
           // ObjectAnimator alpha = ObjectAnimator.ofFloat(convertView, "alpha", 0f, 1f);
            //ObjectAnimator rotation = ObjectAnimator.ofFloat(convertView,"rotation",0,180,0);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(convertView,"rotationX",0,180,0);
            // ObjectAnimator rotation = ObjectAnimator.ofFloat(convertView,"rotationY",0,180,0);
            animatorSet.setDuration(400);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.playTogether(scaleX,scaleY,rotation);//多个动画同时开始
            animatorSet.start();
            return convertView;
        }
    }

    static class ViewHolder {
        TextView name;
        TextView tongzhi;
        TextView delete;

        public ViewHolder(View converView) {
            name = (TextView) converView.findViewById(R.id.tv_name);
            tongzhi = (TextView) converView.findViewById(R.id.tv_tongzhi);
            delete = (TextView) converView.findViewById(R.id.tv_delete);
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
    private ArrayList<Person> personlist;
    public ArrayList<Person> query() {

        BmobQuery<Person> query = new BmobQuery<Person>();
        personlist=new ArrayList<Person>();
        // query.addWhereEqualTo("playerName", "比目"); //查询playerName叫“比目”的数据
        //返回1000条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(100);
        query.addWhereLessThan("Time",System.currentTimeMillis());
        //执行查询方法
        //query.addWhereLessThan("Time", time);
        query.findObjects(new FindListener<Person>() {
            public void done(List<Person> object, BmobException e) {
                if (e == null) {
                   Log.d("查询成功：共" + object.size(), "条数据。");
                   // number = object.size();
                   for (int i=0;i<object.size();i++){
                       personlist.add(object.get(i));
                    //   personlist.get(i).setId(object.get(i).getObjectId());
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
        return personlist;

    }

    public void delete(String id){
        Person person = new Person();
        person.setObjectId(id);
        person.delete(new UpdateListener() {
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","成功");
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        stopService(networkIntent);
        super.onDestroy();
    }
}
