package co.alizay.calendar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.alizay.calendar.R;
import co.alizay.calendar.models.CareGiver;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CareGiver careGiver);
    }

    private List<CareGiver> careGivers = new ArrayList<>();
    private final OnItemClickListener listener;

    public SearchViewAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCareGivers(List<CareGiver> careGivers) {
        this.careGivers = careGivers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(careGivers.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return careGivers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewCareGiver;

        ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.tvNameCareGiver);
            imageViewCareGiver = (ImageView) itemView.findViewById(R.id.imageViewCareGiver);
        }

        public void bind(final CareGiver careGiver, final OnItemClickListener listener) {
            textViewName.setText(careGiver.getName().getFirst()
                 + " " + careGiver.getName().getLast());

            Picasso.with(itemView.getContext()).load(careGiver.getPicture().getThumbnail())
                    .into(imageViewCareGiver);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(careGiver);
                }
            });
        }
    }

    public void filterList(List<CareGiver> careGivers) {
        System.out.println("Its here with list of " + careGivers.size());
        this.careGivers = careGivers;
        notifyDataSetChanged();
    }
}
