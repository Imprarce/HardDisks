package com.example.harddisks.MainPages;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.harddisks.MainPages.HelpFunc.DiskDataClass;
import com.example.harddisks.MainPages.HelpFunc.DiskDatabaseHelper;
import com.example.harddisks.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CompareFragment extends Fragment {

    private ImageView disk1ImageView;
    private TextView disk1NameTextView;

    private TextView disk1Model;
    private TextView disk1Code;
    private TextView disk1Capacity;
    private TextView disk1Spindle;
    private TextView disk1CacheSize;
    private TextView disk1Raid;

    private ImageView disk2ImageView;
    private TextView disk2NameTextView;
    private TextView disk2Model;
    private TextView disk2Code;
    private TextView disk2Capacity;
    private TextView disk2Spindle;
    private TextView disk2CacheSize;
    private TextView disk2Raid;

    private List<DiskDataClass> diskList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        ImageView home = (ImageView) view.findViewById(R.id.main_menu_logo);
        ImageView author = (ImageView) view.findViewById(R.id.user_logo);
        ImageView about_prog = (ImageView) view.findViewById(R.id.about_prog_logo);
        ImageView instruction = (ImageView) view.findViewById(R.id.instruction_logo);
        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_logo);


        // Настройка навигации и всех элементов в макете xml
        home.setOnClickListener(view1 -> navController.navigate(R.id.action_compareFragment_to_disksFragment));
        author.setOnClickListener(view1 -> navController.navigate(R.id.action_compareFragment_to_authorFragment));
        about_prog.setOnClickListener(view1 -> navController.navigate(R.id.action_compareFragment_to_programInfoFragment));
        instruction.setOnClickListener(view1 -> navController.navigate(R.id.action_compareFragment_to_instructionManualFragment));
        favorite.setOnClickListener(view1 -> navController.navigate(R.id.action_compareFragment_to_favoriteFragment));

        disk1ImageView = view.findViewById(R.id.disk1Image);
        disk1NameTextView = view.findViewById(R.id.disk1NameTextView);
        disk1Model = view.findViewById(R.id.disk1Model);
        disk1Code = view.findViewById(R.id.disk1Code);
        disk1Capacity = view.findViewById(R.id.disk1Capacity);
        disk1Spindle = view.findViewById(R.id.disk1SpindleSpeed);
        disk1CacheSize = view.findViewById(R.id.disk1CacheSize);
        disk1Raid = view.findViewById(R.id.disk1Raid);

        disk2ImageView = view.findViewById(R.id.disk2Image);
        disk2NameTextView = view.findViewById(R.id.disk2NameTextView);
        disk2Model = view.findViewById(R.id.disk2Model);
        disk2Code = view.findViewById(R.id.disk2Code);
        disk2Capacity = view.findViewById(R.id.disk2Capacity);
        disk2Spindle = view.findViewById(R.id.disk2SpindleSpeed);
        disk2CacheSize = view.findViewById(R.id.disk2CacheSize);
        disk2Raid = view.findViewById(R.id.disk2Raid);

        Spinner disk1Spinner = view.findViewById(R.id.disk1Spinner);
        Spinner disk2Spinner = view.findViewById(R.id.disk2Spinner);

        loadDiskList();

        // Создание адаптеров для Spinner
        DiskSpinnerAdapter spinner1Adapter = new DiskSpinnerAdapter(requireContext(), diskList);
        DiskSpinnerAdapter spinner2Adapter = new DiskSpinnerAdapter(requireContext(), diskList);

        // Установка адаптеров для Spinner
        disk1Spinner.setAdapter(spinner1Adapter);
        disk2Spinner.setAdapter(spinner2Adapter);

        // Установка слушателя для обработки выбора диска из Spinner
        disk1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DiskDataClass selectedDisk = (DiskDataClass) parentView.getSelectedItem();
                updateDiskFirstInfo(selectedDisk);
                // Обновление информации при выборе диска
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ничего не делаем, так как не выбран диск
            }
        });

        disk2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DiskDataClass selectedDisk = (DiskDataClass) parentView.getSelectedItem();
                updateDiskSecondInfo(selectedDisk);
                // Обновление информации при выборе диска
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ничего не делаем, так как не выбран диск
            }
        });
    }

    private void loadDiskList() {
        // Загрузка списка дисков из базы данных SQLite
        DiskDatabaseHelper dbHelper = new DiskDatabaseHelper(requireContext());
        diskList = dbHelper.getAllDisks();
    }

    private void updateDiskFirstInfo(DiskDataClass selectedDisk) {
        // Обновление информации о выбранном диске
        if (selectedDisk != null) {
            // Установка изображения, текста и другой информации для диска
            Picasso.get().load(selectedDisk.getImageUrl()).into(disk1ImageView);
            disk1NameTextView.setText(selectedDisk.getCapacity() + " ГБ Жесткий диск " + selectedDisk.getModel() + " [" + selectedDisk.getManufacturerCode() + "]");
            disk1Model.setText(selectedDisk.getModel());
            disk1Code.setText(selectedDisk.getManufacturerCode());
            disk1Capacity.setText(Integer.toString(selectedDisk.getCapacity()));
            disk1Spindle.setText(Integer.toString(selectedDisk.getSpindleSpeed()));
            disk1CacheSize.setText(Integer.toString(selectedDisk.getCacheSize()));
            if(selectedDisk.hasRaid()){
                disk1Raid.setText("Да");
            } else {
                disk1Raid.setText("Нет");
            }
        }
    }

    private void updateDiskSecondInfo(DiskDataClass selectedDisk) {
        // Обновление информации о выбранном диске
        if (selectedDisk != null) {
            // Установка изображения, текста и другой информации для диска
            Picasso.get().load(selectedDisk.getImageUrl()).into(disk2ImageView);
            disk2NameTextView.setText(selectedDisk.getCapacity() + " ГБ Жесткий диск " + selectedDisk.getModel() + " [" + selectedDisk.getManufacturerCode() + "]");
            disk2Model.setText(selectedDisk.getModel());
            disk2Code.setText(selectedDisk.getManufacturerCode());
            disk2Capacity.setText(Integer.toString(selectedDisk.getCapacity()));
            disk2Spindle.setText(Integer.toString(selectedDisk.getSpindleSpeed()));
            disk2CacheSize.setText(Integer.toString(selectedDisk.getCacheSize()));
            if(selectedDisk.hasRaid()){
                disk2Raid.setText("Да");
            } else {
                disk2Raid.setText("Нет");
            }
        }
    }
}
