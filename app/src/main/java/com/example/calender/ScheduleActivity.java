package com.example.calender;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calender.domain.Schedule;
import com.example.calender.helper.Constants;

public class ScheduleActivity extends AppCompatActivity implements TextWatcher {
    private EditText editTextTitle;
    private EditText editTextStart;
    private EditText editTextEnd;
    private Button buttonConfirm;
    private Button buttonCancel;
    private Button buttonDeleteSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextStart = findViewById(R.id.editTextStart);
        editTextEnd = findViewById(R.id.editTextEnd);
        buttonConfirm = findViewById(R.id.confirm);
        buttonCancel = findViewById(R.id.cancel);
        buttonDeleteSchedule = findViewById(R.id.deleteSchedule);

        editTextTitle.addTextChangedListener(this);
        editTextStart.addTextChangedListener(this);
        editTextEnd.addTextChangedListener(this);

        editTextTitle.setText(getIntent().getStringExtra(Constants.KEY_TITLE));
        editTextStart.setText(getIntent().getStringExtra(Constants.KEY_START_TIME));
        editTextEnd.setText(getIntent().getStringExtra(Constants.KEY_END_TIME));

        boolean isNewSchedule = getIntent().getBooleanExtra(Constants.KEY_IS_NEW_SCHEDULE, true);

        ((TextView)findViewById(R.id.explan)).setText(isNewSchedule ? "새로운 일정" : "일정 수정");
        findViewById(R.id.deleteSchedule).setVisibility(isNewSchedule ? View.INVISIBLE : View.VISIBLE);

        buttonDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
                builder.setTitle("일정 삭제");
                builder.setMessage("정말 삭제하시겠습니까?");
                builder.setNegativeButton("아니요", null);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(Constants.RC_DELETE, intent);
                        finish();
                    }
                });
                builder.create().show();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_TITLE, editTextTitle.getText().toString().trim());
                intent.putExtra(Constants.KEY_START_TIME, editTextStart.getText().toString().trim());
                intent.putExtra(Constants.KEY_END_TIME, editTextEnd.getText().toString().trim());
                setResult(Constants.RC_SUCCESS, intent);
                finish();
            }
        });
    }

    @Override
    public void onTextChanged(CharSequence string, int start, int before, int count) {
        updateUi();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    private void updateUi() {
        boolean enabled = !editTextTitle.getText().toString().trim().isEmpty() && !editTextStart.getText().toString().trim().isEmpty() && !editTextEnd.getText().toString().trim().isEmpty();
        buttonConfirm.setEnabled(enabled);
        buttonConfirm.setTextColor(enabled ? Color.rgb(0xFF, 0x1B, 0x19) : Color.rgb(0xB1, 0xAB, 0xAB));
    }
}
