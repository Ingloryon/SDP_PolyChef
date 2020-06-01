package ch.epfl.polychef.utils;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.R;

public class CustomRatingBar {

    private int starFullSource;
    private int starHalfSource;
    private int starEmptySource;
    private View ratingView;
    private double rate = 0.0;
    private boolean clickable;


    /**
     * Create a new customRatingBar.
     * @param ratingView the view where the rating bar will be displayed
     * @param starFullSource the image source of a star that is full
     * @param starHalfSource the image source of a star that is half full
     * @param starEmptySource the image source of a star that is empty
     * @param clickable set the customRatingBar to clickable or not
     */
    public CustomRatingBar(View ratingView, int starFullSource, int starHalfSource, int starEmptySource, boolean clickable){
        this.ratingView = ratingView;
        this.starFullSource = starFullSource;
        this.starHalfSource = starHalfSource;
        this.starEmptySource = starEmptySource;
        this.clickable = clickable;
        if(clickable){
            makeImageClickable();
        }
    }

    /**
     * Make all the images clickable and change the rate according to where we have clicked.
     */
    private void makeImageClickable() {
        ArrayList<ImageView> stars = getAllStarsImage();
        for(int i = 0; i < stars.size(); i++){
            final int starIndex = i;
            stars.get(i).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    setRate((double) starIndex + 1);
                }
            });
        }
    }

    /**
     * Draw the bar in the layout depending on the current rate.
     */
    public void drawBar(){
        ArrayList<ImageView> stars = getAllStarsImage();
        double roundedRate = ((double)Math.round(rate * 2)) / 2;
        for(int i = 0; i < stars.size(); i++){
            if(roundedRate >= i + 1){
                stars.get(i).setImageResource(starFullSource);
                stars.get(i).setTag(starFullSource);
            }else if(roundedRate <= i){
                stars.get(i).setImageResource(starEmptySource);
                stars.get(i).setTag(starEmptySource);
            }else{
                stars.get(i).setImageResource(starHalfSource);
                stars.get(i).setTag(starHalfSource);
            }
        }
    }

    /**
     * Set a new rate and draw the layout accordingly.
     * @param rate the rate to change to
     */
    public void setRate(double rate){
        if(rate > 5){
            this.rate = 5;
        }else if(rate < 0){
            this.rate = 0;
        }else{
            this.rate = rate;
        }
        drawBar();
    }

    /**
     * Get all the ImageView of the rating bar.
     * @return the image views
     */
    public ArrayList<ImageView> getAllStarsImage(){
        ArrayList<ImageView> stars = new ArrayList<>();
        stars.add(ratingView.findViewById(R.id.star0));
        stars.add(ratingView.findViewById(R.id.star1));
        stars.add(ratingView.findViewById(R.id.star2));
        stars.add(ratingView.findViewById(R.id.star3));
        stars.add(ratingView.findViewById(R.id.star4));
        return stars;
    }

    /**
     * Get the current rate of the rating bar.
     * @return the rate
     */
    public double getRate(){
        return rate;
    }

    /**
     * Get the full image resource of the rating bar.
     * @return the id
     */
    public int getFullImageResource(){
        return starFullSource;
    }

    /**
     * Get the half image resource of the rating bar.
     * @return the id
     */
    public int getHalfImageResource(){
        return starHalfSource;
    }

    /**
     * Get the empty image resource of the rating bar.
     * @return the id
     */
    public int getEmptyImageResource(){
        return starEmptySource;
    }
}
