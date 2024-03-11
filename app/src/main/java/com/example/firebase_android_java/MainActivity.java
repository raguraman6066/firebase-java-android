package com.example.firebase_android_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText name,age;
    TextView person_data;
    Button save,load;
    DatabaseReference reference;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=findViewById(R.id.person_name);
        age=findViewById(R.id.person_age);
        save=findViewById(R.id.button);
        load=findViewById(R.id.button2);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        person_data=findViewById(R.id.person_result);
        reference= FirebaseDatabase.getInstance().getReference("Person");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String person_name=name.getText().toString();
                int person_age= Integer.parseInt(age.getText().toString());

                //logic to write data insert to database
                Person person=new Person(person_name,person_age);
                reference.push().setValue(person).addOnSuccessListener(unused -> {
                    Toast.makeText(MainActivity.this, "Data is sent successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Not able to send data. Try again", Toast.LENGTH_SHORT).show();
                    }
                });

                //reset
                name.getText().clear();
                age.getText().clear();
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                reference.orderByKey().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       progressBar.setVisibility(View.INVISIBLE);
                       person_data.setText("");
                       for(DataSnapshot d:snapshot.getChildren()){
                           Person person=d.getValue(Person.class);
                           person_data.append(person.getName()+" "+person.getAge()+"\n");
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}