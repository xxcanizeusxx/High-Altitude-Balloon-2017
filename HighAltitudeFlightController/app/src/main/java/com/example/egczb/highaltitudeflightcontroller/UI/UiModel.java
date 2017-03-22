package com.example.egczb.highaltitudeflightcontroller.UI;

import com.example.egczb.highaltitudeflightcontroller.R;

/**
 * Created by EGCZB on 3/18/2017.
 * This is the model used to house the UI components
 * such as menu titles, descriptions and images.
 * This class uses setters and getters to set and get
 * the UI components.
 */

public class UiModel {

    private String uiMenuTitle;
    private String uiMenuDesc;
    private int uiMenuBackImg;
    private int uiMenuIcon;


    public UiModel(String uiMenuTitle, String uiMenuDesc, int uiMenuBackImg, int uiMenuIcon){
        //Assign the same variables in constructor
        this.uiMenuTitle = uiMenuTitle;
        this.uiMenuDesc = uiMenuDesc;
        this.uiMenuBackImg = uiMenuBackImg;
        this.uiMenuIcon = uiMenuIcon;

    }

    //Setters and getters used to set and get our menu
    public String getUiMenuTitle(){
        return uiMenuTitle;
    }
    public void setUiMenuTitle(String uiMenuTitle){
        this.uiMenuTitle = uiMenuTitle;
    }
    public String getUiMenuDesc(){
        return uiMenuDesc;
    }
    public void setUiMenuDesc(String uiMenuDesc){
        this.uiMenuDesc = uiMenuDesc;
    }
    public int getUiMenuBackImg(){
        return uiMenuBackImg;
    }
    public void setUiMenuBackImg(int uiMenuBackImg){
        this.uiMenuBackImg = uiMenuBackImg;
    }
    public int getUiMenuIcon(){
        return uiMenuIcon;
    }
    public void setUiMenuIcon(int uiMenuIcon){
        this.uiMenuIcon = uiMenuIcon;
    }
}
