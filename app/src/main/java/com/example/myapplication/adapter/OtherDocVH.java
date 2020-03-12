package com.example.myapplication.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.data.BaseModel;
import com.example.myapplication.data.OtherDocModel;
import com.example.myapplication.listener.OtherDocClickListener;

public class OtherDocVH extends BaseVH {
    private View mView;
    private TextView textView;
    private OtherDocClickListener otherDocClickListener;

    public OtherDocVH(@NonNull View itemView, OtherDocClickListener otherDocClickListener) {
        super(itemView);
        this.mView = itemView;
        this.otherDocClickListener = otherDocClickListener;
        textView = itemView.findViewById(R.id.tv_other_doc);
    }

    @Override
    void onBind(BaseModel baseModel, boolean selected) {
        OtherDocModel model = (OtherDocModel) baseModel;
        textView.setText(model.getName());

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherDocClickListener.otherDocClicked();
            }
        });
    }

}
