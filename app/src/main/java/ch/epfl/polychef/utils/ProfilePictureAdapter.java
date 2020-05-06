package ch.epfl.polychef.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ProfilePicture;

/**
 * An adapter to display the different profile pictures in the main ListView.
 */
public class ProfilePictureAdapter  extends BaseAdapter {

    private List<ProfilePicture> listPictures;
    private LayoutInflater layoutInflater;
    private Context context;

    /**
     * The constructor of the adapter.
     * @param aContext the context
     * @param listPictures the list of profile pictures
     */
    public ProfilePictureAdapter(Context aContext,  List<ProfilePicture> listPictures) {
        this.context = aContext;
        //TODO: deep copy ?
        this.listPictures = listPictures;
        layoutInflater = LayoutInflater.from(aContext);
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

    /**
     * Returns the updated view with the profile picture choice.
     * @param position the position on the display
     * @param convertView the initial view
     * @param parent the viewGroup the activity belongs to
     * @return the updated view
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.profile_pict_item, null);
            holder = new ViewHolder();
            holder.flagView = (ImageView) convertView.findViewById(R.id.profile_picture_drawable);
            holder.pictureName = (TextView) convertView.findViewById(R.id.profile_picture_label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfilePicture current_picture = this.listPictures.get(position);
        holder.pictureName.setText(current_picture.getPictureLabel());

        int pictureId = context.getResources().getIdentifier(current_picture.getPicturePath(), "drawable", context.getPackageName());
        holder.flagView.setImageResource(pictureId);

        return convertView;
    }

    static class ViewHolder {
        ImageView flagView;
        TextView pictureName;
    }

}