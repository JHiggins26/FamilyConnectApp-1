package com.example.jawanhiggins.familyconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText fistName = (EditText) findViewById(R.id.rFirstName);
        final EditText lastName = (EditText) findViewById(R.id.rLastName);
        final EditText userName = (EditText) findViewById(R.id.userName);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText rReEnterPassword = (EditText) findViewById(R.id.rReEnterPassword);
        final EditText rAge = (EditText) findViewById(R.id.rAge);
        final Button buttonRegister = (Button) findViewById(R.id.bRegister);



    }
}
