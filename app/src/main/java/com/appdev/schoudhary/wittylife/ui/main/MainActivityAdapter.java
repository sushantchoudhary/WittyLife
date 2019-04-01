package com.appdev.schoudhary.wittylife.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.model.Urls;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityAdapterViewHolder> {


    private List<Urls> mURLList;
    private List<QOLRanking> mQOLRanking;

    private final MainActivityAdapterOnClickHandler mainActivityAdapterOnClickHandler;

    MainActivityAdapter(List<QOLRanking> mQOLRanking, List<Urls> mURLList, MainActivityAdapterOnClickHandler mainActivityAdapterOnClickHandler) {
        this.mURLList = mURLList;
        this.mQOLRanking = mQOLRanking;
        this.mainActivityAdapterOnClickHandler = mainActivityAdapterOnClickHandler;

    }

    @NonNull
    @Override
    public MainActivityAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.destination_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MainActivityAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapterViewHolder mainActivityAdapterViewHolder, int position) {
        Urls urls = mURLList.get(position);
        String destinationImg = urls.getRegular();

        /**
         * Populate the RV with the destination images
         */
        loadDestinationImages(destinationImg, mainActivityAdapterViewHolder);

        /**
         * getValue is always null since call is asynchronous to avoid updating UI on main thread
         */
//        List<Urls> urlsList = mDB.urlDao().loadAllUrls().getValue();

    }

    private void loadDestinationImages(String destinationImg, @NonNull MainActivityAdapterViewHolder mainActivityAdapterViewHolder) {
        Context context = mainActivityAdapterViewHolder.mDestinationImageView.getContext();
        Picasso.with(context)
                .load(destinationImg)
                .placeholder(R.drawable.adamsherez258833unsplash)
                .error(R.drawable.adamsherez258833unsplash)
                .transform(new MaskTransformation(context, R.drawable.rounded_convers_transformation))
                .into(mainActivityAdapterViewHolder.mDestinationImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mURLList) {
            return 0;
        }
        return 6;
    }

    public void setDestinationData(List<Urls> urls) {
        mURLList = urls;
        notifyDataSetChanged();
    }


    public interface MainActivityAdapterOnClickHandler {
        void onClick(QOLRanking qolRanking);
    }

    public class MainActivityAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mDestinationImageView;


        MainActivityAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mDestinationImageView = itemView.findViewById(R.id.destination_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //FIXME Hacky: Works since order of records from QOLRanking is in sync with order in Urls table
            int adapterPosition = getAdapterPosition();
            QOLRanking rankingData = mQOLRanking.get(adapterPosition);
            mainActivityAdapterOnClickHandler.onClick(rankingData);
        }
    }
}
