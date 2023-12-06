package com.example.harddisks.AuthPages;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.harddisks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationFragment extends Fragment {

    private static boolean login_check = false;
    private static boolean password_check = false;
    private static boolean password_confirmed = false;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersRef = mDatabase.getReference("user_data");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        Button enter = view.findViewById(R.id.enterReg);
        Button registration = view.findViewById(R.id.registrationEmail);

        EditText login = view.findViewById(R.id.email_reg);
        EditText password = view.findViewById(R.id.password_reg);
        EditText passwordConfirmed = view.findViewById(R.id.password_confirmed_reg);

        enter.setOnClickListener(v -> navController.navigate(R.id.action_registration_to_authorization));

        registration.setOnClickListener(v -> {

            if(isPassword_check(password.getText().toString())){
                password_check = true;
            } else password_check = false;

            if(isConfirmedPassword((password).getText().toString(), (passwordConfirmed).getText().toString())){
                password_confirmed = true;
            } else password_confirmed = false;

            if(password_check && password_confirmed) {
                mAuth.createUserWithEmailAndPassword(login.getText().toString(), password.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();


                            Map<String, Object> user = new HashMap<>();
                            user.put("login", login.getText().toString());
                            user.put("password", password.getText().toString());


                            mUsersRef.child(userId).setValue(user);
                            Toast.makeText(getContext(), "Вы успешно зарегистировались", Toast.LENGTH_LONG).show();
                            navController.navigate(R.id.action_registration_to_authorization);
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "Данный логин уже занят", Toast.LENGTH_LONG).show();
                            }
                            if(exception instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(getContext(), "Вы неправильно ввели почту", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            } else if(!password_check) {
                Toast.makeText(getContext(), "Вы написали некорректный пароль, следуйте правилам", Toast.LENGTH_LONG).show();
            } else if(!password_confirmed){
                Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_LONG).show();
            }
        });

    }

    // Метод для проверки сложности пароля
    protected boolean isPassword_check(String password){
        Pattern pattern = Pattern.compile("^(?=.*[0-9]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Метод для проверки совпадения пароля и подтверждения пароля
    protected boolean isConfirmedPassword(String password, String password_confirmed){
        if(Objects.equals(password, password_confirmed)) return true;
        else return false;
    }
}