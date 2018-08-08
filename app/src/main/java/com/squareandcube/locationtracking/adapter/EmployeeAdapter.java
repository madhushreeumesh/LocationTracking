package com.squareandcube.locationtracking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareandcube.locationtracking.activity.AssignTaskMapsActivity;
import com.squareandcube.locationtracking.activity.EditDeleteEmployeeActivity;
import com.squareandcube.locationtracking.activity.EmployeeListActivity;
import com.squareandcube.locationtracking.activity.TrackEmployeeActivity;
import com.squareandcube.locationtracking.model.Employee;
import com.squareandcube.locationtracking.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> implements Filterable {


    private AppCompatActivity activity;
    private List<Employee> employeeList;
    private List<Employee> filteredEmployeeList;
    public Context context;
    public String managerid;
    public String managerMail;

    public EmployeeAdapter(AppCompatActivity activity, Context context, String[] id, String[] password, String[] mobile, String managerid, String managerMail) {

        super();
        this.activity = activity;
        this.context = context;
        this.managerid = managerid;
        this.managerMail = managerMail;
        this.employeeList = new ArrayList<Employee>();
        this.filteredEmployeeList = new ArrayList<Employee>();

        for (int i = 0; i < id.length; i++) {
            Employee emp = new Employee();
            emp.setEmpId(id[i]);
            emp.setEmpPassword(password[i]);
            emp.setEmpMobile(mobile[i]);
            employeeList.add(emp);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Employee Employee = filteredEmployeeList.get(position);
        holder.empId.setText(Employee.getEmpId());
        holder.empPassword.setText(Employee.getEmpPassword());
        holder.empMobile.setText(Employee.getEmpMobile());

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.assign_track_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.assign_task_menu:

                                boolean connect = false;
                                ConnectivityManager taskConnectivityManagaer = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                if (taskConnectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                        taskConnectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                    if (checkPermission()) {
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        sharedPreferences.edit().putString("empId", holder.empId.getText().toString().trim()).apply();
                                        sharedPreferences.edit().putString("managerId", managerid).apply();
                                        sharedPreferences.edit().putString("managerMail", managerMail).apply();
                                        Intent mAssignTaskIntent = new Intent(context, AssignTaskMapsActivity.class);
                                        mAssignTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(mAssignTaskIntent);
                                    }
                                    else{
                                        permissionAlertDailog();
                                    }
                                } else {
                                    networkConnectMessage();
                                }
                                return true;

                            case R.id.track_menu:
                                boolean connects = false;
                                ConnectivityManager connectivityManagaer = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                if (connectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                        connectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                    if(checkPermission()) {
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        sharedPreferences.edit().putString("empId", holder.empId.getText().toString().trim()).apply();
                                        sharedPreferences.edit().putString("managerId", managerid).apply();
                                        sharedPreferences.edit().putString("managerMail", managerMail).apply();

                                        Intent mTrackIntent = new Intent(context, TrackEmployeeActivity.class);
                                        mTrackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(mTrackIntent);
                                    }
                                    else{
                                        permissionAlertDailog();
                                    }
                                } else {
                                    networkConnectMessage();
                                }
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connects = false;
                ConnectivityManager connectivityManagaer = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    Intent mUserIntent = new Intent(v.getContext(), EditDeleteEmployeeActivity.class);
                    v.getContext().startActivity(mUserIntent);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    sharedPreferences.edit().putString("Id", holder.empId.getText().toString().trim()).apply();
                    sharedPreferences.edit().putString("Password", holder.empPassword.getText().toString().trim()).apply();
                    sharedPreferences.edit().putString("Mobile", holder.empMobile.getText().toString().trim()).apply();
                    sharedPreferences.edit().putString("ManagerId", managerid).apply();
                } else {
                    networkConnectMessage();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredEmployeeList.size();
    }

    public void networkConnectMessage() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);
        builder.setTitle("No Network");
        builder.setMessage("Please Check the Internet Connection and Try again");
        //builder.getWindow().setLayout(600, 400);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        (activity).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                    }
                });
        builder.show();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString=charSequence.toString();
                if(charString.isEmpty())
                {
                    filteredEmployeeList = employeeList;
                }
                else
                {
                    List<Employee> filteredList=new ArrayList<>();
                    for(Employee employee: employeeList)
                    {
                        if(employee.getEmpId().toLowerCase().contains(charString.toLowerCase()) || employee.getEmpPassword().contains(charSequence) )
                        {
                            filteredList.add(employee);
                        }

                    }
                    filteredEmployeeList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredEmployeeList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredEmployeeList = (ArrayList<Employee>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView empId, empMobile, empPassword;
        public CardView mCardView;
        public TextView contact;

        public MyViewHolder(View view) {
            super(view);
            empId = (TextView) view.findViewById(R.id.empId);
            empMobile = (TextView) view.findViewById(R.id.empmobile);
            empPassword = (TextView) view.findViewById(R.id.empPassword);
            mCardView = (CardView) view.findViewById(R.id.user_cardView);
            contact = (TextView) view.findViewById(R.id.textViewOptions);

        }

    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void permissionAlertDailog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permissons Need");
        builder.setMessage("Need Location Permission for this app");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
//                        ((activity)).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        ((activity)).startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName())), 0);
                    }
                });
        builder.show();
    }

}
