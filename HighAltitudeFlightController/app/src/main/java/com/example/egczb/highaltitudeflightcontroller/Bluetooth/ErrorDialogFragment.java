package com.example.egczb.highaltitudeflightcontroller.Bluetooth;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.egczb.highaltitudeflightcontroller.R;

/**
 * Created by EGCZB on 3/25/2017.
 * Dialog used to notify of errors
 */

public class ErrorDialogFragment extends DialogFragment {
   public static void errorDialogFragment(final Context context, final String alertTittle, final String alertDescription, final String actionButton){
       LayoutInflater layoutInflater = LayoutInflater.from(context);
       View errorView = layoutInflater.inflate(R.layout.error_dialog, null);
       final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

       //Initialize the Ui components
       TextView titleAlert = (TextView)errorView.findViewById(R.id.alert_title);
       TextView descAlert = (TextView)errorView.findViewById(R.id.alert_description);
       Button action = (Button)errorView.findViewById(R.id.alert_button);

       titleAlert.setText(alertTittle);
       descAlert.setText(alertDescription);

       action.setText(actionButton);
       action.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               return;
           }
       });

       alertDialog.setView(errorView);
       alertDialog.show();

   }
}
