package ch.epfl.polychef.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ProfilePicture;

/**
 * An adapter to display the different profile pictures in the main ListView.
 */
public class ProfilePictureAdapter extends BaseAdapter {

    private List<ProfilePicture> listPictures;
    private LayoutInflater layoutInflater;
    private Context context;

    /**
     * The constructor of the adapter.
     * @param context the context
     * @param listPictures the list of profile pictures
     */
    public ProfilePictureAdapter(Context context,  List<ProfilePicture> listPictures) {
        this.context = context;

        // we perform a deep copy of the argument
        this.listPictures = new ArrayList<>();
        for(int i = 0 ; i < listPictures.size() ; ++i){
            ProfilePicture pp = listPictures.get(i);
            this.listPictures.add(new ProfilePicture(pp.getPictureLabel(), pp.getPicturePath()));
        }

        layoutInflater = LayoutInflater.from(context);
    }


    /**
     * Returns the updated view with the profile picture choice.
     * @param position the position on the display
     * @param convertView the initial view
     * @param parent the viewGroup the activity belongs to
     * @return the updated view
     */
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.profile_pict_item, null);
            holder = new ViewHolder();
            holder.flagView = convertView.findViewById(R.id.profilePicture);
            holder.pictureName = convertView.findViewById(R.id.profile_picture_label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfilePicture currentPicture = this.listPictures.get(position);
        holder.pictureName.setText(currentPicture.getPictureLabel());

        int pictureId = context.getResources().getIdentifier(currentPicture.getPicturePath(), "drawable", context.getPackageName());
        holder.flagView.setImageResource(pictureId);

        return convertView;
    }

    @Override
    public int getCount() {
        return listPictures.size();
    }

    @Override
    public Object getItem(int position) {
        return listPictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView flagView;
        TextView pictureName;
    }

}