package com.example.silacak2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.example.silacak2.R;
import com.example.silacak2.model.ListAnggotaModel;

import java.util.ArrayList;

public class adapterListAnggotaCoba extends BaseAdapter {
    ArrayList<ListAnggotaModel> dataModel;
    Context context;
    LayoutInflater inflater;

    public adapterListAnggotaCoba(Context c , ArrayList<ListAnggotaModel> model){
        this.dataModel = model;
        this.context = c;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return dataModel.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_check_profile,null);
        final CheckedTextView ck = view.findViewById(R.id.listAnggotadetails);
        final ImageView img = view.findViewById(R.id.listanggotafotodetails);

        ck.setText(dataModel.get(i).getName());
        if(!dataModel.get(i).getFoto().equals("null")){
            byte[] decodedString = Base64.decode(dataModel.get(i).getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            img.setImageBitmap(bitmap);
        }

        return view;
    }
}
