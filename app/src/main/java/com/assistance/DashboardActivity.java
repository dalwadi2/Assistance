package com.assistance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.assistance.db.Person;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick({R.id.ll_att_report, R.id.ll_salary_report, R.id.ll_add_att, R.id.ll_add_person, R.id.ll_add_salary, R.id.ll_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_att_report:
                break;
            case R.id.ll_salary_report:
                break;
            case R.id.ll_add_att:
                break;
            case R.id.ll_add_person:
                addPerson();
                break;
            case R.id.ll_add_salary:
                startActivity(AddSalaryActivity.class);
                break;
            case R.id.ll_delete:
                confirmDelete();
                break;
        }
    }

    private void startActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void confirmDelete() {
        new MaterialDialog.Builder(this)
                .title("Delete Everything?")
                .content("Your Data will not recover after Deletion. Are you sure want to Delete Everything?")
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .inputRangeRes(4, 4, R.color.material_red_500)
                .positiveText("Delete")
                .input("Password", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (!TextUtils.isEmpty(input.toString()) && input.toString().equals("2702")) {
                            realm.beginTransaction();
                            realm.deleteAll();
                            realm.commitTransaction();
                            Toast.makeText(DashboardActivity.this, "Deleted All Data Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DashboardActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private void addPerson() {
        new MaterialDialog.Builder(this)
                .title("Add Person")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText("Add")
                .input("Name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Log.e(TAG, "onInput: " + input);
                        if (!TextUtils.isEmpty(input.toString())) {
                            realm.beginTransaction();
                            String uniqueId = UUID.randomUUID().toString();
                            Person person = realm.createObject(Person.class, uniqueId);
                            person.setName(input.toString());
                            realm.commitTransaction();
                            Toast.makeText(DashboardActivity.this, input + " Added to list", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }
}
