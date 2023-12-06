package com.example.harddisks.MainPages;

import static android.content.ContentValues.TAG;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.harddisks.MainPages.HelpFunc.DiskAdapter;
import com.example.harddisks.MainPages.HelpFunc.DiskDataClass;
import com.example.harddisks.MainPages.HelpFunc.DiskDatabaseHelper;
import com.example.harddisks.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DisksFragment extends Fragment {

    private List<DiskDataClass> diskList;
    private List<DiskDataClass> originalDiskList;

    private Handler handler = new Handler();
    private ListView diskListView;
    private DiskAdapter adapter;

    private ImageView upArrow;
    private ImageView downArrow;

    private SearchView search;

    private boolean up = false;
    private boolean down = false;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference disksRef = database.getReference("disks_data");

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageRef = storage.getReference();


    private DiskDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_disks, container, false);

        dbHelper = new DiskDatabaseHelper(requireContext());


        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        diskList = dbHelper.getAllDisks();
        originalDiskList = new ArrayList<>(diskList); // Копирование оригинального списка
        getDisk();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        ImageView author = (ImageView) view.findViewById(R.id.user_logo);
        ImageView about_prog = (ImageView) view.findViewById(R.id.about_prog_logo);
        ImageView instruction = (ImageView) view.findViewById(R.id.instruction_logo);
        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_logo);
        ImageView compare = (ImageView) view.findViewById(R.id.compare_logo);
        upArrow = (ImageView) view.findViewById(R.id.upForMemory);
        downArrow = (ImageView) view.findViewById(R.id.downForMemory);
        CardView memory = (CardView) view.findViewById(R.id.memory);
        search = (SearchView) view.findViewById(R.id.search);
        diskListView = (ListView) view.findViewById(R.id.disksList);

        author.setOnClickListener(view1 -> navController.navigate(R.id.action_disksFragment_to_authorFragment));
        about_prog.setOnClickListener(view1 -> navController.navigate(R.id.action_disksFragment_to_programInfoFragment));
        instruction.setOnClickListener(view1 -> navController.navigate(R.id.action_disksFragment_to_instructionManualFragment));
        favorite.setOnClickListener(view1 -> navController.navigate(R.id.action_disksFragment_to_favoriteFragment));
        compare.setOnClickListener(view1 -> navController.navigate(R.id.action_disksFragment_to_compareFragment));


        memory.setOnClickListener(view1 -> filterByMemory());

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Вызов метода фильтрации по имени диска с использованием введенного текста
                filterByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Вызов метода фильтрации по имени диска при изменении текста ввода
                filterByName(newText);
                return true;
            }
        });

    }

    public void getDisk() {
        disksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DiskDataClass> disks = new ArrayList<>();

                for (DataSnapshot diskSnapshot : dataSnapshot.getChildren()) {
                    DiskDataClass diskDataClass = diskSnapshot.getValue(DiskDataClass.class);
                    if (diskDataClass != null) {
                        disks.add(diskDataClass);
                    }
                }

                dbHelper.loadDisksFromFirebase(disks);
                addDisk();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error reading data", error.toException());
            }
        });

    }

    public void addDisk() {

        DiskDataClass wdBlueDisk = new DiskDataClass("https://firebasestorage.googleapis.com/v0/b/harddisks-3f306.appspot.com/o/WDBLUE.png?alt=media&token=7eb1e999-2b0d-4680-9d34-0cebc394f259",
                "WD Blue", "131123", 1000, 6, 7200, 64, true);
        DiskDataClass toshibaDisk = new DiskDataClass("https://firebasestorage.googleapis.com/v0/b/harddisks-3f306.appspot.com/o/Toshiba.png?alt=media&token=5caf76a3-8f05-4e62-8111-24c96a7c4b54",
                "Toshiba P300", "HDWD110UZSVA", 1000, 6, 7200, 64, false);

        if (dbHelper.isManufacturerCodeUnique(wdBlueDisk.manufacturerCode)) {
            disksRef.child(wdBlueDisk.manufacturerCode).setValue(wdBlueDisk);
        }

        if (dbHelper.isManufacturerCodeUnique(toshibaDisk.manufacturerCode)) {
            disksRef.child(toshibaDisk.manufacturerCode).setValue(toshibaDisk);
        }

        diskList.clear();
        diskList.addAll(dbHelper.getAllDisks());


        adapter = new DiskAdapter(requireContext(), R.layout.list_item_disk, diskList);
        diskListView.setAdapter(adapter);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
            Collections.sort(diskList, memoryComparator);
            up = true;
            down = false;
            upArrow.setVisibility(View.VISIBLE);
            downArrow.setVisibility(View.INVISIBLE);
        } else {
            Collections.sort(diskList, Collections.reverseOrder(memoryComparator));
            up = false;
            down = true;
            upArrow.setVisibility(View.INVISIBLE);
            downArrow.setVisibility(View.VISIBLE);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    protected void filterByName(String query) {
        List<DiskDataClass> filteredList = new ArrayList<>();

        for (DiskDataClass disk : originalDiskList) {
            // Приведение имени диска и введенного запрос к нижнему регистру и проверка, содержится ли запрос в имени диска
            if (disk.getModel().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(disk);
            }
        }

        // Обновление списка дисков с отфильтрованным списком
        diskList.clear();
        diskList.addAll(filteredList);

        // Обновление адаптера для отображения изменений
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


}