package com.example.egczb.highaltitudeflightcontroller;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.egczb.highaltitudeflightcontroller.UI.UiAdapter;
import com.example.egczb.highaltitudeflightcontroller.UI.UiModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Ui components
    private RecyclerView recyclerView;
    private UiAdapter adapter;
    private List<UiModel> uiModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Method to set up the collapsing toolbar
        setUpCollapsingToolbar();

        //Initialize Ui components
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        uiModelList = new ArrayList<>();
        adapter = new UiAdapter(this, uiModelList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Method to pupulate the UiMenu model
        popUiMenu();

        //Load the toolbar background image
        try{
            Glide.with(this).load(R.drawable.burst).into((ImageView)findViewById(R.id.toolbar_image));
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void popUiMenu() {
        //The Ui menu titles
         String[] uiTitles = {
                "Payload Connection Center",
                "Raw Data File",
                "Flight Analytics",
                "Launch To-Do List",
                "Project Documentation"
        };

        //The Ui menu descriptions
         String[] uiDescriptions = {
                "Connect to the Arduino via Bluetooth and check the status of the payload. " +
                        "Extract on-board flight data.",
                "View the raw flight data as a txt file. This confirms the copy " +
                        "of the flight data saved to the device.",
                "Access the flight data and instantly analyze the recorded flight in an " +
                        "interactive way",
                "View the list to add or remove launch-readiness tasks that will " +
                        "ensure a successful launch.",
                "View a small summary of the project documentation. Explore categories by career fields."
        };

        //The Ui menu image background
         int uiImages[] = {
                 R.drawable.blebkg,
                 R.drawable.sdbkg,
                 R.drawable.graphbkg,
                 R.drawable.todobkg,
                 R.drawable.docbkg
        };

        //The Ui menu icons
        int uiIcons[] = {
                R.drawable.ble,
                R.drawable.sd,
                R.drawable.graph,
                R.drawable.todo,
                R.drawable.doc
        };

        //Create a new model based on the above
        for (int i = 0; i < uiTitles.length; i++){
           UiModel uiModel = new UiModel(uiTitles[i], uiDescriptions[i], uiImages[i], uiIcons[i]);
            uiModelList.add(uiModel);
            adapter.notifyDataSetChanged();
        }

    }


    //Method to set the collapsing toolbar
    private void setUpCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

    }
}
