package com.example.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;


import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.BottomNavigationBar.ViewHouseDetail_Fragmnet;
import com.example.models.CustomGalleryAdapter;
import com.example.models.House;
import com.example.payment.PaymentActivity;
import com.example.renthouse.R;
import com.github.dhaval2404.imagepicker.provider.GalleryProvider;
import com.google.android.material.card.MaterialCardView;
import com.google.api.Distribution;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
        implements Filterable {
    Context context;
    int customlayout_id;
    List<House> houses; //list
    List<House> listFull;
    CustomGalleryAdapter cga;
    List images;

    public RecyclerViewAdapter(Context context, List houses, int customlayout_id) {
        this.context = context;
        this.houses = houses;
        this.customlayout_id = customlayout_id;
        listFull=new ArrayList<>(houses);
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(customlayout_id, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        House house = houses.get(position);
            images=new ArrayList();
            images.add(house.getImage1());
            images.add(house.getImage2());
            images.add(house.getImage3());
            images.add(house.getImage4());
            images.add(house.getImage5());
            Glide.with(context).load(house.getImage1()).into(holder.housecardImage);
            cga=new CustomGalleryAdapter(context,images);
            holder.gallery.setAdapter(cga);
            holder.gallery.setSpacing(5);
            holder.gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Glide.with(context).load(images.get(position)).into(holder.housecardImage);

                }
            });

            boolean isExpanded=house.isExpanded();



            holder.housecardCity.setText(house.getCity().toUpperCase()+",INDIA");
            holder.housecardAddress.setText("Contact : "+house.getContactPerson()+"\n"+"House   : "+house.getHouseNo()+"\n"+"Street    : "+house.getStreet()+"\n"+"Post      : "+house.getPost());
            holder.housecardSize.setText(house.getSize());
            holder.housecardPrice.setText("Rs."+house.getPrice());

            holder.collapseable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        }




    @Override
    public int getItemCount() {

        return houses.size();
    }

    @Override
    public Filter getFilter() {
        return filterHouse;
    }

   private  Filter filterHouse=new Filter() {
       //FilterResults filterResults;

       @Override
       protected FilterResults performFiltering(CharSequence constraint) {
            String searchText=constraint.toString().toLowerCase();
            List<House> tempList=new ArrayList<>();
            if(searchText.length()==0 || searchText.isEmpty())
            {
                tempList.addAll(listFull);
            }
            else if(searchText!=null)
            {
                for(House house:listFull)
                {
                    if(house.getCity().toLowerCase().contains(searchText))
                    {
                        tempList.add(house);
                    }

                }
            }

           FilterResults filterResults=new FilterResults();
            filterResults.values=tempList;

            return filterResults;
       }

       @Override
       protected void publishResults(CharSequence constraint, FilterResults results) {
              houses.clear();
              houses.addAll((Collection<? extends House>) results.values);
              notifyDataSetChanged();
       }
   };





    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView housecardImage;
        Button housecardSize, housecardPrice,contact_call,rentit;
        TextView housecardCity, housecardAddress;
        MaterialCardView cardView;
        LinearLayout collapseable;
        Gallery gallery;

        public ViewHolder(View view) {
            super(view);
            //Getting all the views
            housecardImage = view.findViewById(R.id.housecardImage);
            housecardSize = view.findViewById(R.id.housecardsize);
            housecardPrice = view.findViewById(R.id.housecardprice);
            housecardCity = view.findViewById(R.id.housecardcity);
            housecardAddress = view.findViewById(R.id.housecardaddress);
            cardView = view.findViewById(R.id.card);
            collapseable = view.findViewById(R.id.collapsable);
            gallery=view.findViewById(R.id.simpleGallery);
            contact_call=view.findViewById(R.id.contact_call);
            rentit=view.findViewById(R.id.rentit);


            Checkout.preload(context);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    House house = houses.get(getAdapterPosition());
                    house.setExpanded(!house.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                  //  List<SlideModel> slideModelList = new ArrayList<>();

                }
            });
            housecardCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    House house = houses.get(getAdapterPosition());
                    house.setExpanded(!house.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            contact_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    House house= houses.get(getAdapterPosition());
                    String phone="tel:"+house.getPhone();
                    Intent intent=new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(phone));
                     context.startActivity(intent);
                }
            });

            rentit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    House house= houses.get(getAdapterPosition());

                    Intent intent=new Intent(context, PaymentActivity.class);
//                    Bundle bundle=new Bundle();
                    intent.putExtra("house",house);
                    context.startActivity(intent);
                   // startPayment();
                    //unregisterReceiver();
                }
            });


        }






        
    }
}
