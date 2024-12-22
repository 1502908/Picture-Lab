package com.hui.picture.lab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SaveSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_success);

        // 添加返回按钮点击事件
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Uri imageUri = getIntent().getData();
        
        ImageView editedImage = findViewById(R.id.edited_image);
        editedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        editedImage.setImageURI(imageUri);
        
        ImageButton whatsappButton = findViewById(R.id.share_whatsapp);
        ImageButton facebookButton = findViewById(R.id.share_facebook);
        ImageButton messengerButton = findViewById(R.id.share_messenger);
        ImageButton twitterButton = findViewById(R.id.share_twitter);
        ImageButton mailButton = findViewById(R.id.share_mail);

        whatsappButton.setOnClickListener(v -> shareToApp(imageUri, "com.tencent.mm"));
        facebookButton.setOnClickListener(v -> shareToApp(imageUri, "com.tencent.mobileqq"));
        messengerButton.setOnClickListener(v -> shareToApp(imageUri, "com.tencent.mm"));
        twitterButton.setOnClickListener(v -> shareToApp(imageUri, "com.tencent.mm"));
        mailButton.setOnClickListener(v -> shareToApp(imageUri, "com.tencent.mm"));
    }

    private void shareToApp(Uri imageUri, String packageName) {
        if (imageUri == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setPackage(packageName);
        
        try {
            startActivity(shareIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 