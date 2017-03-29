package com.example.egczb.highaltitudeflightcontroller.UI;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.egczb.highaltitudeflightcontroller.Bluetooth.BluetoothScanActivity;
import com.example.egczb.highaltitudeflightcontroller.Documentation.DocumentationActivity;
import com.example.egczb.highaltitudeflightcontroller.FlightAnalytics.FlightAnalyticsActivity;
import com.example.egczb.highaltitudeflightcontroller.R;
import com.example.egczb.highaltitudeflightcontroller.SD.SDActivity;
import com.example.egczb.highaltitudeflightcontroller.ToDo.ToDoActivity;

import java.util.List;

/**
 * Created by EGCZB on 3/21/2017.
 * This is an adapter that takes the UiModel and adapts it
 * to a RecyclerView. We are using listview designed to show
 * everything in a unordered list.
 */

public class UiAdapter extends RecyclerView.Adapter<UiAdapter.UiViewHolder>{

    //The activitie's context
    private Context context;
    //Reference to our model setter and getter @<code>UiModel</code>
    private List<UiModel> uiModelList;

    //The UiViewHolder class that will extend RecyclerView for a list like view
    public class UiViewHolder extends RecyclerView.ViewHolder{
        //The Ui Views
        public TextView uiTitle;
        public TextView uiDesc;
        public ImageView uiBackgImg;
        public ImageView uiMenuIcon;

        //The holder that holds our Ui components
        public UiViewHolder(View view){
            super(view);
            //Initialize the Ui components a.k.a boiler plate code
            uiTitle = (TextView)view.findViewById(R.id.ui_menu_title);
            uiDesc = (TextView)view.findViewById(R.id.ui_menu_description);
            uiBackgImg = (ImageView)view.findViewById(R.id.ui_menu_background_image);
            uiMenuIcon = (ImageView) view.findViewById(R.id.ui_menu_icon);

            //Set the onClick to listen to the view clicks and initiate our activities
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    //Switch statement on adapter position
                    switch (getAdapterPosition()){
                        case 0:
                            Toast.makeText(context, "Opening Payload Connection Center", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, BluetoothScanActivity.class);
                            context.startActivity(intent);
                            break;
                        case 1:
                            Toast.makeText(context, "Opening Raw Flight File", Toast.LENGTH_SHORT).show();
                            intent = new Intent(context, SDActivity.class);
                            context.startActivity(intent);
                            break;
                        case 2:
                            Toast.makeText(context, "Opening Flight Analytics", Toast.LENGTH_SHORT).show();
                            intent = new Intent(context, FlightAnalyticsActivity.class);
                            context.startActivity(intent);
                            break;
                        case 3:
                            Toast.makeText(context, "Opening To-Do List", Toast.LENGTH_SHORT).show();
                            intent = new Intent(context, ToDoActivity.class);
                            context.startActivity(intent);
                            break;
                        case 4:
                            Toast.makeText(context, "Opening Project Documentation", Toast.LENGTH_SHORT).show();
                            intent = new Intent(context, DocumentationActivity.class);
                            context.startActivity(intent);
                            break;
                    }
                }

            });

        }
    }
    //Method to intialize the UiAdapter
    public UiAdapter(Context context, List<UiModel> uiModelList){
        this.context = context;
        this.uiModelList = uiModelList;
    }

    //System override method call. This call is made when the UiViewHolder is first created
    //This will attach the layout (ui_card_template) to our ViewHolder
    @Override
    public  UiViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //Attaches the layout to the parent
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_card_template, parent, false);

        //Returns the specific view
        return new UiViewHolder(itemView);
    }

    //This is the system call that is called when the ViewHolder is binded to the layout
    //This is where we can code some stuff
    @Override
    public void onBindViewHolder(final UiViewHolder uiHolder, int position){
        //Initialize the UiModel class
        UiModel model = uiModelList.get(position);
        //Do some code here: i.e set and the names of the Ui
        uiHolder.uiTitle.setText(model.getUiMenuTitle());
        uiHolder.uiDesc.setText(model.getUiMenuDesc());
        //Load the menu background image and icons with Glide
        Glide.with(context).load(model.getUiMenuBackImg()).into(uiHolder.uiBackgImg);
        Glide.with(context).load(model.getUiMenuIcon()).into(uiHolder.uiMenuIcon);

    }

    //System call that returns the size of the uiModelList
    @Override
    public int getItemCount() {
        return uiModelList.size();
    }
}
