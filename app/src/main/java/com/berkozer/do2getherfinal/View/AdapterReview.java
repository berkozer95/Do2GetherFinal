package com.berkozer.do2getherfinal.View;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.berkozer.do2getherfinal.R;
import com.berkozer.do2getherfinal.Models.Review;

import java.util.List;

public class AdapterReview extends ArrayAdapter<Review> {

    private Activity activity;
    private List<Review> lReview;
    private static LayoutInflater inflater = null;

    public AdapterReview(Activity activity, int textViewResourceId, List<Review> _lReview) {
        super(activity, textViewResourceId, _lReview);
        try {
            this.activity = activity;
            this.lReview = _lReview;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return lReview.size();
    }

    public Review getItem(Review position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_text;
        //public TextView display_rating;
        public TextView display_author;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        try {
            if (convertView == null) {

                vi = inflater.inflate(R.layout.review, null);

                vi.setVerticalScrollBarEnabled(true);
                holder = new ViewHolder();



                holder.display_author = (TextView) vi.findViewById(R.id.display_author);
                holder.display_text = (TextView) vi.findViewById(R.id.display_text);
                //holder.display_rating = (TextView) vi.findViewById(R.id.display_rating);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }


            holder.display_author.setText(lReview.get(position).getDateInfo() + " and user rating is: " + lReview.get(position).getRating());
            if (lReview.get(position).getText() != null) {
                holder.display_text.setText(lReview.get(position).getText());
            }else{
                holder.display_text.setText("No review left by user");
            }
            //holder.display_rating.setText(lReview.get(position).getRating());


        } catch (Exception e) {


        }
        return vi;
    }
}
