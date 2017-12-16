package com.assistance.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.assistance.R;
import com.assistance.adapters.SalaryAdapter;
import com.assistance.db.Salary;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SalaryReport extends AppCompatActivity {
    private static final String TAG = "SalaryReport";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_from_date_s)
    TextView tvFromDateS;
    @BindView(R.id.tv_to_date_s)
    TextView tvToDateS;
    @BindView(R.id.tv_select_person_s)
    TextView tvSelectPersonS;
    @BindView(R.id.tv_total_salary)
    TextView tvTotalSalary;
    @BindView(R.id.fab_search_s)
    FloatingActionButton fabSearchS;
    @BindView(R.id.pb_salary)
    ProgressBar pbSalary;
    @BindView(R.id.rv_salary)
    RecyclerView rvSalary;
    private DatePickerDialog dpdFrm;
    private DatePickerDialog dpdTO;
    private Realm realm;
    private RealmResults<Salary> personlist;
    private Set<String> finallist;
    private boolean nameSelected = false;
    private String selectedName;
    private Calendar now;
    private boolean toDate = false;
    private boolean fromDate = false;
    private RealmResults<Salary> records;
    private SalaryAdapter salaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_report);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        now = Calendar.getInstance();
        realm = Realm.getDefaultInstance();

        personlist = realm.where(Salary.class).findAllSorted("personName", Sort.ASCENDING);

        dpdFrm = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        fromDate = true;
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        tvFromDateS.setText(date);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpdFrm.setVersion(DatePickerDialog.Version.VERSION_2);
        dpdTO = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        toDate = true;
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        tvToDateS.setText(date);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpdTO.setVersion(DatePickerDialog.Version.VERSION_2);

        finallist = new ArraySet<>();
        for (Salary person : personlist) {
            finallist.add(person.getPersonName());
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSalary.setLayoutManager(layoutManager);
        rvSalary.setHasFixedSize(true);
    }

    @OnClick({R.id.tv_from_date_s, R.id.tv_to_date_s, R.id.tv_select_person_s, R.id.fab_search_s})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_from_date_s:
                dpdFrm.show(getFragmentManager(), "Datepickerdialog_frm");
                break;
            case R.id.tv_to_date_s:
                dpdTO.show(getFragmentManager(), "Datepickerdialog_to");
                break;
            case R.id.tv_select_person_s:
                listPerson();
                break;
            case R.id.fab_search_s:
                processFetch();
                break;
        }
    }

    private void processFetch() {
        if (!fromDate) {
            Toast.makeText(this, "Select From Date to fetch the record", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!toDate) {
            Toast.makeText(this, "Select To Date to fetch the record", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nameSelected) {
            Toast.makeText(this, "Select Person Name to Continue.", Toast.LENGTH_SHORT).show();
            return;
        }
        pbSalary.setVisibility(View.VISIBLE);
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date frmdate = formatter.parse(tvFromDateS.getText().toString());
            Date todate = formatter.parse(tvToDateS.getText().toString());

            Log.w(TAG, "selectedName: " + selectedName + " frmdate " + frmdate.getTime() / 1000L + " todate " + todate.getTime() / 1000L);

            records = realm.where(Salary.class)
                    .equalTo("personName", selectedName)
                    .between("timestamp", frmdate.getTime() / 1000L, todate.getTime() / 1000L)
                    .findAllSorted("timestamp", Sort.ASCENDING);


            if (records.size() > 0) {
                int totalSalary = 0;
                for (Salary record : records) {
                    totalSalary += record.getSalaryAmount();
                }
                tvTotalSalary.setText("Total Salary: \u20B9 " + records.sum("salaryAmount").toString());
                salaryAdapter = new SalaryAdapter(SalaryReport.this, records);
                rvSalary.setAdapter(salaryAdapter);
                rvSalary.setVisibility(View.VISIBLE);
                Toast.makeText(this, records.size() + " Record Fetch Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
                rvSalary.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error in Saving Data " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        pbSalary.setVisibility(View.GONE);


    }

    private void listPerson() {
        if (finallist.size() > 0) {
            new MaterialDialog.Builder(this)
                    .title("Select Person")
                    .items(finallist)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            tvSelectPersonS.setText(text);
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
