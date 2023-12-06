package com.example.harddisks.MainPages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.harddisks.MainPages.HelpFunc.DiskAdapter;
import com.example.harddisks.MainPages.HelpFunc.DiskDataClass;
import com.example.harddisks.MainPages.HelpFunc.DiskDatabaseHelper;
import com.example.harddisks.MainPages.HelpFunc.OnFavoriteChangeListener;
import com.example.harddisks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FavoriteFragment extends Fragment implements OnFavoriteChangeListener {

    private List<DiskDataClass> favoriteDisks;
    private Handler handler = new Handler();

    private ImageView upArrow;
    private ImageView downArrow;
    private DiskAdapter adapter;

    private boolean up = false;
    private boolean down = false;

    private DatabaseReference userFavoriteDisksRef;
    private DiskDatabaseHelper diskDatabaseHelper;

    private ListView disksList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        userFavoriteDisksRef = FirebaseDatabase.getInstance().getReference()
                .child("user_data")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favorite_disks");

        diskDatabaseHelper = new DiskDatabaseHelper(requireContext());


        favoriteDisks = new ArrayList<>();
        adapter = new DiskAdapter(requireContext(), R.layout.list_item_disk, favoriteDisks);
        adapter.setOnFavoriteChangeListener(this);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        disksList = view.findViewById(R.id.disksList);

        ImageView home = (ImageView) view.findViewById(R.id.main_menu_logo);
        ImageView author = (ImageView) view.findViewById(R.id.user_logo);
        ImageView about_prog = (ImageView) view.findViewById(R.id.about_prog_logo);
        ImageView instruction = (ImageView) view.findViewById(R.id.instruction_logo);
        ImageView compare = (ImageView) view.findViewById(R.id.compare_logo);
        upArrow = (ImageView) view.findViewById(R.id.upForMemory);
        downArrow = (ImageView) view.findViewById(R.id.downForMemory);
        CardView memory = (CardView) view.findViewById(R.id.memory);

        memory.setOnClickListener(view1 -> filterByMemory());
        home.setOnClickListener(view1 -> navController.navigate(R.id.action_favoriteFragment_to_disksFragment));
        author.setOnClickListener(view1 -> navController.navigate(R.id.action_favoriteFragment_to_authorFragment));
        about_prog.setOnClickListener(view1 -> navController.navigate(R.id.action_favoriteFragment_to_programInfoFragment));
        instruction.setOnClickListener(view1 -> navController.navigate(R.id.action_favoriteFragment_to_instructionManualFragment));
        compare.setOnClickListener(view1 -> navController.navigate(R.id.action_favoriteFragment_to_compareFragment));

        loadFavoriteDisks();

    }

    private void loadFavoriteDisks() {
        userFavoriteDisksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                favoriteDisks.clear();

                for (DataSnapshot diskSnapshot : dataSnapshot.getChildren()) {
                    String manufacturerCode = diskSnapshot.getValue(String.class);
                    if (manufacturerCode != null) {
                        // Здесь используется manufacturerCode для получения диска из базы данных
                        DiskDatabaseHelper dbHelper = new DiskDatabaseHelper(requireContext());
                        DiskDataClass disk = dbHelper.getDiskByManufacturerCode(manufacturerCode);
                        if (disk != null) {
                            favoriteDisks.add(disk);
                        }
                    }
                }

                disksList.setAdapter(adapter);

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }

    @Override
    public void onFavoriteChanged(List<DiskDataClass> updatedFavoriteDisks) {
        // Обновите ваш список избранных во фрагменте
        favoriteDisks = updatedFavoriteDisks;
        // Теперь обновите адаптер
        adapter.updateData(favoriteDisks);
    }

    protected void filterByMemory() {
        Comparator<DiskDataClass> memoryComparator = new Comparator<DiskDataClass>() {
            @Override
            public int compare(DiskDataClass disk1, DiskDataClass disk2) {
                int memory1 = disk1.getCapacity();
                int memory2 = disk2.getCapacity();
                return Integer.compare(memory1, memory2);
            }
        };

        if (down) {
            Collections.sort(favoriteDisks, memoryComparator);
            up = true;
            down = false;
            upArrow.setVisibility(View.VISIBLE);
            downArrow.setVisibility(View.INVISIBLE);
        } else {
            Collections.sort(favoriteDisks, Collections.reverseOrder(memoryComparator));
            up = false;
            down = true;
            upArrow.setVisibility(View.INVISIBLE);
            downArrow.setVisibility(View.VISIBLE);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }
}