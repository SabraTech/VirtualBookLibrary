package com.example.space.virtualbooklibrary;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyFabFragment extends AAH_FabulousFragment {

    ArrayMap<String, List<String>> appliedFilters = new ArrayMap<>();
    List<TextView> textViews = new ArrayList<>();
    TabLayout tabsTypes;
    ImageButton refresh, apply;
    SectionsPagerAdapter sectionsAdapter;
    private DisplayMetrics metrics;

    public static MyFabFragment newInstance() {
        MyFabFragment myFabFragment = new MyFabFragment();
        return myFabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appliedFilters = ((HomeActivity) getActivity()).getAppliedFilters();
        metrics = this.getResources().getDisplayMetrics();

        for (Map.Entry<String, List<String>> entry : appliedFilters.entrySet()) {
            Log.d("filterFragment", "from activity: " + entry.getKey());
            for (String s : entry.getValue()) {
                Log.d("filterFragment", "from activity val: " + s);

            }
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.filter_view, null);

        RelativeLayout content = contentView.findViewById(R.id.content);
        LinearLayout buttons = contentView.findViewById(R.id.buttons);
        refresh = contentView.findViewById(R.id.btn_refresh);
        apply = contentView.findViewById(R.id.btn_apply);
        ViewPager types = contentView.findViewById(R.id.types);
        tabsTypes = contentView.findViewById(R.id.tabs_types);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter(appliedFilters);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (TextView tv : textViews) {
                    tv.setTag("unselected");
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                appliedFilters.clear();
            }
        });

        sectionsAdapter = new SectionsPagerAdapter();
        types.setOffscreenPageLimit(4);
        types.setAdapter(sectionsAdapter);
        sectionsAdapter.notifyDataSetChanged();
        tabsTypes.setupWithViewPager(types);

        setAnimationDuration(600); // default 500
        setPeekHeight(300); // default 400dp
        setCallbacks((Callbacks) getActivity()); // to get back result
        setAnimationListener((AnimationListener) getActivity()); // to get animation callbacks
        setViewgroupStatic(buttons); // static layout at bottom
        setViewPager(types); // to enable scroll
        setViewMain(content); // main bottom sheet view
        setMainContentView(contentView);
        super.setupDialog(dialog, style);
    }

    private void inflateLayoutWithFilters(final String filterCategory, FlexboxLayout fbl) {
        List<String> keys = new ArrayList<>();
        switch (filterCategory) {
            case "category":
                keys = ((HomeActivity) getActivity()).getBookData().getUniqueCategoryKeys();
                break;
            case "rating":
                keys = ((HomeActivity) getActivity()).getBookData().getUniqueRatingKeys();
                break;
//            case "year":
//                keys = ((HomeActivity) getActivity()).getBookData().getUniqueYearKeys();
//                break;
        }

        for (int i = 0; i < keys.size(); i++) {
            View subChild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
            final TextView tv = subChild.findViewById(R.id.txt_title);
            tv.setText(keys.get(i));
            final int finalI = i;
            final List<String> finalKeys = keys;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tv.getTag() != null && tv.getTag().equals("selected")) {
                        tv.setTag("unselected");
                        tv.setBackgroundResource(R.drawable.chip_unselected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                        removeFromSelectedMap(filterCategory, finalKeys.get(finalI));
                    } else {
                        tv.setTag("selected");
                        tv.setBackgroundResource(R.drawable.chip_selected);
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                        addToSelectedMap(filterCategory, finalKeys.get(finalI));
                    }
                }
            });

            if (appliedFilters != null && appliedFilters.get(filterCategory) != null && appliedFilters.get(filterCategory).contains(keys.get(finalI))) {
                tv.setTag("selected");
                tv.setBackgroundResource(R.drawable.chip_selected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
            } else {
                tv.setBackgroundResource(R.drawable.chip_unselected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
            }
            textViews.add(tv);

            fbl.addView(subChild);
        }
    }

    private void addToSelectedMap(String key, String value) {
        if (appliedFilters.get(key) != null && !appliedFilters.get(key).contains(value)) {
            appliedFilters.get(key).add(value);
        } else {
            List<String> temp = new ArrayList<>();
            temp.add(value);
            appliedFilters.put(key, temp);
        }
    }

    private void removeFromSelectedMap(String key, String value) {
        if (appliedFilters.get(key).size() == 1) {
            appliedFilters.remove(key);
        } else {
            appliedFilters.get(key).remove(value);
        }
    }

    public class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, container, false);
            FlexboxLayout fbl = layout.findViewById(R.id.fbl);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("category", fbl);
                    break;
                case 1:
                    inflateLayoutWithFilters("rating", fbl);
                    break;
            }
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CATEGORY";
                case 1:
                    return "RATING";
            }
            return "";
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
