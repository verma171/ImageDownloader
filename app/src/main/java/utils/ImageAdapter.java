package utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sample.droidrank.com.droidrank.R;

/**
 * Created by ravi on 29-Mar-17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final int PAZE_SIZE = 20;
    List<ImageInfo> mList;
    Context mcontext;
    public ImageAdapter(List list,Context context)
    {
        mList = list;
        mcontext = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
         AsyncImageLoader.getInstance(mcontext).DisplayImage(mList.get(position).getImageUrl(),holder.imageView);
         holder.textView.setText(mList.get(position).getImageDescription());

    }

    @Override
    public int getItemCount() {
        if(mList != null)
            return mList.size();
        else
           return 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView ;
        TextView textView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView)itemView.findViewById(R.id.textview);
        }
    }

    public void setMjsonArray(int pageCount,List<ImageInfo> list) {
        mList.addAll(list);
        notifyItemRangeInserted(((pageCount-1)*PAZE_SIZE)+1,PAZE_SIZE);
    }
}
