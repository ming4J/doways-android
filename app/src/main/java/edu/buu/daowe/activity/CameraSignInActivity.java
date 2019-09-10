package edu.buu.daowe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;
import edu.buu.daowe.Util.Base64Util;
import edu.buu.daowe.http.BaseRequest;
import okhttp3.Call;
import okhttp3.MediaType;

public class CameraSignInActivity extends Activity {
    ImageView mIvScan;
    DaoWeApplication app;
    public String TAG = "CameraSignInActivity";
    /**
     * 0:从上往下 1:从下往上
     */
    Animation mTop2Bottom, mBottom2Top;
    private ImageView btn;
    //   private ImageView iv;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.ShutterCallback shutter;
    private Camera.PictureCallback jepg;
    Intent it;
    Bundle datas;

    JSONObject senddata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (DaoWeApplication) getApplication();
        it = getIntent();
        datas = it.getBundleExtra("datas");
        if (datas != null) {
            setContentView(R.layout.activity_face_main);

            senddata = new JSONObject();
            senddata.put("buildingUuid", datas.getString("buildingUuid"));
            senddata.put("md5", datas.getString("md5"));
            senddata.put("floorsMajor", datas.getInt("floorsMajor"));
            senddata.put("roomMinor", datas.getInt("roomMinor"));
            senddata.put("timeId", datas.getInt("timeId"));
            senddata.put("id", datas.getString("id"));
            initview();

        } else {
            this.finish();
        }


    }

    private void initview() {
        mIvScan = findViewById(R.id.scan_line);

        btn = findViewById(R.id.test);
        mTop2Bottom = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.7f);

        mBottom2Top = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.7f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f);

        mBottom2Top.setRepeatMode(Animation.RESTART);
        mBottom2Top.setInterpolator(new LinearInterpolator());
        mBottom2Top.setDuration(1500);
        mBottom2Top.setFillEnabled(true);//使其可以填充效果从而不回到原地
        mBottom2Top.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        mBottom2Top.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvScan.startAnimation(mTop2Bottom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTop2Bottom.setRepeatMode(Animation.RESTART);
        mTop2Bottom.setInterpolator(new LinearInterpolator());
        mTop2Bottom.setDuration(1500);
        mTop2Bottom.setFillEnabled(true);
        mTop2Bottom.setFillAfter(true);
        mTop2Bottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvScan.startAnimation(mBottom2Top);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIvScan.startAnimation(mTop2Bottom);


        surfaceView = (SurfaceView) findViewById(R.id.cameraSV);
        surfaceHolder = surfaceView.getHolder();
        SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }

            //获取前置摄像头
            public Camera getCamera() {
                camera = null;
                Camera.CameraInfo info = new Camera.CameraInfo();
                int cnt = Camera.getNumberOfCameras();
                for (int i = 0; i < cnt; i++) {
                    Camera.getCameraInfo(i, info);

                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        try {
                            camera = Camera.open(i);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return camera;
            }



            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                camera = getCamera();
                try {
                    camera.setPreviewDisplay(holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPictureSize(320, 240);
                parameters.getFocusMode();
                parameters.setPictureFormat(PixelFormat.JPEG);
                camera.setParameters(parameters);
                //
                camera.setDisplayOrientation(90);
                camera.startPreview();
            }
        };
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置显示图片
        jepg = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                camera.startPreview();

                final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                //  iv.setImageBitmap(bm);
                Toast.makeText(CameraSignInActivity.this, "拍照成功！", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //    Log.e(TAG, Base64Util.encode(data));

                        OkHttpUtils.get().addHeader("Authorization", "Bearer " + app.getToken()).addHeader("Content-Type", "application/json")
                                .url(BaseRequest.BASEURL + "tools/" + app.getStuid() + "/face").build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                // Log.e(TAG,e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {

                                try {
                                    org.json.JSONObject mydata = new org.json.JSONObject(response);
                                    if (mydata.getInt("code") == 200) {
                                        String md = mydata.getString("data");
                                        // Log.e(TAG,md);
                                        String facetoken = new org.json.JSONObject(md).getString("faceToken");
                                        String accesstoken = new org.json.JSONObject(md).getString("accessToken");
                                        JSONArray array = new JSONArray();
                                        JSONObject data1 = new JSONObject();
                                        data1.put("image", Base64Util.encode(data));
                                        data1.put("image_type", "BASE64");
                                        data1.put("quality_control", "NONE");
                                        data1.put("face_type", "LIVE");
                                        data1.put("liveness_control", "NORMAL");


                                        JSONObject data2 = new JSONObject();
                                        data2.put("image_type", "URL" +
                                                "");
                                        data2.put("image", facetoken);
                                        //   data2.put("face_field", "faceshape,facetype");

                                        data2.put("quality_control", "NONE");
                                        data2.put("face_type", "LIVE");
                                        data2.put("liveness_control", "NONE");
                                        array.add(data1);
                                        array.add(data2);

                                        OkHttpUtils.postString().content(array.toJSONString()).mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .url("https://aip.baidubce.com/rest/2.0/face/v3/match?access_token=" + accesstoken).build().execute(new StringCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {
                                                //     Log.e(TAG, e.toString());
                                            }

                                            @Override
                                            public void onResponse(final String response, int id) {
                                                //   Log.e(TAG, response);
                                                try {
                                                    org.json.JSONObject result = new org.json.JSONObject(response);
                                                    if (result.getInt("error_code") == 0) {
                                                        result = new org.json.JSONObject(result.getString("result"));
                                                        final float score = (float) result.getDouble("score");
                                                        senddata.put("score", score);
                                                        //    Log.e("tagtag",senddata.toJSONString());
                                                        OkHttpUtils.postString().addHeader("Authorization", "Bearer " + app.getToken()).mediaType(MediaType.parse("application/json; charset=utf-8")).content(senddata.toJSONString())
                                                                .url(BaseRequest.BASEURL + "users/sign").build().execute(new StringCallback() {
                                                            @Override
                                                            public void onError(Call call, Exception e, int id) {
                                                                Log.e("tagtag", e.getMessage());
                                                            }

                                                            @Override
                                                            public void onResponse(String response, int id) {
                                                                try {
                                                                    final org.json.JSONObject data = new org.json.JSONObject(response);
                                                                    if (data.getInt("code") == 200) {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    if (data.getString("data").equals("1")) {
                                                                                        Toast.makeText(CameraSignInActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                                                                                        finish();
                                                                                    }

                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    Toast.makeText(CameraSignInActivity.this, data.getString("data"), Toast.LENGTH_SHORT).show();
                                                                                    finish();
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();


            }
        };

        shutter = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                // Toast.makeText(getApplicationContext(), "成功拍照", Toast.LENGTH_SHORT).show();

            }
        };
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(shutter, null, jepg);
            }
        });


    }


}
