package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.rafaelverginelli.innovationbookcase.R;
import models.ResourceModel;


public class BooksAdapter extends ArrayAdapter<ResourceModel> {

    Context context;
    int layoutResourceId;
    List<ResourceModel> data = new ArrayList<>();
    IBookSelectable callback = null;

    public List<ResourceModel> getData() {
        return data;
    }

    public void setData(List<ResourceModel> data) {
        this.data = data;
    }

    public interface IBookSelectable  {
        void OnBookSelected(ResourceModel book, Holder holder);
    }

    public BooksAdapter(Context context, int layoutResourceId, List<ResourceModel> data, IBookSelectable callback) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.callback = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();

            holder.bg = (RelativeLayout)row.findViewById(R.id.bg);
            holder.imgCover = (ImageView)row.findViewById(R.id.imgCover);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtSubtitle = (TextView)row.findViewById(R.id.txtSubtitle);
            holder.txtAuthors = (TextView)row.findViewById(R.id.txtAuthors);
            holder.txtCategories = (TextView)row.findViewById(R.id.txtCategories);

            row.setTag(holder);
        }
        else {
            holder = (Holder)row.getTag();
        }

        final ResourceModel item = data.get(position);

        final Holder finalHolder = holder;


        if(callback != null){
            holder.bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnBookSelected(item, finalHolder);
                }
            });
        }

        if(!item.getThumbnail().isEmpty()){
            Picasso.with(context).
                    load(item.getThumbnail()).
                    error(R.drawable.ic_book).
                    into(holder.imgCover);
        }

        holder.txtTitle.setVisibility(item.getTitle().isEmpty() ? View.GONE : View.VISIBLE);
        holder.txtTitle.setText(item.getTitle());
        holder.txtSubtitle.setVisibility(item.getSubtitle().isEmpty() ? View.GONE : View.VISIBLE);
        holder.txtSubtitle.setText(item.getSubtitle());

        StringBuilder authors = new StringBuilder();
        int authorsSize = item.getAuthors().size();
        for(int i = 0; i < authorsSize; i++){
            authors.append(item.getAuthors().get(i));
            if(i < (authorsSize - 1)) {
                authors.append(context.getString(R.string.author_separator));
            }
        }
        holder.txtAuthors.setVisibility(authors.toString().isEmpty() ? View.GONE : View.VISIBLE);
        holder.txtAuthors.setText(String.format(context.getString(R.string.by_x), authors));

        StringBuilder categories = new StringBuilder();
        int categoriesSize = item.getCategories().size();
        for(int i = 0; i < categoriesSize; i++){
            categories.append(item.getCategories().get(i));
            if(i < (categoriesSize - 1)) {
                categories.append(context.getString(R.string.author_separator));
            }
        }
        holder.txtCategories.setVisibility(categories.toString().isEmpty() ? View.GONE : View.VISIBLE);
        holder.txtCategories.setText(categories);

        return row;
    }

    public static class Holder {
        public RelativeLayout bg;
        public ImageView imgCover;
        public TextView txtTitle;
        public TextView txtSubtitle;
        public TextView txtAuthors;
        public TextView txtCategories;
    }

}