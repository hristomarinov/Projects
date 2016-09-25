package marinov.hristo.taskone.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import marinov.hristo.taskone.R;

/**
 * @author HristoMarinov (christo_marinov@abv.bg).
 */
public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.ViewHolder> {

    public IRecycleViewSelectedItem mListener;
    private ArrayList<Song> mAdapterData;

    public class ViewHolder extends RecyclerView.ViewHolder {

        int position;
        TextView mNumber, mSinger, mSong;

        public void setItemPosition(int position) {
            this.position = position;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            mNumber = (TextView) itemView.findViewById(R.id.number);
            mSinger = (TextView) itemView.findViewById(R.id.singer);
            mSong = (TextView) itemView.findViewById(R.id.song);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(position);
                }
            });
        }
    }

    public SpotifyAdapter(ArrayList<Song> data, IRecycleViewSelectedItem listener) {
        this.mAdapterData = data;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spotify_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder != null) {
            Song data = mAdapterData.get(position);

            holder.mNumber.setText(String.valueOf(data.getID()));
            holder.mSinger.setText(data.getArtist());
            holder.mSong.setText(data.getTitle());

            if (position % 2 == 0) {
                holder.mSong.setPadding(45, 3, 0, 0);
            } else {
                holder.mSong.setPadding(95, 3, 0, 0);
            }

            holder.setItemPosition(position);
        }
    }
}
