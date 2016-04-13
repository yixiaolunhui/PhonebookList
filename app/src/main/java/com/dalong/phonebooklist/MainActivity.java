package com.dalong.phonebooklist;

import android.Manifest;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.dalong.phonebooklist.adapter.PhoneBookAdapter;
import com.dalong.phonebooklist.entity.Contact;
import com.dalong.phonebooklist.utils.PhoneBookUtls;
import com.dalong.phonebooklist.view.MyLetterListView;
import com.dalong.phonebooklist.view.PinnedSectionListView;

import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private PinnedSectionListView mPinnedSectionListView;

    private MyLetterListView mMyLetterListView;

    private static final int RC_PHONE_BOOK= 100;

    private TextView overlay;
    private WindowManager windowManager;
    private HashMap<String, Integer> alphaIndexer;
    private Handler handler;
    private OverlayThread overlayThread;
    private List<Contact> allPhoneBookList;
    private String[] sections;
    private PhoneBookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initWeight();
    }



    private void initView() {
        mPinnedSectionListView=(PinnedSectionListView)findViewById(R.id.dalong_phone_list);
        mMyLetterListView=(MyLetterListView)findViewById(R.id.dalong_letter_view);
        adapter=new PhoneBookAdapter(this,allPhoneBookList);
        mPinnedSectionListView.setAdapter(adapter);
    }

    @AfterPermissionGranted(RC_PHONE_BOOK)
    private void initData() {
        Log.v("888888","initData------0----");
        String[] perms = {Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(MainActivity.this,perms)) {
            Log.v("888888","initData------1----");
            allPhoneBookList= PhoneBookUtls.getAllCallRecords(MainActivity.this);
            alphaIndexer = new HashMap<String, Integer>();
            handler = new Handler();
            overlayThread = new OverlayThread();
            sections = new String[allPhoneBookList.size()];
            for (int i = 0; i < allPhoneBookList.size(); i++) {
                String currentStr = allPhoneBookList.get(i).getNameSort();
                String previewStr = (i - 1) >= 0 ? allPhoneBookList.get(i - 1)
                        .getNameSort() : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = allPhoneBookList.get(i).getNameSort();
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
            adapter.changeData(allPhoneBookList);
        }else{
            EasyPermissions.requestPermissions(this, "获取通讯录", RC_PHONE_BOOK, perms);
        }

    }
    private void initWeight() {
        mMyLetterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        initOverlay();
    }
    /**
     * 点击字母的响应事件
     */
    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                mPinnedSectionListView.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                handler.postDelayed(overlayThread, 1500);
            }
        }

    }
    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }
    /**
     * 点击右侧按钮弹框
     */
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.view_phone_book_select_overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("888888", "onPermissionsGranted:" + requestCode + ":" + perms.size());

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("888888", "onPermissionsDenied:" + requestCode + ":" + perms.size());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
