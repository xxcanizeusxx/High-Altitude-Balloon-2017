package com.example.egczb.highaltitudeflightcontroller.UI;

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
 * Dialog used to notify of errors. Extends DialogFragment and has only
 * one method to call. The buildError takes the context of the activity,
 * the title, description, and action of the alert dialog. This dialog is only used
 * to provide error information and does not provide any actions other than dismiss.
 */

public class ErrorDialogFragment extends DialogFragment {
    //Method to call to build our alert dialog
    public static void buildError(final Context context, final String alertTittle, final String alertDescription, final String actionButton){
        //Get our layout inflater from the context of our activity
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //Create a view and attach it to the inflater with custom dialog.
        View errorView = layoutInflater.inflate(R.layout.error_dialog, null);
        //Build the alertDialog
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        //Initialize the Ui components
        TextView titleAlert = (TextView)errorView.findViewById(R.id.alert_title);
        TextView descAlert = (TextView)errorView.findViewById(R.id.alert_description);
        Button action = (Button)errorView.findViewById(R.id.alert_button);

        //Set the title and description of the dialog
        titleAlert.setText(alertTittle);
        descAlert.setText(alertDescription);
        //Set the action button to dismiss the dialog
        action.setText(actionButton);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(errorView);
        alertDialog.show();

    }
}
