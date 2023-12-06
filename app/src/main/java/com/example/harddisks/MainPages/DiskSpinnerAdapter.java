package com.example.harddisks.MainPages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harddisks.MainPages.HelpFunc.DiskDataClass;
import com.example.harddisks.R;

import java.util.List;

public class DiskSpinnerAdapter extends ArrayAdapter<DiskDataClass> {

    public DiskSpinnerAdapter(Context context, List<DiskDataClass> disks) {
        super(context, R.layout.my_spinner, disks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_spinner, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.diskSpinnerTextView);

        DiskDataClass disk = getItem(position);

        if (disk != null) {
            // Устанавливаем изображение и текст для каждого элемента
            textView.setText(disk.getModel());
        }

        return convertView;
    }
}
