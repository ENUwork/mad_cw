package com.pedalT.app.ui.adverts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.pedalT.app.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPager_Adapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    // Fix this: Replace with the Images from the adverts_images list:
     private List<String> images;
    // Constructor:

    // [1 Args]:
    public ViewPager_Adapter(Context context, List<String> images) {
        this.context = context;
        this.images = new ArrayList<>();
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Locate Target Layout:
        View view = layoutInflater.inflate(R.layout.advert_image_select_adapter, null);

        // Locate View:
        ImageView imageView = (ImageView) view.findViewById(R.id.imageVal);

        // Add the target image with Glide to ViewPager
        Glide.with(context).load(images.get(position)).into(imageView);

        //
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

}