package com.appdev.schoudhary.wittylife.ui.main;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appdev.schoudhary.wittylife.R;
import com.appdev.schoudhary.wittylife.model.QOLRanking;
import com.appdev.schoudhary.wittylife.model.Result;
import com.appdev.schoudhary.wittylife.model.Urls;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityAdapterViewHolder> {


    private List<Result> mResultList;

    private List<QOLRanking> mQOLRanking;

    private final MainActivityAdapterOnClickHandler mainActivityAdapterOnClickHandler;

    MainActivityAdapter(List<QOLRanking> mQOLRanking, List<Result> mResultList, MainActivityAdapterOnClickHandler mainActivityAdapterOnClickHandler) {
        this.mResultList = mResultList;
        this.mQOLRanking = mQOLRanking;
        this.mainActivityAdapterOnClickHandler = mainActivityAdapterOnClickHandler;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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
        Urls urls = mResultList.get(position).getUrls();
        String photographer = mResultList.get(position).getUser().getName();

//        Urls urls = mURLList.get(position);
        String destinationImg = urls.getRegular();

        String destinationCity = mQOLRanking.get(position).getCityName();


        /**
         * Populate the RV with the destination images
         */
        loadDestinationImages(destinationCity, destinationImg, mainActivityAdapterViewHolder);

        /**
         * getValue is always null since call is asynchronous to avoid updating UI on main thread
         */
//        List<Urls> urlsList = mDB.urlDao().loadAllUrls().getValue();

        /**
         * Set attribution text for the image
         */
        setAttributionText(mainActivityAdapterViewHolder, photographer);
    }

    private void setAttributionText(@NonNull MainActivityAdapterViewHolder mainActivityAdapterViewHolder, String photographer) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainActivityAdapterViewHolder.mDestinationImageView.setTooltipText("Photo by " + photographer +" " + "on Unsplash");
        } else {
        TooltipCompat.setTooltipText(mainActivityAdapterViewHolder.mDestinationImageView, "Photo by " + photographer + " " + "on Unsplash");

    }


    }

    private void loadDestinationImages(String destinationCity, String destinationImg, @NonNull MainActivityAdapterViewHolder mainActivityAdapterViewHolder) {
        Context context = mainActivityAdapterViewHolder.mDestinationImageView.getContext();
        Picasso.with(context)
                .load(destinationImg)
                .fit()
                .placeholder(R.drawable.adamsherez258833unsplash)
                .error(R.drawable.adamsherez258833unsplash)
                .transform(new MaskTransformation(context, R.drawable.rounded_convers_transformation))
                .into(mainActivityAdapterViewHolder.mDestinationImageView);


        mainActivityAdapterViewHolder.mDestinationCity.setText(destinationCity);
    }

    @Override
    public int getItemCount() {
        if (null == mResultList) {
            return 0;
        }
        return 20;
    }

    public void setDestinationData(List<Urls> urls) {
        final List<Urls> mURLList = urls;
        notifyDataSetChanged();
    }


    public interface MainActivityAdapterOnClickHandler {
        void onClick(QOLRanking qolRanking);
    }

    public class MainActivityAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mDestinationImageView;
        final TextView mDestinationCity;
        final LinearLayout mDestinationUrl;


        MainActivityAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mDestinationImageView = itemView.findViewById(R.id.destination_item);
            mDestinationCity= itemView.findViewById(R.id.destination_city);
            mDestinationUrl= itemView.findViewById(R.id.destination_container);

            mDestinationImageView.setOnClickListener(this);

            mDestinationCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDestinationImageView.performClick();
                }
            });

        }

        @Override
        public void onClick(View v) {
            //FIXME Works since order of records from QOLRanking is in sync with order in Urls table
            int adapterPosition = getAdapterPosition();
            QOLRanking rankingData = mQOLRanking.get(adapterPosition);
            mainActivityAdapterOnClickHandler.onClick(rankingData);
        }
    }
}
