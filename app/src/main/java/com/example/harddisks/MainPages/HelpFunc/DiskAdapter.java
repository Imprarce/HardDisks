package com.example.harddisks.MainPages.HelpFunc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.example.harddisks.MainPages.FavoriteFragment;
import com.example.harddisks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DiskAdapter extends ArrayAdapter<DiskDataClass> {

    private List<String> favoriteDisks = new ArrayList<>();

    private List<DiskDataClass> disksForFavorite = new ArrayList<>();
    private OnFavoriteChangeListener onFavoriteChangeListener;

    private DatabaseReference userFavoriteDisksRef;


    private Context context;
    private int resource;

    public DiskAdapter(@NonNull Context context, int resource, @NonNull List<DiskDataClass> disks) {
        super(context, resource, disks);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }
        DiskDataClass disk = getItem(position);


        ImageView imageViewDisk = view.findViewById(R.id.imageViewDisk);
        TextView mainTextDisk = view.findViewById(R.id.mainTextDisk);
        TextView textSecond = view.findViewById(R.id.textSecond);

        userFavoriteDisksRef = FirebaseDatabase.getInstance().getReference()
                .child("user_data")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favorite_disks");

        Button addToFavoriteButton = view.findViewById(R.id.add_in_favorite);
        Button removeFromFavoriteButton = view.findViewById(R.id.delete_in_favorite);

        loadFavoriteDisks();

        addToFavoriteButton.setOnClickListener(v -> {
            // Добавление диска в избранное
            if (disk != null && !favoriteDisks.contains(disk.getManufacturerCode())) {
                favoriteDisks.add(disk.getManufacturerCode());
                updateList(favoriteDisks);
            }
        });

        removeFromFavoriteButton.setOnClickListener(v -> {
            // Удаление диска из избранного
            if (disk != null && favoriteDisks.contains(disk.getManufacturerCode())) {
                int pos = favoriteDisks.indexOf(disk.getManufacturerCode());
                favoriteDisks.remove(disk.getManufacturerCode());
                if (onFavoriteChangeListener != null) {
                    disksForFavorite.remove(pos);
                    onFavoriteChangeListener.onFavoriteChanged(disksForFavorite);
                }
                updateList(favoriteDisks);
            }
        });


        if (disk != null) {

            Picasso.get().load(disk.getImageUrl()).into(imageViewDisk);

            if(disk.hasRaid()){
            mainTextDisk.setText(disk.getCapacity() + " ГБ Жесткий диск " + disk.getModel() + " [" + disk.getManufacturerCode() + "] " + disk.getSpeedInterface() + " Гбит/с, " + disk.getSpindleSpeed() + " об/мин, кэш память - " +
                    disk.getCacheSize() + ", RAID");
            } else {
                mainTextDisk.setText(disk.getCapacity() + " ГБ Жесткий диск " + disk.getModel() + " [" + disk.getManufacturerCode() + "] " + disk.getSpeedInterface() + " Гбит/с, " + disk.getSpindleSpeed() + " об/мин, кэш память - " +
                        disk.getCacheSize());
            }

            textSecond.setText("Описание будет добавлено позже");

        }

        return view;
    }

    public void updateData(List<DiskDataClass> newDisks) {
        clear();
        addAll(newDisks);
        notifyDataSetChanged();
    }


    private void loadFavoriteDisks() {
        userFavoriteDisksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                favoriteDisks.clear();
                disksForFavorite.clear();

                for (DataSnapshot diskSnapshot : dataSnapshot.getChildren()) {
                    String manufacturerCode = diskSnapshot.getValue(String.class);
                    if (manufacturerCode != null) {
                        // Здесь используется manufacturerCode для получения диска из базы данных
                        DiskDatabaseHelper dbHelper = new DiskDatabaseHelper(context);
                        DiskDataClass disk = dbHelper.getDiskByManufacturerCode(manufacturerCode);
                        disksForFavorite.add(disk);
                        favoriteDisks.add(manufacturerCode);
                    }
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }

    public void updateList(List<String> favoriteDisksList){

        userFavoriteDisksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Очищает существующий список в Firebase
                userFavoriteDisksRef.removeValue();

                // Проходит по списку и добавляет только уникальные manufacturerCode
                for (String manufacturerCode : favoriteDisksList) {
                    if (!dataSnapshot.hasChild(manufacturerCode)) {
                        userFavoriteDisksRef.push().setValue(manufacturerCode);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки при чтении из Firebase
            }
        });
    }

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener listener) {
        this.onFavoriteChangeListener = listener;
    }
}
