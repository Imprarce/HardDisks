package com.example.harddisks.MainPages.Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.harddisks.MainPages.HelpFunc.DiskDataClass;
import com.example.harddisks.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class AddDiskFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference disksDatabaseReference;

    private EditText sizeEditText, nameEditText, manufacturerCodeEditText,
            interfaceSpeedEditText, spindleSpeedEditText, cacheSizeEditText, raidEditText;

    private ImageView exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_disk, container, false);

        // Инициализируются Firebase Storage и Realtime Database
        storageReference = FirebaseStorage.getInstance().getReference("disk_images");
        disksDatabaseReference = FirebaseDatabase.getInstance().getReference("disks_data");

        // Инициализируются все EditText
        sizeEditText = view.findViewById(R.id.size_hd);
        nameEditText = view.findViewById(R.id.name_hd);
        manufacturerCodeEditText = view.findViewById(R.id.manufacturerCode_hd);
        interfaceSpeedEditText = view.findViewById(R.id.interface_speed);
        spindleSpeedEditText = view.findViewById(R.id.spindle_speed);
        cacheSizeEditText = view.findViewById(R.id.cache_size);
        raidEditText = view.findViewById(R.id.raid);
        exit = view.findViewById(R.id.exit_from_add);


        // Кнопка для выбора изображения
        Button addImageButton = view.findViewById(R.id.add_image);
        addImageButton.setOnClickListener(v -> openFileChooser());

        // Кнопка для загрузки данных в Firebase
        Button saveDataButton = view.findViewById(R.id.save_data_button);
        saveDataButton.setOnClickListener(v -> saveDataToFirebase());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        exit.setOnClickListener(view1 -> navController.navigate(R.id.action_addDiskFragment_to_adminFragment));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void saveDataToFirebase() {
        // Проверка на заполненость необходимых полей
        if (imageUri == null || sizeEditText.getText().toString().isEmpty()
                || nameEditText.getText().toString().isEmpty()
                || manufacturerCodeEditText.getText().toString().isEmpty()
                || interfaceSpeedEditText.getText().toString().isEmpty()
                || spindleSpeedEditText.getText().toString().isEmpty()
                || cacheSizeEditText.getText().toString().isEmpty()
                || raidEditText.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int size = Integer.parseInt(sizeEditText.getText().toString());
        int interfaceSpeed = Integer.parseInt(interfaceSpeedEditText.getText().toString());
        int spindleSpeed = Integer.parseInt(spindleSpeedEditText.getText().toString());
        int cacheSize = Integer.parseInt(cacheSizeEditText.getText().toString());
        boolean raid = Boolean.parseBoolean(raidEditText.getText().toString());

        // Создание уникального имени файла для изображения
        String fileName = FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + System.currentTimeMillis() + ".jpg";

        // Получение ссылки на место в Storage, где будет хранится файл
        StorageReference fileReference = storageReference.child(fileName);

        // Загрузка изображения в Storage
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Получение ссылки на загруженное изображение
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Создание объекта DiskDataClass с введенными данными и ссылкой на изображение
                        DiskDataClass diskData = new DiskDataClass(
                                uri.toString(),
                                nameEditText.getText().toString(),
                                manufacturerCodeEditText.getText().toString(),
                                size,
                                interfaceSpeed,
                                spindleSpeed,
                                cacheSize,
                                raid
                        );

                        // Сохранение данныых в Realtime Database
                        String diskId = disksDatabaseReference.push().getKey();
                        disksDatabaseReference.child(Objects.requireNonNull(diskId)).setValue(diskData)
                                .addOnSuccessListener(aVoid -> {
                                    // Успешно сохранено
                                    Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
                                    // Очистка поля ввода
                                    clearInputFields();
                                })
                                .addOnFailureListener(e -> {
                                    // Ошибка при сохранении
                                    Toast.makeText(requireContext(), "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // Ошибка при загрузке изображения
                    Toast.makeText(requireContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputFields() {
        sizeEditText.setText("");
        nameEditText.setText("");
        manufacturerCodeEditText.setText("");
        interfaceSpeedEditText.setText("");
        spindleSpeedEditText.setText("");
        cacheSizeEditText.setText("");
        raidEditText.setText("");
        imageUri = null;
    }
}
