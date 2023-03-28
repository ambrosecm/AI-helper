package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class CameraActivity extends AppCompatActivity {
    String TAG = "SurfaceViewActivity_AA";
    TextureView textureView = null;
    CaptureRequest.Builder mCaptureRequestBuilder;
    CaptureRequest mCaptureRequest;
    Surface mSurfacePrevew; //预览的surface
    ImageReader mImageReader;
    private Size mPreviewSize;
    private Size mCaptureSize;
    CameraDevice mCameraDevice;
    File mImageFile;
    int num=0;
    private CameraCaptureSession mCameraCaptureSession;
    ;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        textureView = (TextureView) findViewById(R.id.svPhoto);
        textureView.setSurfaceTextureListener(new SurfaceTextureListener());
        findViewById(R.id.btPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void OpenCamera() {
        try {
            Log.e(TAG, "OpenCamera");

            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                //遍历所有摄像头
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    //默认打开后置摄像头
                    if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
                        continue;
                    //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    //获取相机支持的最大拍照尺寸
                    mCaptureSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new Comparator<Size>() {
                        @Override
                        public int compare(Size lhs, Size rhs) {
                            return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth());
                        }
                    });
                    break;
                }


            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(manager.getCameraIdList()[0], new CameraState(), null);
        }catch (Exception ex){

        }
    }
    class CameraState extends CameraDevice.StateCallback {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            try {
                setupImageReader();
                SurfaceTexture mSurfaceTexture = textureView.getSurfaceTexture();
                //设置TextureView的缓冲区大小
                mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                //获取Surface显示预览数据
                mSurfacePrevew = new Surface(mSurfaceTexture);
                //需要同时增加预览和拍照的 Surface
                camera.createCaptureSession(Arrays.asList(mSurfacePrevew,mImageReader.getSurface()), new CameraSessionState(), null);
            }catch (Exception ex){}
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG,"CameraState ==> onDisconnected camera.id="+camera.getId());
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(TAG,"CameraState ==> handleMessage camera.id="+camera.getId()+"  error="+error);
        }
        @Override
        public void onClosed(CameraDevice camera) {
            super.onClosed(camera);
            Log.e(TAG,"CameraState ==> onClosed camera.id="+camera.getId());
            //当调用CameraDevice.close()关闭相机设备后会回调此方法。camera为别关闭的相机设备。
            //该方法被回调执行后，任何尝试调用camera相关的操作都会失败，并且抛出一个IllegalStateException异常
        }
    }

    class CameraSessionState extends   CameraCaptureSession.StateCallback{
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.e(TAG, "CameraSessionState onConfigured");
            try {
                //创建CaptureRequestBuilder，TEMPLATE_PREVIEW比表示预览请求
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                //设置Surface作为预览数据的显示界面
                mCaptureRequestBuilder.addTarget(mSurfacePrevew);
                //创建捕获请求
                mCaptureRequest = mCaptureRequestBuilder.build();
                mCameraCaptureSession = session;
                //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.e(TAG,"CameraSessionState onConfigureFailed");
        }
    }

    class SurfaceTextureListener implements TextureView.SurfaceTextureListener{
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.e(TAG,"SurfaceTextureListener===> onSurfaceTextureAvailable");
            mPreviewSize=new Size(width,height);
            OpenCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.e(TAG,"SurfaceTextureListener===> onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.e(TAG,"SurfaceTextureListener===> onSurfaceTextureDestroyed");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Log.e(TAG,"SurfaceTextureListener===> onSurfaceTextureUpdated");
        }
    }


    private void capture() {
        Log.e(TAG,"capture===>  ");
        try {
            //首先我们创建请求拍照的CaptureRequest
            if(mCameraDevice==null)
                return;

            final CaptureRequest.Builder mCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //获取屏幕方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            //设置CaptureRequest输出到mImageReader
            mCaptureBuilder.addTarget(mImageReader.getSurface());
            //设置拍照方向
            mCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));
            //这个回调接口用于拍照结束时重启预览，因为拍照会导致预览停止
            CameraCaptureSession.CaptureCallback mImageSavedCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    // Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    //重启预览
                    Log.e(TAG,"capture===>  restartPreview");
                    restartPreview();
                }
            };
            //停止预览
            mCameraCaptureSession.stopRepeating();
            //开始拍照，然后回调上面的接口重启预览，因为mCaptureBuilder设置ImageReader作为target，所以会自动回调ImageReader的onImageAvailable()方法保存图片
            mCameraCaptureSession.capture(mCaptureBuilder.build(), mImageSavedCallback, null);
            Log.e(TAG,"capture===>  end");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void restartPreview() {
        try {
            //执行setRepeatingRequest方法就行了，注意mCaptureRequest是之前开启预览设置的请求
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupImageReader() {
        //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据，本例的2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(mCaptureSize.getWidth(), mCaptureSize.getHeight(), ImageFormat.JPEG, 2);//YUV_420_888
        //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.e(TAG,"========onImageAvailable==========");
                Image image = reader.acquireLatestImage();
                new Thread(new imageSaver(image, System.currentTimeMillis()+"")).start();

            }
        }, null);
    }
    Handler mHandler = new Handler(){
        //handleMessage为处理消息的方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageView iv1=(ImageView)findViewById(R.id.imageView7);
            iv1.setImageURI(Uri.fromFile((File)msg.obj));
        }
    };
    Handler mHandler2 = new Handler(){
        //handleMessage为处理消息的方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView iv1=findViewById(R.id.textView11);
            iv1.setText((String)msg.obj);
        }
    };
    public class imageSaver implements Runnable {
        private Image mImage;
        private String imgName;
        String result;
        public imageSaver(Image image, String imgName) {
            mImage = image;
            this.imgName=imgName;
        }
        @Override
        public void run() {
            //我们可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant";
            try {
                String imgStr = Base64Util.encode(data);
                String imgParam = URLEncoder.encode(imgStr, "UTF-8");

                String param = "image=" + imgParam;

                // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
                String ak="sYA6fioXv0EwLSY5Ry4viGEv";
                String sk="aiTGg7oDq2Nm33VBOutjplNeixASGbYy";
                String accessToken = AuthService.getAuth(ak,sk);
                result = HttpUtil.post(url, accessToken, param);
                System.out.println(result);

            } catch (Exception e) {
                e.printStackTrace();
            }

            buffer.get(data);
            mImageFile = new File(getExternalCacheDir()+ "/"+System.currentTimeMillis()+".jpg");
            FileOutputStream fos = null;
            try {
                System.out.println(mImageFile.getAbsoluteFile());
                fos = new FileOutputStream(mImageFile);
                fos.write(data, 0 ,data.length);
                Message msg = Message.obtain();
                msg.obj=mImageFile;
                mHandler.sendMessage(msg);//sendMessage()用来传送Message类的值到mHandler
                Message msg2 = Message.obtain();
                msg2.obj="莲花";
                mHandler2.sendMessage(msg2);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImageFile = null;
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mImage.close();
            }
        }
    }

    //选择sizeMap中大于并且最接近width和height的size
    private Size getOptimalSize(Size[] sizeMap, int Width, int height){
        ArrayList<Size> list=new ArrayList<>();
        Log.e(TAG,"Width="+Width+",height="+height);
        for(Size size:sizeMap){
            Log.e(TAG,"size.w="+size.getWidth()+"   size.h="+size.getHeight());
            if(size.getWidth()>Width && size.getHeight()>height) {
                list.add(new Size(size.getWidth(),size.getHeight()));
                Log.e(TAG,"===============list  size.w="+size.getWidth()+"   size.h="+size.getHeight());
            }
        }
        if(list.size()>0){
            Size size= Collections.min(list, new Comparator<Size>() {
                @Override
                public int compare(Size o1, Size o2) {
                    return o1.getWidth()*o1.getHeight() - o2.getWidth()*o2.getHeight();
                }
            });
            Log.e(TAG,"最终 size.w="+size.getWidth()+"  ,size.h="+size.getHeight());
            return size;
        }else{
            return sizeMap[0];
        }
    }
}
