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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.harddisks.R;


public class ProgramInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_program_info, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        ImageView home = (ImageView) view.findViewById(R.id.main_menu_logo);
        ImageView author = (ImageView) view.findViewById(R.id.user_logo);
        ImageView instruction = (ImageView) view.findViewById(R.id.instruction_logo);
        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_logo);
        ImageView compare = (ImageView) view.findViewById(R.id.compare_logo);
        WebView webView = view.findViewById(R.id.github);

        home.setOnClickListener(view1 -> navController.navigate(R.id.action_programInfoFragment_to_disksFragment));
        author.setOnClickListener(view1 -> navController.navigate(R.id.action_programInfoFragment_to_authorFragment));
        instruction.setOnClickListener(view1 -> navController.navigate(R.id.action_programInfoFragment_to_instructionManualFragment));
        favorite.setOnClickListener(view1 -> navController.navigate(R.id.action_programInfoFragment_to_favoriteFragment));
        compare.setOnClickListener(view1 -> navController.navigate(R.id.action_programInfoFragment_to_compareFragment));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://github.com/IconProg/HardDiskProg");
    }
}