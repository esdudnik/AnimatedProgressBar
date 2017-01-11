package com.android.esdudnik.animatedprogressbar.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.esdudnik.animatedprogressbar.R;
import com.android.esdudnik.animatedprogressbar.ui.view.CashbackBar;

public class MainActivity extends AppCompatActivity {

    EditText integerAnimationTimeEditText;
    EditText decimalAnimationTimeEditText;
    EditText newValueEditText;
    Button addValueButton;
    CashbackBar cashbackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        integerAnimationTimeEditText = (EditText) findViewById(R.id.integerAnimationTimeEditText);
        decimalAnimationTimeEditText = (EditText) findViewById(R.id.decimalAnimationTimeEditText);
        newValueEditText = (EditText) findViewById(R.id.newValueEditText);
        addValueButton = (Button) findViewById(R.id.addValueButton);
        cashbackBar = (CashbackBar) findViewById(R.id.cashbackBar);
        addValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(integerAnimationTimeEditText.getEditableText().toString()))
                    cashbackBar.setIntegerPartStepAnimationDuration(Integer.parseInt(integerAnimationTimeEditText.getText().toString()));
                if (!TextUtils.isEmpty(decimalAnimationTimeEditText.getEditableText().toString()))
                    cashbackBar.setDecimalPartStepAnimationDuration(Integer.parseInt(decimalAnimationTimeEditText.getText().toString()));
                cashbackBar.addValue(Float.parseFloat(newValueEditText.getEditableText().toString()));
            }
        });
    }
}
