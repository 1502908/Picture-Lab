package com.hui.picture.lab;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EditOptionsAdapter extends RecyclerView.Adapter<EditOptionsAdapter.ViewHolder> {
    private final String[] options;
    private final int type;
    private final Bitmap previewBitmap;
    private static ViewHolder lastSelectedHolder = null;  // 使用静态变量跟踪上一个选中项

    public EditOptionsAdapter(String[] options, int type, Bitmap originalBitmap) {
        this.options = options;
        this.type = type;
        // 创建一个缩小版的预览图
        this.previewBitmap = Bitmap.createScaledBitmap(originalBitmap, 120, 120, true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.optionName.setText(options[position]);
        
        // 设置预览图
        Bitmap preview = previewBitmap.copy(previewBitmap.getConfig(), true);
        holder.optionPreview.setImageBitmap(preview);
        
        // 应用对应的效果到预览图
        applyPreviewEffect(holder.optionPreview, position);
        
        // 清除之前的背景
        holder.itemView.setBackground(null);
        
        holder.itemView.setOnClickListener(v -> {
            EditImageActivity activity = (EditImageActivity) v.getContext();
            
            // 清除上一个选中项的背景
            if (lastSelectedHolder != null) {
                lastSelectedHolder.itemView.setBackground(null);
            }
            
            // 设置当前选中项的背景
            holder.itemView.setBackground(v.getContext().getResources()
                .getDrawable(R.drawable.selected_option_background));
            lastSelectedHolder = holder;
            
            switch (type) {
                case 0:
                    activity.applyEffect(options[position]);
                    break;
                case 1:
                    activity.applyFilter(options[position]);
                    break;
                case 2:
                    activity.applySticker(options[position]);
                    break;
            }
        });
    }

    private void applyPreviewEffect(ImageView imageView, int position) {
        switch (type) {
            case 0: // 特效
                applyEffectPreview(imageView, position);
                break;
            case 1: // 滤镜
                applyFilterPreview(imageView, position);
                break;
            case 2: // 贴纸
                applyStickerPreview(imageView, position);
                break;
        }
    }

    private void applyEffectPreview(ImageView imageView, int position) {
        ColorMatrix cm = new ColorMatrix();
        switch (position) {
            case 0: // 模糊
                // 模糊效果预览
                break;
            case 1: // 锐化
                // 锐化效果预览
                break;
            case 2: // 对比度
                cm = new ColorMatrix(new float[]{
                    1.2f, 0, 0, 0, 0,
                    0, 1.2f, 0, 0, 0,
                    0, 0, 1.2f, 0, 0,
                    0, 0, 0, 1, 0
                });
                break;
            case 3: // 亮度
                cm = new ColorMatrix(new float[]{
                    1, 0, 0, 0, 30,
                    0, 1, 0, 0, 30,
                    0, 0, 1, 0, 30,
                    0, 0, 0, 1, 0
                });
                break;
            case 4: // 饱和度
                cm.setSaturation(1.5f);
                break;
        }
        imageView.setColorFilter(new ColorMatrixColorFilter(cm));
    }

    private void applyFilterPreview(ImageView imageView, int position) {
        ColorMatrix cm = new ColorMatrix();
        switch (position) {
            case 0: // 复古
                cm.setSaturation(0.5f);
                cm.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                break;
            case 1: // 黑白
                cm.setSaturation(0);
                break;
            case 2: // 怀旧
                cm.setScale(0.9f, 0.9f, 0.7f, 1.0f);
                break;
            case 3: // 冷色调
                cm.setScale(0.8f, 1.0f, 1.2f, 1.0f);
                break;
            case 4: // 暖色调
                cm.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                break;
        }
        imageView.setColorFilter(new ColorMatrixColorFilter(cm));
    }

    private void applyStickerPreview(ImageView imageView, int position) {
        // 为贴纸选项设置预览图
        switch (position) {
            case 0: // 表情
                imageView.setImageResource(R.drawable.preview_emoji);
                break;
            case 1: // 装饰
                imageView.setImageResource(R.drawable.preview_decoration);
                break;
            case 2: // 文字
                imageView.setImageResource(R.drawable.preview_text);
                break;
            case 3: // 边框
                imageView.setImageResource(R.drawable.preview_frame);
                break;
            case 4: // 气泡
                imageView.setImageResource(R.drawable.preview_bubble);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView optionName;
        ImageView optionPreview;

        ViewHolder(View view) {
            super(view);
            optionName = view.findViewById(R.id.option_name);
            optionPreview = view.findViewById(R.id.option_preview);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.optionPreview.setImageBitmap(null);
    }
} 