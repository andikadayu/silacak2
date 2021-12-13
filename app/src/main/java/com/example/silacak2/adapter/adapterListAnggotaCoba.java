package com.example.silacak2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.silacak2.R;
import com.example.silacak2.model.ListAnggotaModel;

import java.util.ArrayList;

public class adapterListAnggotaCoba extends ArrayAdapter<ListAnggotaModel> {
    ArrayList<ListAnggotaModel> dataModel;
    Context context;
    private CheckedTextView ck;
    public SparseBooleanArray checkedState = new SparseBooleanArray();

    public adapterListAnggotaCoba(@NonNull Context context, ArrayList<ListAnggotaModel> dataModel) {
        super(context, 0,dataModel);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void clearAllCheck(){
        checkedState.clear();
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_check_profile,viewGroup,false);
        ListAnggotaModel dataModels = getItem(i);

        ck = view.findViewById(R.id.listAnggotadetails);
        final ImageView img = view.findViewById(R.id.listanggotafotodetails);

        ck.setText(dataModels.getName());
        ck.setChecked(checkedState.get(i));
        if(!dataModels.getFoto().equals("null")){
            byte[] decodedString = Base64.decode(dataModels.getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
            img.setImageBitmap(bitmap);
        }

        ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedTextView checkedText = (CheckedTextView) v;
                checkedText.toggle();
                if (checkedText.isChecked())
                    checkedState.put(i,true);
                else
                    checkedState.delete(i);
            }
        });

        return view;
    }
}
