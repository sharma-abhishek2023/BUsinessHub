package com.example.visha.businesspocket;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class ownerLogIn extends Fragment {

    public static EditText name,businessName,officeNumber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.owner_login,container,false);

        name= v.findViewById(R.id.enterNameEditText);
        businessName= v.findViewById(R.id.businessNameEditText);
        officeNumber= v.findViewById(R.id.officeNumberEditText);

        return v;
    }
}
