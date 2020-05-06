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
 *
 */
public class ProfilePictureAdapter  extends BaseAdapter {

    private List<ProfilePicture> listPictures;
    private LayoutInflater layoutInflater;
    private Context context;

    /**
     *
     * @param aContext
     * @param listPictures
     */
    public ProfilePictureAdapter(Context aContext,  List<ProfilePicture> listPictures) {
        this.context = aContext;
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
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
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
        holder.pictureName.setText(current_picture.getPictureName());

        int pictureId = context.getResources().getIdentifier(current_picture.getPicturePath(), "drawable", context.getPackageName());
        holder.flagView.setImageResource(pictureId);

        return convertView;
    }

    static class ViewHolder {
        ImageView flagView;
        TextView pictureName;
    }

}