package edu.buu.daowe.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.signature.StringSignature;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.activity.LoginActivity;
import edu.buu.daowe.defui.ItemView;
import edu.buu.daowe.dialogue.ModifyPhotoBottomDialog;
import edu.buu.daowe.http.BaseRequest;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class UserCenter_Fragment extends Fragment {
    private Bitmap bitmap;
    private ImageView mHBack;
    private ImageView mHHead;
    private ImageView mUserLine;
    private ItemView studentName;
    private TextView mUserName;
    private TextView mUserVal;
    byte[] picdata;
    boolean isgetdata=false;
    public DaoWeApplication app;
    FragmentManager fragmanager;
    private ItemView mNickName;
    private ItemView mSex;
    private ItemView mSignName;
    private ItemView mLogout;
    private ItemView mPass;
   // private ItemView mPhone;
    private ItemView mAbout;
    private ItemView mclassinfo;
    JSONObject userinfo;
    public static String TAG = "UserCenter_Fragment";

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter && !isgetdata){
            isgetdata = true;
         //   Log.e("zxzxzxzxzxzxzxzxz","zxzxzxzxzxzxzxzxz");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                      //  Thread.sleep(1000);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置背景磨砂效果

                                try {
                                    if(userinfo!=null){
                                        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).addHeader("Content-Type","application/json;charset=utf-8")
                                                .url(BaseRequest.BASEURL + "users/" + app.getUsername()).build()
                                                .execute(new StringCallback() {
                                                             @Override
                                                             public void onError(Call call, Exception e, int id) {

                                                             }

                                                             @Override
                                                             public void onResponse(String response, int id) {
                                                                 try {
                                                                     JSONObject data = new JSONObject(response);
                                                                     data = data.getJSONObject("data");

                                                                         Glide.with(getActivity()).load(data.getString("avatar")).signature(new StringSignature(System.nanoTime() + ""))
                                                                                 .bitmapTransform(new BlurTransformation(getActivity(), 30, 3), new CenterCrop(getActivity())).diskCacheStrategy(DiskCacheStrategy.NONE)
                                                                                 .skipMemoryCache(true).into(mHBack);

                                                                         //设置圆形图像
                                                                         Glide.with(getActivity()).load(data.getString("avatar")).signature(new StringSignature(System.nanoTime()+""))
                                                                                 .bitmapTransform(new CropCircleTransformation(getActivity())).diskCacheStrategy(DiskCacheStrategy.NONE)
                                                                                 .skipMemoryCache(true).into(mHHead);


                                                                 } catch (JSONException e) {
                                                                     e.printStackTrace();
                                                                 }
                                                             }
                                                         });

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();



            //getdata

        }
        else{
            isgetdata = false;
        }
        return  super.onCreateAnimation(transit,enter,nextAnim);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usercenter_content, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();
        fragmanager = this.getFragmentManager();
        initView();
        setData();
    }

    private void setData() {

        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).url(BaseRequest.BASEURL + "users/" + app.getUsername()).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                //    Log.e("errrrr", response);
                try {
                    userinfo = new JSONObject(response);
                    if (userinfo.getInt("code") == 200) {

                        userinfo = userinfo.getJSONObject("data");
                        //app.setStuid(userinfo.getString("id"));
                        // tvsign.setText(userinfo.getString("introduction"));
                        //mUserName.setText(userinfo.getString("name"));
                        //mNickName.setRightDesc(userinfo.getString("name"));
                        String phone = app.getUsername();
                        mSignName.setRightDesc(userinfo.getString("introduction"));
                        //mUserVal.setText(phone.substring(0, 3) + "****" + phone.substring(7, phone.length()));
                        mSex.setRightDesc(userinfo.getString("sex"));
                        studentName.setRightDesc(userinfo.getString("name"));
                        //设置背景磨砂效果
                        Glide.with(getActivity()).load(userinfo.getString("avatar"))
                                .bitmapTransform(new BlurTransformation(getActivity(), 30,3), new CenterCrop(getActivity())).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(mHBack);

                        //设置圆形图像
                        Glide.with(getActivity()).load(userinfo.getString("avatar"))
                                .bitmapTransform(new CropCircleTransformation(getActivity())).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(mHHead);
                        userinfo = userinfo.getJSONObject("data");
                        mNickName.setRightDesc(userinfo.getString("college"));
                        mclassinfo.setRightDesc(userinfo.getString("grade") + "级" + userinfo.getString("major"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mAbout.setRightDesc("1.0.1");
        mAbout.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(), "已经是最新版了哦～", Toast.LENGTH_SHORT).show();
            }
        });


        //设置用户名整个item的点击事件
        mNickName.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        });
        //修改用户名item的左侧图标
        //   mNickName.setLeftIcon(R.drawable.ic_phone);
        //
        //  mNickName.setLeftTitle("修改后的用户名");
        //   mNickName.setRightDesc("名字修改");
        mNickName.setShowRightArrow(false);
        mNickName.setShowBottomLine(false);
        studentName.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(),"你好👋",Toast.LENGTH_SHORT).show();
            }
        });
        //设置用户名整个item的点击事件
        mNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity()
                        , "我是onclick事件显示的", Toast.LENGTH_SHORT).show();
            }
        });

        mSex.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(), "性别暂时不支持修改哦～", Toast.LENGTH_SHORT).show();
            }
        });
        mclassinfo.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {

            }
        });
        mSignName.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                LayoutInflater factory = LayoutInflater.from(getActivity());//提示框
                final View view = factory.inflate(R.layout.dialog_intro, null);//这里必须是final的
                final EditText etintro = view.findViewById(R.id.etIntro);//获得输入框对象

                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入新的个性签名")//提示框标题
                        .setView(view)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (etintro.getText().equals("") || etintro.getText() == null || etintro.getText().length() == 0) {
                                            Toast.makeText(getActivity(), "个性签名输入不合法,本次设置无效", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            try {
                                                final JSONObject object = new JSONObject();
                                                object.put("introduction", etintro.getText().toString());
                                                //事件
                                                OkHttpUtils.patch().addHeader("Authorization", "Bearer " + app.getToken())
                                                        .requestBody(new RequestBody() {
                                                            @Override
                                                            public MediaType contentType() {
                                                                return MediaType.parse("application/json; charset=utf-8");
                                                            }

                                                            @Override
                                                            public void writeTo(BufferedSink sink) throws IOException {
                                                                sink.outputStream().write(object.toString().getBytes());
                                                            }

                                                        })
                                                        .url(BaseRequest.BASEURL + "users/" + app.getStuid() + "/intro").build()
                                                        .execute(new StringCallback() {
                                                            @Override
                                                            public void onError(Call call, Exception e, int id) {
                                                                //   Log.e(TAG, e.toString());
                                                            }

                                                            @Override
                                                            public void onResponse(String response, int id) {
                                                                // Log.e(TAG, response.toString());
                                                                mSignName.setRightDesc(etintro.getText().toString());
                                                            }

                                                        });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                }).setNegativeButton("取消", null).setCancelable(false).create().show();

            }
        });

        mPass.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(), "即将推出", Toast.LENGTH_SHORT).show();
            }
        });
        mLogout.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils.delete().addHeader("Authorization", "Bearer " + app.getToken()).url(BaseRequest.BASEURL + "auth/logout").build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                app.getEditor().putString("AUTOLOGIN", "false").commit();
                                //  Log.e(TAG,response);
                                startActivity(new Intent(getContext(), LoginActivity.class));
                            }
                        });

                    }
                }).start();
            }
        });
        mHHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ModifyPhotoBottomDialog editNameDialog = new ModifyPhotoBottomDialog();
                editNameDialog.show(fm, "fragment_bottom_dialog");

            }
        });



    }

    @Override
    public void onPause() {
        super.onPause();
       isgetdata = false;
    }

    @Override
    public void onResume() {
        super.onResume();

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("loglogloglog","logloglog");
            if (requestCode == 0x666 && data!=null){
            InputStream ins= null;
            try {
                ins =getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap pic = BitmapFactory.decodeStream(ins);
                mHHead.setImageBitmap(pic);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void initView() {
        //顶部头像控件
        mHBack = getView().findViewById(R.id.h_back);
        mHHead = getView().findViewById(R.id.h_head);
        //mUserLine = getView().findViewById(R.id.user_line);
        //mUserName = getView().findViewById(R.id.user_name);
        //mUserVal = getView().findViewById(R.id.user_val);
        //下面item控件
        studentName = getView().findViewById(R.id.Name);
        mLogout=getView().findViewById(R.id.logout_item);
        mNickName = getView().findViewById(R.id.nickName);
        mSex = getView().findViewById(R.id.sex);
        mSignName = getView().findViewById(R.id.signName);
        mPass = getView().findViewById(R.id.pass);
        //mPhone = getView().findViewById(R.id.phone);
        mAbout = getView().findViewById(R.id.about);
        mclassinfo = getView().findViewById(R.id.classinfo);
        mclassinfo.setShowRightArrow(false);
        mclassinfo.setShowBottomLine(false);
        studentName.setShowBottomLine(false);
        studentName.setShowRightArrow(false);
        mLogout.setShowRightArrow(false);
        mLogout.setShowBottomLine(true);
    }



}
