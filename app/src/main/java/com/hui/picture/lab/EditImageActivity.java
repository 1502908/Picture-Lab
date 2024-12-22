package com.hui.picture.lab;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;


public class EditImageActivity extends AppCompatActivity {
    private ImageView imagePreview;
    private RecyclerView subOptionsRecycler;
    private TextView effectOption, filterOption, stickerOption;
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        imagePreview = findViewById(R.id.image_preview);
        subOptionsRecycler = findViewById(R.id.sub_options_recycler);
        effectOption = findViewById(R.id.effect_option);
        filterOption = findViewById(R.id.filter_option);
        stickerOption = findViewById(R.id.sticker_option);
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton saveButton = findViewById(R.id.save_button);

        subOptionsRecycler.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Uri imageUri = getIntent().getData();
        if (imageUri != null) {
            try {
                originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                imagePreview.setImageBitmap(currentBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveImage());
        setupClickListeners();
    }

    private void setupClickListeners() {
        effectOption.setOnClickListener(v -> showSubOptions(0));
        filterOption.setOnClickListener(v -> showSubOptions(1));
        stickerOption.setOnClickListener(v -> showSubOptions(2));
    }

    private void showSubOptions(int type) {
        subOptionsRecycler.setVisibility(View.VISIBLE);
        subOptionsRecycler.setAdapter(new EditOptionsAdapter(getOptions(type), type, originalBitmap));
    }

    private String[] getOptions(int type) {
        switch (type) {
            case 0: // 特效
                return new String[]{"模糊", "锐化", "对比度", "亮度", "饱和度"};
            case 1: // 滤镜
                return new String[]{"复古", "黑白", "怀旧", "冷色调", "暖色调"};
            case 2: // 贴纸
                return new String[]{"表情", "装饰", "文字", "边框", "气泡"};
            default:
                return new String[]{};
        }
    }

    public void applyEffect(String effectName) {
        if (currentBitmap == null) return;
        
        switch (effectName) {
            case "模糊":
                applyBlurEffect();
                break;
            case "锐化":
                applySharpenEffect();
                break;
            case "对比度":
                applyContrastEffect();
                break;
            case "亮度":
                applyBrightnessEffect();
                break;
            case "饱和度":
                applySaturationEffect();
                break;
        }
    }

    public void applyFilter(String filterName) {
        if (currentBitmap == null) return;

        ColorMatrix colorMatrix = new ColorMatrix();
        switch (filterName) {
            case "复古":
                colorMatrix.setSaturation(0.5f);
                colorMatrix.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                break;
            case "黑白":
                colorMatrix.setSaturation(0);
                break;
            case "怀旧":
                colorMatrix.setScale(0.9f, 0.9f, 0.7f, 1.0f);
                break;
            case "冷色调":
                colorMatrix.setScale(0.8f, 1.0f, 1.2f, 1.0f);
                break;
            case "暖色调":
                colorMatrix.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                break;
        }
        imagePreview.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    public void applySticker(String stickerName) {
        int stickerResId = getStickerResourceId(stickerName);
        if (stickerResId != 0) {
            ViewGroup container = findViewById(R.id.sticker_container);
            
            // 先计算容器的尺寸
            float containerWidth = container.getWidth();
            float containerHeight = container.getHeight();
            
            // 创建贴纸视图并设置图片资源
            StickerView stickerView = new StickerView(this);
            stickerView.setImageResource(stickerResId);
            
            // 获取贴纸的原始尺寸
            int stickerWidth = stickerView.getDrawable().getIntrinsicWidth();
            int stickerHeight = stickerView.getDrawable().getIntrinsicHeight();
            
            // 计算初始缩放比例（使贴纸宽度为容器宽度的1/3）
            float scale = containerWidth / (stickerWidth * 3);
            
            // 计算居中位置
            float translateX = (containerWidth - stickerWidth * scale) / 2;
            float translateY = (containerHeight - stickerHeight * scale) / 2;
            
            // 创建初始变换矩阵
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            matrix.postTranslate(translateX, translateY);
            
            // 设置初始矩阵
            stickerView.setImageMatrix(matrix);
            stickerView.updateMatrix(matrix);
            
            // 添加到容器
            container.addView(stickerView);
            stickerView.bringToFront();
        }
    }

    private int getStickerResourceId(String stickerName) {
        switch (stickerName) {
            case "表情":
                return R.drawable.sticker_emoji;
            case "装饰":
                return R.drawable.sticker_decoration;
            case "文字":
                return R.drawable.sticker_text;
            case "边框":
                return R.drawable.sticker_frame;
            case "气泡":
                return R.drawable.sticker_bubble;
            default:
                return 0;
        }
    }

    private void applyBlurEffect() {
        // 实现模糊效果
        // 可以使用RenderScript或其他模算法
    }

    private void applySharpenEffect() {
        // 实现锐化效果
        // 可以使用卷积矩阵实现
    }

    private void applyContrastEffect() {
        ColorMatrix cm = new ColorMatrix(new float[]{
            1.2f, 0, 0, 0, 0,
            0, 1.2f, 0, 0, 0,
            0, 0, 1.2f, 0, 0,
            0, 0, 0, 1, 0
        });
        imagePreview.setColorFilter(new ColorMatrixColorFilter(cm));
    }

    private void applyBrightnessEffect() {
        ColorMatrix cm = new ColorMatrix(new float[]{
            1, 0, 0, 0, 30,
            0, 1, 0, 0, 30,
            0, 0, 1, 0, 30,
            0, 0, 0, 1, 0
        });
        imagePreview.setColorFilter(new ColorMatrixColorFilter(cm));
    }

    private void applySaturationEffect() {
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(1.5f);
        imagePreview.setColorFilter(new ColorMatrixColorFilter(cm));
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (originalBitmap != null) {
            originalBitmap.recycle();
        }
        if (currentBitmap != null) {
            currentBitmap.recycle();
        }
    }

    private void saveImage() {
        if (currentBitmap == null) return;

        try {
            // 创建最终的bitmap，包含所有效果
            Bitmap finalBitmap = createFinalBitmap();
            
            // 创建文件名
            String fileName = "PictureLab_" + System.currentTimeMillis() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            }

            // 保存图片
            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                try (OutputStream os = getContentResolver().openOutputStream(imageUri)) {
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, os);
                    
                    // 跳转保存成功页面
                    Intent intent = new Intent(this, SaveSuccessActivity.class);
                    intent.setData(imageUri);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap createFinalBitmap() {
        // 创建新的Bitmap，包含所有效果和贴纸
        Bitmap finalBitmap = Bitmap.createBitmap(
            currentBitmap.getWidth(),
            currentBitmap.getHeight(),
            currentBitmap.getConfig()
        );
        
        Canvas canvas = new Canvas(finalBitmap);
        
        // 绘制主图片（包含滤镜效��）
        Paint paint = new Paint();
        paint.setColorFilter(imagePreview.getColorFilter());
        canvas.drawBitmap(currentBitmap, 0, 0, paint);
        
        // 获取图片和容器的信息
        ViewGroup container = findViewById(R.id.sticker_container);
        ImageView imagePreview = findViewById(R.id.image_preview);
        
        // 获取图片在屏幕上的实际显示区域
        RectF viewRect = new RectF(0, 0, imagePreview.getWidth(), imagePreview.getHeight());
        RectF imageRect = new RectF(0, 0, currentBitmap.getWidth(), currentBitmap.getHeight());
        Matrix imageMatrix = imagePreview.getImageMatrix();
        
        // 计算从视图坐标到图片坐标的转换矩阵
        Matrix transformMatrix = new Matrix();
        transformMatrix.setRectToRect(viewRect, imageRect, Matrix.ScaleToFit.CENTER);
        
        // 绘制所有贴纸
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof StickerView) {
                StickerView stickerView = (StickerView) child;
                
                // 获取贴纸的变换矩阵
                Matrix stickerMatrix = new Matrix(stickerView.getMatrix());
                
                // 将贴纸的变换矩阵转换到图片坐标系
                stickerMatrix.postConcat(transformMatrix);
                
                // 保存画布状态
                canvas.save();
                
                // 应用变换并绘制贴纸
                canvas.concat(stickerMatrix);
                stickerView.getDrawable().setBounds(0, 0, 
                    stickerView.getDrawable().getIntrinsicWidth(),
                    stickerView.getDrawable().getIntrinsicHeight());
                stickerView.getDrawable().draw(canvas);
                
                // 恢复画布状态
                canvas.restore();
            }
        }
        
        return finalBitmap;
    }
} 