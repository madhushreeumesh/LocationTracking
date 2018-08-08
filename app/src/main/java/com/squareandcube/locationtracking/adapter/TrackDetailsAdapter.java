package com.squareandcube.locationtracking.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareandcube.locationtracking.LatlongPreferences;
import com.squareandcube.locationtracking.R;
import com.squareandcube.locationtracking.activity.MapsActivity;
import com.squareandcube.locationtracking.model.Employee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TrackDetailsAdapter extends RecyclerView.Adapter<TrackDetailsAdapter.UserViewHolder> {
    public AppCompatActivity activity;


    public List<Employee> listUsers;
    public Context context;
    private String managerId;
    private String managerMail;
    private LatLng addressLatLong;
    private String latlongString;
    private String lat;
    private String longvalue;

    private LatLng empAddressLatLong;
    private String emplatlongString;
    private String emplat;
    private String emplongvalue;
    private String empid;

    Employee data;

    public TrackDetailsAdapter(AppCompatActivity activity, Context context, String[] taskno, String[]  desLat, String[] desLong, String[] desAddress, String[]  curLat, String[] curLong, String[] curAddress, String managerId, String managerMail, String empid)
    {
        super();
        this.context = context;
        this.activity = activity;
        this.managerId = managerId;
        this.managerMail = managerMail;
        this.empid = empid;

        listUsers = new ArrayList<Employee>();

        for(int i = 0; i < taskno.length; i++){
            Employee emp = new Employee();
            // emp.setTaskno(id[i]);

            emp.setTaskno(taskno[i]);
            emp.setDeslat(desLat[i]);
            emp.setDeslong(desLong[i]);
            emp.setEmpaddress(curAddress[i]);
            emp.setDesaddress(desAddress[i]);
            emp.setCurlat(curLat[i]);
            emp.setCurlat(curLong[i]);
            listUsers.add(emp);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_details, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int position) {
        Log.d(TAG, "position: " + position);
        data = listUsers.get(position);
        holder.textViewtakno.setText(data.getTaskno());
        holder.textviewempaddress.setText(data.getEmpaddress());
        holder.textViewaddress.setText(data.getDesaddress());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ConnectivityManager connectivityManagaer = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    if (connectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManagaer.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                        //Snackbar.make(view, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                        String address = holder.textViewaddress.getText().toString().trim();
                        addressLatLong = getLocationFromAddress(address);
                        latlongString = addressLatLong.toString().trim();
                        String lat_long_list[] = latlongString.split(",");
                        lat = lat_long_list[0];
                        lat = lat.replace("(", "").trim();
                        lat = lat.replace("lat/lng:", "").trim();
                        longvalue = lat_long_list[1];
                        longvalue = longvalue.replace(")", "").trim();

                        String empaddress = holder.textviewempaddress.getText().toString().trim();

                        if (empaddress.isEmpty()) {
                            Toast.makeText(activity, "Employee Didn't started the Task", Toast.LENGTH_SHORT).show();
                        } else {
                            empAddressLatLong = getLocationFromAddress(empaddress);
                            emplatlongString = empAddressLatLong.toString().trim();
                            String emp_lat_long_list[] = emplatlongString.split(",");
                            emplat = emp_lat_long_list[0];
                            emplat = emplat.replace("(", "").trim();
                            emplat = emplat.replace("lat/lng:", "").trim();
                            emplongvalue = emp_lat_long_list[1];
                            emplongvalue = emplongvalue.replace(")", "").trim();

                            SharedPreferences sharedTaskId = PreferenceManager.getDefaultSharedPreferences(context);
                            sharedTaskId.edit().putString("EmployeeId", empid).apply();
                            sharedTaskId.edit().putString("EmployeeTask", holder.textViewtakno.getText().toString().trim()).apply();
                            sharedTaskId.edit().putString("daddress", holder.textViewaddress.getText().toString().trim()).apply();
                            sharedTaskId.edit().putString("dLat", lat).apply();
                            sharedTaskId.edit().putString("dLong", longvalue).apply();

                            Intent intent = new Intent(activity, MapsActivity.class);
                            (activity).startActivity(intent);
                        }
                    } else {
                        networkConnectMessage();
                    }
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.v(TrackDetailsAdapter.class.getSimpleName(),""+listUsers.size());
        return listUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewtakno;
        public TextView textViewaddress;
        public TextView textviewempaddress;
        public CardView cardView;

        public UserViewHolder(View view) {
            super(view);
            textViewtakno = (TextView) view.findViewById(R.id.taskno);
            textViewaddress = (TextView) view.findViewById(R.id.address);
            textviewempaddress=(TextView)view.findViewById(R.id.empaddress);
            cardView = (CardView) view.findViewById(R.id.cardview);

        }
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

    public LatLng getLocationFromAddress( String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
