package xiaoliang.ltool.activity.lock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.IOException;

import xiaoliang.ltool.R;
import xiaoliang.ltool.activity.weather.WeatherActivity;
import xiaoliang.ltool.activity.meizhi.MeizhiActivity;
import xiaoliang.ltool.activity.qr.QRCreateActivity;
import xiaoliang.ltool.activity.qr.QRReadActivity;
import xiaoliang.ltool.listener.AdminReceiver;
import xiaoliang.ltool.util.DialogUtil;
import xiaoliang.ltool.util.OtherUtil;
import xiaoliang.ltool.util.ShortcutUtil;
import xiaoliang.ltool.util.ToastUtil;

public class CreateLockActivity extends AppCompatActivity implements Switch.OnCheckedChangeListener,View.OnClickListener,AdapterView.OnItemSelectedListener {

    private ImageView showImg,resImg;
    private TextInputEditText nameEdit,numEdit;
    private View imgBtn;
    private SwitchCompat circularSwitch,redPointSwitch,repeatSwitch;
    private View numLayout;
    private static final int GET_IMG = 500;
    private static final int GET_CLIPPING = 501;
    private Bitmap image;
    private int pointNum = 0;
    private ShortcutUtil shortcutUtil;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private static final int MY_REQUEST_CODE = 9999;
    private AppCompatSpinner openModel;
    private String[] modelNames = {"锁屏","二维码扫描","二维码生成","妹子图","天气"};
    private Class[] modelClass = {LockActivity.class,QRReadActivity.class,QRCreateActivity.class,MeizhiActivity.class,WeatherActivity.class};
    private Class thisClass = modelClass[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_create_lock_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showImg = (ImageView) findViewById(R.id.activity_create_lock_show);
        resImg = (ImageView) findViewById(R.id.activity_create_lock_img);
        nameEdit = (TextInputEditText) findViewById(R.id.activity_create_lock_name);
        numEdit = (TextInputEditText) findViewById(R.id.activity_create_lock_pointnum);
        imgBtn = findViewById(R.id.activity_create_lock_imgbtn);
        circularSwitch = (SwitchCompat) findViewById(R.id.activity_create_lock_circular);
        redPointSwitch = (SwitchCompat) findViewById(R.id.activity_create_lock_redpoint);
        repeatSwitch = (SwitchCompat) findViewById(R.id.activity_create_lock_repeat);
        numLayout = findViewById(R.id.activity_create_lock_pointnum_layout);
        openModel = (AppCompatSpinner) findViewById(R.id.activity_create_lock_model);
        openModel.setOnItemSelectedListener(this);
        imgBtn.setOnClickListener(this);
        circularSwitch.setOnCheckedChangeListener(this);
        redPointSwitch.setOnCheckedChangeListener(this);
        repeatSwitch.setOnCheckedChangeListener(this);
        shortcutUtil = new ShortcutUtil(this);
        //获取设备管理服务
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        image = OtherUtil.drawable2Bitmap( getResources().getDrawable(R.drawable.ic_oneplus));
        numLayout.setVisibility(View.GONE);
        resImg.setImageResource(R.drawable.ic_oneplus);
        showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
        openModel.setAdapter(new ArrayAdapter(this,R.layout.item_shortcut_model,R.id.item_shortcut_model_text,modelNames));
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.activity_create_lock_circular:
                shortcutUtil.setCircular(isChecked);
                break;
            case R.id.activity_create_lock_redpoint:
                shortcutUtil.setRedPoint(isChecked);
                if(isChecked){
                    numLayout.setVisibility(View.VISIBLE);
                }else{
                    numLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.activity_create_lock_repeat:
                shortcutUtil.setRepeat(isChecked);
                break;
        }
        showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_create_lock_pointnum_enter:
                if(numEdit.getText().toString().length()>0)
                    pointNum = Integer.parseInt(numEdit.getText().toString());
                else
                    pointNum = 0;
                showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
                break;
            case R.id.activity_create_lock_fab:
                sendLock();
                break;
            case R.id.activity_create_lock_imgbtn:
                DialogUtil.getAlertDialog(this, "选择图片", "选择您的锁屏按钮图片", "H2OS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resImg.setImageResource(R.drawable.ic_oneplus);
                        image = OtherUtil.drawable2Bitmap( getResources().getDrawable(R.drawable.ic_oneplus));
                        showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
                    }
                }, "锤子", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resImg.setImageResource(R.drawable.ic_smartisan);
                        image = OtherUtil.drawable2Bitmap( getResources().getDrawable(R.drawable.ic_smartisan));
                        showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
                    }
                }, "选择图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                        startActivityForResult(intent, GET_IMG);
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_IMG:
                saveImage(data);
                break;
            case GET_CLIPPING:
                try{
                    if(data!=null){
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());//显得到bitmap图片
                        if(bmp!=null){
                            image = bmp;
                            resImg.setImageBitmap(image);
                            showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
                        }
                    }
                }catch (IOException e){
                    Log.d("CreateLock-->Result",e.getMessage());
                }
                break;
            case MY_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    shortcutUtil.addShortcut(nameEdit.getText().toString(),pointNum,image,LockActivity.class);
                }else{
                    DialogUtil.getAlertDialog(CreateLockActivity.this,"未获取设备管理器权限，无法进行锁屏操作。\n请进行授权，本应用承诺此权限不用于锁屏意外的任何用途。");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveImage(Intent data){
        try{
            if (data != null){
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());//显得到bitmap图片
                if(bmp==null)
                    return;
                if(bmp.getWidth()==bmp.getHeight()) {
                    image = bmp;
                    resImg.setImageBitmap(image);
                    showImg.setImageBitmap(shortcutUtil.getShortcutBmp(pointNum,image));
                }else{
                    startPhotoZoom(data.getData(),GET_CLIPPING);
                }
            }
        }catch (Exception e){
            Log.d("CreateLock-->saveImage",e.getMessage());
        }
    }


    private void startPhotoZoom(Uri uri, int type) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 144);
        intent.putExtra("outputY", 144);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, type);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendLock() {
        /*
        * 假如先判断是否有权限，如果没有则调用activeManage()，然后立即锁屏，再finish()。
         * 这样做是有问题的，因为activeManage()可能还在等待另一个Activity的结果，那么此时依然没有权限却
         * 执行了lockNow()，这样就出错了。
         * 处理方法有2个：
         * 1、是重写OnActivityResult()函数，在里面判断是否获取权限成功，是则锁屏并finish()
         * 否则继续调用activeManage()获取权限（这样激活后立即锁屏，效果很好）
         * 2、不重写OnActivityResult()函数，第一次获取权限后不锁屏而立即finish()，这样从理论上说也可能
         * 失败，可能权限还没获取好就finish了（这样激活后就回到桌面，还得再按一次锁屏才能锁）
        * 综上推荐第一种方法。*/

        //判断是否有锁屏权限，若有则立即锁屏并结束自己，若没有则获取权限
        if (!policyManager.isAdminActive(componentName)) {
            activeManage();
        }else{
            shortcutUtil.addShortcut(nameEdit.getText().toString(),pointNum,image,thisClass);
            ToastUtil.T(this,"已添加");
        }
    }
    //获取权限
    private void activeManage() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "本权限用于调用系统锁屏功能，本应用承诺，不用于其他任何非法途径");
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        thisClass = modelClass[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        openModel.setSelection(0);
    }
}
