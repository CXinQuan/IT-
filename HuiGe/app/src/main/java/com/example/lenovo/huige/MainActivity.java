package com.example.lenovo.huige;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bean.Person;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import myserce.InternetService;

public class MainActivity extends Activity {
    private Vibrator mVibrator;//震动对象
    private SensorManager sm;   //摇一摇

    private int imageIds[];
    private ArrayList<View> dots;
    private ViewPager mViewPager;
    private ArrayList<ImageView> images;
    private ViewPagerAdapter adapter;
    private int oldPosition = 0;
    private int currentItem = 0;
    private Button btn_yuyue;
    private TextView tv_number;
    private TextView tv_guanggao;
    private ProgressBar progressBar;

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledExecutorService scheduledExecutorService_Query;
    //private IWeather iweather = new Weather();
    private LocationManager locationManager;
    private String provider;
    private EditText name;  //宿舍号
    private EditText name2; //确认宿舍号
    private EditText phonenumber; //手机号码
    private EditText et_yewu; //桶数
    private Spinner spinner;
    private String item_name;
    private String item_phone;
    private String return_objectId;
    private Button btn_queding;
    private LinearLayout ll;
    private Button guagual_queding;
    //private boolean isyuyue = true;
    int number = 0;
    private long time = System.currentTimeMillis();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(MainActivity.this, "4a4ca1fb561ab94dcb1cf7dae60ef009");
        init();
        network();
        // query();
        tv_number.setText("B Team 欢迎您！");
        tv_number.setTextSize(20);
    }

    public void init() {
        //获取手机震动服务
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        //摇一摇
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorjiasudu = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(listener, sensorjiasudu, SensorManager.SENSOR_DELAY_NORMAL);

        imageIds = new int[]{R.drawable.photo12, R.drawable.photo13, R.drawable.photo14};
        images = new ArrayList<ImageView>();
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        btn_yuyue = (Button) findViewById(R.id.btn_yuyue);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_guanggao = (TextView) findViewById(R.id.tv_guanggao);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        adapter = new ViewPagerAdapter();
        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                oldPosition = position;
                currentItem = position;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
        name = (EditText) findViewById(R.id.name);
        name2 = (EditText) findViewById(R.id.name2);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        et_yewu = (EditText) findViewById(R.id.et_yewu);
        // spinner = (Spinner) findViewById(R.id.spinner);
        btn_queding = (Button) findViewById(R.id.btn_queding);
        btn_queding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!name.getText().toString().equals("")
                        && !phonenumber.getText().toString().equals("")
                        && !et_yewu.getText().toString().equals("")) {
                    if (name.getText().toString().equals(name2.getText().toString())) {
                        Person p2 = new Person();
                        p2.setName(name.getText().toString());
                        p2.setPhonenumber(phonenumber.getText().toString());
                        p2.setYewu(et_yewu.getText().toString());
                        p2.setIsOK(false);
                        time = System.currentTimeMillis();
                        p2.setTime(time);
                        // sharePerference 存储 信息.
                        SharedPreferences.Editor editor = getSharedPreferences("BTEAM", MODE_PRIVATE).edit();
                        editor.putString("name", name.getText().toString());
                        editor.putString("name2", name2.getText().toString());
                        editor.putString("phonenumber", phonenumber.getText().toString());
                        editor.putString("et_yewu", et_yewu.getText().toString());
                        editor.commit();
//                        name.setText("");
//                        phonenumber.setText("");
//                        et_yewu.setText("");
                        // 改为动画出去
                        donghua_Out(ll);
                        btn_yuyue.setText("一键预约");
                        isCancle = !isCancle;
                        //ll.setVisibility(View.GONE);
                        p2.save(new SaveListener<String>() {
                            public void done(String objectId, BmobException e) {
                                if (e == null) {
                                    //需要查询当前已经有多少用户了，前面还有多少人,是根据条件查询，而不是查询总数
                                    query();
                                    Toast.makeText(MainActivity.this, "预约成功，订单号为：" + objectId, Toast.LENGTH_LONG).show();
                                    return_objectId = objectId;//
                                    // btn_yuyue.setText("取消预约");
                                } else {
                                    Toast.makeText(MainActivity.this, "创建数据失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "两次输入宿舍号不同", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "必填项不能为空！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //判断网络
    Intent networkIntent;

    public void network() {
        networkIntent = new Intent(MainActivity.this, InternetService.class);
        startService(networkIntent);
    }

    private class ViewPagerAdapter extends PagerAdapter {
        public int getCount() {
            return images.size();
        }

        //是否是同一张图片
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(images.get(position));
        }

        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(images.get(position));
            return images.get(position);
        }
    }

    protected void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService_Query = Executors.newSingleThreadScheduledExecutor();
        //每隔2秒钟切换一张图片
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 5, 5, TimeUnit.SECONDS); //3,4 执行完ViewPagerTask任务之后再过3秒再执行一次，每隔4秒执行一次，这两个数最好设置为相同数字，不然后面的周期就会紊乱
        scheduledExecutorService_Query.scheduleWithFixedDelay(new RefershTask(), 8, 8, TimeUnit.SECONDS);
        //initialDelay(首个数字)：初始化延时
        // period：两次开始执行最小间隔时间 每隔4s执行一次
        //  handler2.sendEmptyMessageDelayed(0, 3000);
        //  handler4.sendEmptyMessageDelayed(0, 4000);
    }

    //切换图片
    private class ViewPagerTask implements Runnable {
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            handler2.obtainMessage().sendToTarget();  //通过发信息通知更新
        }
    }

    private class RefershTask implements Runnable {
        public void run() {
            handler4.obtainMessage().sendToTarget();  //通过发信息通知更新
        }
    }

    private boolean isOne = true;
    private Handler handler4 = new Handler() {
        public void handleMessage(Message msg) {
            // sersor_number = 0;
            // back_number = 0;
            float translationY = tv_guanggao.getHeight();
            ObjectAnimator anim = ObjectAnimator.ofFloat(tv_guanggao, "translationY", translationY, 0);  //translationY 正的向上，负的向下, 0 代表需要反复
            if (isOne) {
                tv_guanggao.setText("摇一摇更新预约人数！");
                isOne = !isOne;
            } else {
                tv_guanggao.setText("快叫上小伙伴一起来叫水吧！");
                isOne = !isOne;
            }
            anim.setDuration(1000);//从原始位置到指定位置的时间是1秒，显示 n秒之后，又回到原始位置，再1秒出现
            anim.setInterpolator(new DecelerateInterpolator());
            anim.start();
            // handler4.sendEmptyMessageDelayed(0, 7000);
        }
    };

    private Handler handler2 = new Handler(){
        public void handleMessage(Message msg) {
            //设置当前页面
            mViewPager.setCurrentItem(currentItem);
            // handler2.sendEmptyMessageDelayed(0, 3000);

            sersor_number = 0;
            back_number = 0;
//            float translationY = tv_guanggao.getHeight();
//            ObjectAnimator anim = ObjectAnimator.ofFloat(tv_guanggao, "translationY", translationY, 0);  //translationY 正的向上，负的向下, 0 代表需要反复
//            if (isOne) {
//                tv_guanggao.setText("快叫上小伙伴一起来叫水吧！");
//                isOne = !isOne;
//            } else {
//                tv_guanggao.setText("摇一摇更新预约人数！");
//                isOne = !isOne;
//            }
//            anim.setDuration(1000);//从原始位置到指定位置的时间是1秒，显示 n秒之后，又回到原始位置，再1秒出现
//            anim.setInterpolator(new DecelerateInterpolator());
//            anim.start();
        }
    };

    private Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {
            //设置当前页面
            if (number == 0) {
                tv_number.setText("已经轮到你了！");
                //设置震动周期，数组表示时间：等待+执行，单位是毫秒，下面操作代表:等待100，执行100，等待100，执行1000，
                //后面的数字如果为-1代表不重复，之执行一次，其他代表会重复，0代表从数组的第0个位置开始
                mVibrator.vibrate(500);// 震动
                mVibrator.vibrate(new long[]{100, 100, 100, 1000}, -1);
            } else {
                tv_number.setText(number + "");
            }
        }
    };

    private class QueryTask implements Runnable {
        public void run() {
            //   使用摇一摇更新来替代此耗时操作
            query();
            handler3.obtainMessage().sendToTarget();  //通过发信息通知更新
        }
    }

    public void shuaxin(View v) {
        query();
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        // ObjectAnimator rotation = ObjectAnimator.ofFloat(v, "rotation", 0,360);
        //ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 0.0f, 1.0f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(v, "rotation", 0, 360, 0);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(rotation);//多个动画同时开始
        animatorSet.start();


    }

    private boolean isCancle = false;

    public void yuyue(View v) {
        if (isCancle) {
            donghua_Out(ll);
            btn_yuyue.setText("一键预约");
            isCancle = !isCancle;
        } else {
            //  改为动画
            ll = (LinearLayout) findViewById(R.id.alertdialog);
            ll.setVisibility(View.VISIBLE);
            btn_queding.setVisibility(View.VISIBLE);
            donghua_In(ll);

            SharedPreferences sp = getSharedPreferences("BTEAM", MODE_PRIVATE);

            name.setText(sp.getString("name", ""));
            name2.setText(sp.getString("name2", ""));
            phonenumber.setText(sp.getString("phonenumber", ""));
            et_yewu.setText(sp.getString("et_yewu", ""));
            isCancle = !isCancle;
            btn_yuyue.setText("取消");
        }
//        if (isyuyue) {  //一键预约
//            ll = (LinearLayout) findViewById(R.id.alertdialog);
//            ll.setVisibility(View.VISIBLE);
//            isyuyue = !isyuyue;
//
//        } else {  //取消预约
//
//            //加一个判断，当超过一定的时间就不能取消订单
//            btn_yuyue.setText("一键预约");
//            Person p = new Person();
//            p.setObjectId(return_objectId);
//            p.delete(new UpdateListener() {
//                public void done(BmobException e) {
//                    if (e == null) {
//                        Log.i("bmob", "成功");
//                        Toast.makeText(MainActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                        Toast.makeText(MainActivity.this, "系统繁忙，请稍后重试！", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//            isyuyue = !isyuyue;
//        }
    }

    public void  query() {
        tv_number.setText("");
        progressBar.setVisibility(View.VISIBLE);
        BmobQuery<Person> query = new BmobQuery<Person>();
        //查询playerName叫“比目”的数据
        // query.addWhereEqualTo("playerName", "比目");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(10000);
        //执行查询方法
        query.addWhereEqualTo("IsOK", false);
        query.addWhereLessThanOrEqualTo("Time", time);
        query.findObjects(new FindListener<Person>() {
            public void done(List<Person> object, BmobException e) {
                if (e == null) {
                    Log.d("查询成功：共" + object.size(), "条数据。");
                    number = object.size();
                    if (number == 0) {
                        progressBar.setVisibility(View.GONE);
                        tv_number.setTextSize(20);
                        tv_number.setText("即将送达！");
                    } else {
                        progressBar.setVisibility(View.GONE);
                        tv_number.setTextSize(50);
                        tv_number.setText(number + "");
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void fenxiang(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("好东西要分享")
                //  .setMessage("选择")
                .setPositiveButton("微信群", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        //参数   前一个参数是应用程序的包名,后一个是这个应用程序的Activity名
                        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                        intent.setComponent(comp);
                        intent.setAction("android.intent.action.SEND");
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_TEXT, "以后叫水一键就可以搞定了！@所有人");
                        //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tupian()));
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("朋友圈", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("com.tencent.mm",
                                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                        intent.setComponent(comp);
                        intent.setAction("android.intent.action.SEND");
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_TEXT, "以后叫水一键就可以搞定了！@所有人");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tupian()));
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "您还没有安装微信！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    public File tupian() {
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "huige_share" + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.share_);
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    protected void onPause() {
        //取消震动
        mVibrator.cancel();
        super.onPause();
    }

    int sersor_number = 0;
    private SensorEventListener listener = new SensorEventListener() {
        float value;
        float xvalue;
        float yvalue;
        float zvalue;

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                xvalue = Math.abs(event.values[0]);
                yvalue = Math.abs(event.values[1]);
                zvalue = Math.abs(event.values[2]);
            }
            if ((xvalue > 15 || yvalue > 15 || zvalue > 15)) {
                if (sersor_number <= 2) {
                    query();
                    sersor_number++;
                    mVibrator.vibrate(500);// 震动
                    // mVibrator.vibrate(new long[]{100, 100, 100, 1000}, -1);
                    //设置震动周期，数组表示时间：等待+执行，单位是毫秒，下面操作代表:等待100，执行100，等待100，执行1000，
                    //后面的数字如果为-1代表不重复，之执行一次，其他代表会重复，0代表从数组的第0个位置开始
                    // mVibrator.vibrate(new long[]{100,100,100,1000},-1);
                    Toast.makeText(MainActivity.this, "更新完毕！", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "系统繁忙，请不要频繁操作！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    protected void onDestroy() {
        if (sm != null) {
            sm.unregisterListener(listener);
        }
        stopService(networkIntent);
        super.onDestroy();
    }

    public void donghua_In(View view) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 1.0f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0, 360, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(btn_queding, "alpha", 0f, 1f);
        view.setClickable(true);//必须设置看不见，不然该位置仍然有点击事件
        btn_queding.setClickable(true);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY, rotation, alpha);//多个动画同时开始
        animatorSet.start();

//        AnimatorSet btn_animator = new AnimatorSet();//组合动画

//        btn_animator.setDuration(1000);
//        btn_animator.setInterpolator(new DecelerateInterpolator());
//        btn_animator.playTogether(alpha);

        //  btn_animator.start();
    }

    public void donghua_Out(View view) {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.0f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0, 360, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(btn_queding, "alpha", 1f, 0f);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY, rotation, alpha);//多个动画同时开始
        animatorSet.start();
        view.setClickable(false);//必须设置看不见，不然该位置仍然有点击事件
        btn_queding.setClickable(false);
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
            Toast.makeText(MainActivity.this, "你再按一下试试！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //<editor-fold desc="Description">
    //    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            if (event.getAction() == KeyEvent.ACTION_UP && event.getRepeatCount() == 0) {//按键的抬起事件
//                Toast.makeText(MainActivity.this, "有种再按一下试试！", Toast.LENGTH_LONG).show();
//            }
//            back_number++;
//        }
//        return super.dispatchKeyEvent(event);
//    }
    //</editor-fold>

    //<editor-fold desc="Description">
    /** 天气失败
     protected void onDestroy() {
     super.onDestroy();
     if (locationManager != null) {
     locationManager.removeUpdates(locationListener);
     }
     }
     public void qiehuan(View v) {

     }

     LocationListener locationListener = new LocationListener() {
     public void onLocationChanged(Location location) {
     iweather.alimap_show(location, handler);
     }

     public void onProviderDisabled(String arg0) {
     }

     public void onProviderEnabled(String arg0) {
     }

     public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
     }
     };

     private void weather_fail() {
     initWeather();
     iweather.weather_XiangQing("海珠", handler);

     locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     List<String> providerlist = locationManager.getProviders(true);
     if (providerlist.contains(LocationManager.GPS_PROVIDER)) {
     provider = LocationManager.GPS_PROVIDER;
     } else if (providerlist.contains(LocationManager.NETWORK_PROVIDER)) {
     provider = LocationManager.NETWORK_PROVIDER;
     } else {
     Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_LONG).show();
     return;
     }
     if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
     return;
     }
     Location location = locationManager.getLastKnownLocation(provider);
     Log.d(location.getLatitude() + "经度是：" + location.getLongitude(), "经纬度");
     if (location != null) {
     iweather.alimap_show(location, handler);
     Log.d("location ", location + "weizhi ");
     iweather.weather_XiangQing(cityName.getText().toString(), handler);
     }
     locationManager.requestLocationUpdates(provider, 2000, 1, locationListener);
     }

     private void initWeather() {
     cityName = (TextView) findViewById(R.id.tv_city);
     publicText = (TextView) findViewById(R.id.public_text);
     weatherDespText = (TextView) findViewById(R.id.weather_desp);
     temp1Text = (TextView) findViewById(R.id.temp1);
     temp2Text = (TextView) findViewById(R.id.temp2);
     }

     private Handler handler = new Handler() {
     public void handleMessage(Message msg) {
     switch (msg.what) {
     case 1:
     cityName.setText(msg.obj + "");
     break;
     case 2:
     Log.d("风力", msg.obj + "风力");
     break;
     case 3://风力
     Log.d("风力", msg.obj + "风力");
     break;
     case 4://城市
     Log.d("城市", msg.obj + "城市");
     break;
     case 5://温度
     Log.d("温度", msg.obj + "温度");
     break;
     case 6://质量
     Log.d("质量", msg.obj + "质量");
     break;
     case 7://PM2.5
     Log.d("PM2.5", msg.obj + "PM2.5");
     break;
     case 8://系数
     Log.d("系数", msg.obj + "系数");
     break;
     case 9:
     Log.d("风力", msg.obj + "风力");
     break;
     }
     }
     };
     */
    //</editor-fold>天气失败
}
