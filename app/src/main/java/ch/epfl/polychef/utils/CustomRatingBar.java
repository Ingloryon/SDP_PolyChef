package ch.epfl.polychef.utils;

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

    public void drawBar(){

        ArrayList<ImageView> stars = getAllStarsImage();
        double roundedRate = Math.round(rate * 2) / 2;
        for(int i = 0; i < stars.size(); i++){
            if(roundedRate >= i + 1){
                stars.get(i).setImageResource(starFullSource);
            }else if(roundedRate <= i){
                stars.get(i).setImageResource(starEmptySource);
            }else{
                stars.get(i).setImageResource(starHalfSource);
            }
        }
    }

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

    private ArrayList<ImageView> getAllStarsImage(){
        ArrayList<ImageView> stars = new ArrayList<>();
        stars.add(ratingView.findViewById(R.id.star0));
        stars.add(ratingView.findViewById(R.id.star1));
        stars.add(ratingView.findViewById(R.id.star2));
        stars.add(ratingView.findViewById(R.id.star3));
        stars.add(ratingView.findViewById(R.id.star4));
        return stars;
    }
}
