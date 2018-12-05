package com.nowfolats_restaurent_task;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nowfolats_restaurent_task.Model.RestaurentData;
import com.nowfolats_restaurent_task.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by  techsolpoint.com on 3/18/2018.
 */

public class RestaurentAdapter extends RecyclerView.Adapter<RestaurentAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<RestaurentData> productList;
    private List<RestaurentData> mFilteredList;
    FilterListener filterListener = null;
    //getting the context and product list with constructor
    public RestaurentAdapter(Context mCtx, List<RestaurentData> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.mFilteredList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.restaurent_cardlist, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final RestaurentData product = mFilteredList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getName());
        holder.textViewShortDesc.setText(product.getAddress());
        String imgUrl = product.getMyImg();

        Picasso.get().load(imgUrl).fit().centerCrop()
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.mipmap.ic_launcher)
                .into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty())
                {

                    mFilteredList = productList;
                    if(filterListener!=null)
                        filterListener.onZeroItems(false);
                } else {

                    ArrayList<RestaurentData> filteredList = new ArrayList<>();

                    for (RestaurentData storeModel : productList)
                    {

                        /*if (storeModel.store_title.toLowerCase().contains(charString)) {

                            filteredList.add(storeModel);
                        }*/

                        //  if (storeModel.store_obj.getString("name").toLowerCase().contains(charString))

                        try
                        {
                            if (storeModel.name.toLowerCase().contains(charString))
                            {
                                filteredList.add(storeModel);
                            }
                            else
                            {
                                Log.d("","null");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }



                    mFilteredList = filteredList;

                    if(filteredList.size() == 0)
                    {
                        if(filterListener!=null)
                            filterListener.onZeroItems(true);
                    }
                    else{
                        if(filterListener!=null)
                            filterListener.onZeroItems(false);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<RestaurentData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        public ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public interface FilterListener
    {
        public void onZeroItems(boolean empty);
    }
}