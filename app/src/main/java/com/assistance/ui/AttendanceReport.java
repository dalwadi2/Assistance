package com.assistance.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.assistance.R;
import com.assistance.adapters.AttendanceAdapter;
import com.assistance.db.Attendance;
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

public class AttendanceReport extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_from_date)
    TextView tvFromDate;
    @BindView(R.id.tv_to_date)
    TextView tvToDate;
    @BindView(R.id.tv_select_person_ar)
    TextView tvSelectPersonAr;
    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;
    @BindView(R.id.pb_att)
    ProgressBar pbAtt;
    @BindView(R.id.rv_att)
    RecyclerView rvAtt;
    private DatePickerDialog dpdFrm;
    private DatePickerDialog dpdTO;
    private Realm realm;
    private RealmResults<Attendance> personlist;
    private Set<String> finallist;
    private boolean nameSelected = false;
    private String selectedName;
    private Calendar now;
    private boolean toDate = false;
    private boolean fromDate = false;
    private RealmResults<Attendance> records;
    private AttendanceAdapter attaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        now = Calendar.getInstance();
        realm = Realm.getDefaultInstance();

        personlist = realm.where(Attendance.class).findAllSorted("personName", Sort.ASCENDING);

        dpdFrm = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        fromDate = true;
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        tvFromDate.setText(date);
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
                        tvToDate.setText(date);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpdTO.setVersion(DatePickerDialog.Version.VERSION_2);

        finallist = new ArraySet<>();
        for (Attendance person : personlist) {
            finallist.add(person.getPersonName());
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAtt.setLayoutManager(layoutManager);
        rvAtt.setHasFixedSize(true);
    }

    @OnClick({R.id.tv_from_date, R.id.tv_to_date, R.id.tv_select_person_ar, R.id.fab_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_from_date:
                dpdFrm.show(getFragmentManager(), "Datepickerdialog_frm");
                break;
            case R.id.tv_to_date:
                dpdTO.show(getFragmentManager(), "Datepickerdialog_to");
                break;
            case R.id.tv_select_person_ar:
                listPerson();
                break;
            case R.id.fab_search:
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
        pbAtt.setVisibility(View.VISIBLE);

        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date frmdate = formatter.parse(tvFromDate.getText().toString());
            Date todate = formatter.parse(tvToDate.getText().toString());

            records = realm.where(Attendance.class)
                    .equalTo("personName", selectedName)
                    .between("timestamp", frmdate.getTime() / 1000L, todate.getTime() / 1000L)
                    .findAllSorted("timestamp", Sort.ASCENDING);

            if (records.size() > 0) {
                attaAdapter = new AttendanceAdapter(AttendanceReport.this, records);
                rvAtt.setAdapter(attaAdapter);
                rvAtt.setVisibility(View.VISIBLE);

                Toast.makeText(this, records.size() + " Record Fetch Successfully", Toast.LENGTH_SHORT).show();
            } else {
                rvAtt.setVisibility(View.GONE);
                Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error in Saving Data " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pbAtt.setVisibility(View.GONE);

    }

    private void listPerson() {
        if (finallist.size() > 0) {
            new MaterialDialog.Builder(this)
                    .title("Select Person")
                    .items(finallist)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            tvSelectPersonAr.setText(text);
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
