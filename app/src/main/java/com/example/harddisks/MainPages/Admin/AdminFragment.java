package com.example.harddisks.MainPages.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.harddisks.R;


public class AdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);

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
        ImageView compare = (ImageView) view.findViewById(R.id.compare_logo);
        Button addDisk = (Button) view.findViewById(R.id.add_hd);


        home.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_disksFragment));
        addDisk.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_addDiskFragment));
        author.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_authorFragment));
        about_prog.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_programInfoFragment));
        instruction.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_instructionManualFragment));
        favorite.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_favoriteFragment));
        compare.setOnClickListener(view1 -> navController.navigate(R.id.action_adminFragment_to_compareFragment));
    }
}