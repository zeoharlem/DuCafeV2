package com.orojinmi.theophilus.ducafev2.Utils;

public class NavItems {
    private String mTitle, mSubtitle;
    private int mIcon;

    public NavItems(String mTitle, String mSubtitle, int mIcon) {
        this.mTitle     = mTitle;
        this.mSubtitle  = mSubtitle;
        this.mIcon      = mIcon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmSubtitle() {
        return mSubtitle;
    }

    public void setmSubtitle(String mSubtitle) {
        this.mSubtitle = mSubtitle;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon(int mIcon) {
        this.mIcon = mIcon;
    }
}
