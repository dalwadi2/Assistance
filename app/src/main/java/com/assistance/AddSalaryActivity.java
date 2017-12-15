package com.assistance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.assistance.db.Person;
import com.assistance.db.Salary;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddSalaryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_select_person)
    TextView tvSelectPerson;
    @BindView(R.id.et_person_manual)
    EditText etPersonManual;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.et_amount)
    EditText etAmount;
    private DatePickerDialog dpd;
    private Realm realm;
    private RealmResults<Person> personlist;
    private List<String> finallist;
    private boolean dateSelected = false;
    private boolean nameSelected = false;
    private String selectedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_salary);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();

        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                AddSalaryActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        personlist = realm.where(Person.class).findAllSorted("name", Sort.ASCENDING);

        finallist = new ArrayList<>();
        for (Person person : personlist) {
            finallist.add(person.getName());
        }
    }

    @OnClick({R.id.tv_select_person, R.id.tv_date, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_person:
                listPerson();
                break;
            case R.id.tv_date:
                dpd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.btn_save:
                processSave();
                break;
        }
    }

    private void processSave() {
        if (!dateSelected) {
            Toast.makeText(this, "Select Date to save the record", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nameSelected) {
            if (TextUtils.isEmpty(etPersonManual.getText().toString())) {
                Toast.makeText(this, "Select Person Name or Write the Name Manually to Continue.", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedName = etPersonManual.getText().toString();
        }
        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            Toast.makeText(this, "Enter Amount to Continue", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            realm.beginTransaction();
            String uniqueId = UUID.randomUUID().toString();
            Salary salary = realm.createObject(Salary.class, uniqueId);
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = formatter.parse(tvDate.getText().toString());
            salary.setPersonName(selectedName);
            salary.setTimestamp(date.getTime() / 1000L);
            salary.setSalaryAmount(Integer.parseInt(etAmount.getText().toString()));
            Toast.makeText(this, "Record Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error in Saving Data " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            realm.commitTransaction();
            finish();
        }


    }

    private void listPerson() {
        if (finallist.size() > 0) {
            new MaterialDialog.Builder(this)
                    .title("Select Person")
                    .items(finallist)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            tvSelectPerson.setText(text);
                            selectedName = text.toString();
                            nameSelected = true;
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "No Person Added in List", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        tvDate.setText(date);
        dateSelected = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
