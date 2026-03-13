package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        EditText edtName = findViewById(R.id.name);
        EditText edtLink = findViewById(R.id.link);
        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {

            String name = edtName.getText().toString();
            String link = edtLink.getText().toString();

            SharedPreferences prefs = getSharedPreferences("notes", MODE_PRIVATE);
            Gson gson = new Gson();

            String json = prefs.getString("list", null);
            Type type = new TypeToken<ArrayList<Note>>() {}.getType();

            List<Note> noteList = json == null ? new ArrayList<>() :
                    gson.fromJson(json, type);

            noteList.add(new Note(name, link));

            prefs.edit().putString("list", gson.toJson(noteList)).apply();

            finish();
        });
    }
}