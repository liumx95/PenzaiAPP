package com.star.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.star.app.LocationActivity;
import com.star.app.R;
import com.star.app.SplashActivity;

import java.util.HashMap;

import static android.support.v7.appcompat.R.id.image;

/**
 * Created by liumx on 2017/4/17.
 */

public class RelationFragment extends Fragment {

    private EditText et_qr_code;
    private Button btn_qr;
    private FrameLayout fl_image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.relation_layout, null);
        et_qr_code= (EditText)view.findViewById(R.id.et_qrcode);
        btn_qr= (Button) view.findViewById(R.id.btn_qr);
        fl_image= (FrameLayout) view.findViewById(R.id.fl_image);
        if (et_qr_code.getText().toString().trim().equals("")){
            try {
                fl_image.addView(initView(SplashActivity.Bdlocation.getAddrStr()));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (et_qr_code.getText().toString().trim().equals("")){
                        Toast.makeText(getActivity(),"请输入您的花语", Toast.LENGTH_LONG).show();
                    }else {
                        try {
                            fl_image.addView(initView(et_qr_code.getText().toString().trim()));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }

        });
        return  view;
    }


    private ImageButton initView(String url) throws WriterException {
        final ImageButton image = new ImageButton(getActivity());
        //将资源文件转换成Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.flower);
        image.setImageBitmap(addLogo(makeQr(1000, 1000,url), bitmap));
        return image;
    }

    private Bitmap makeQr(int width, int height,String url) throws WriterException {
        HashMap<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //图像数据转换，使用了矩阵转换
        Log.e("qr",url);
        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width
                , height, hints);
        //创建一个像素数组
        int[] pixels = new int[bitMatrix.getWidth() * bitMatrix.getHeight()];
        //逐个生成二维码图片
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * bitMatrix.getWidth() + x] = 0xff00db00;
                } else {
                    pixels[y * bitMatrix.getHeight() + x] = 0xffffd9ec;
                }

            }
        }
        //生产二维码图片，使用ARGB
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        //offset偏移量，stride=宽度，

        return bitmap;
    }

    //在二维码中添加logo图案
    public static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return null;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体的大小1/6
        float scaleFactor = srcWidth * 1.0f / 6 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
        canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }


}
