package com.hui.picture.lab;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditOptionsFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    
    public static EditOptionsFragment newInstance(int position) {
        EditOptionsFragment fragment = new EditOptionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_options, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        int position = getArguments().getInt(ARG_POSITION);
        EditImageActivity activity = (EditImageActivity) getActivity();
        if (activity != null) {
            recyclerView.setAdapter(new EditOptionsAdapter(getOptions(position), position, activity.getOriginalBitmap()));
        }
        
        return view;
    }

    private String[] getOptions(int position) {
        switch (position) {
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
} 