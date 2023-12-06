package com.example.harddisks.MainPages;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthorFragment extends Fragment {

    private DatabaseReference userAdminRoot;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author, container, false);

        userAdminRoot = FirebaseDatabase.getInstance().getReference()
                .child("user_data")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
        Button adminButton = (Button) view.findViewById(R.id.adminButton);

        home.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_disksFragment));
        home.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_disksFragment));
        about_prog.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_programInfoFragment));
        instruction.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_instructionManualFragment));
        favorite.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_favoriteFragment));
        compare.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_compareFragment));

        userAdminRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean haveRoot = dataSnapshot.child("adminRoot").getValue(Boolean.class);

                if(haveRoot != null && haveRoot){
                    adminButton.setVisibility(View.VISIBLE);
                    adminButton.setOnClickListener(view1 -> navController.navigate(R.id.action_authorFragment_to_adminFragment));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }
}