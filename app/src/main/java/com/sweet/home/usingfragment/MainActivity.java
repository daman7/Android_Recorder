package com.sweet.home.usingfragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends Activity{
    static String[] TITLES = {"A", "B", "C", "D"};
    static String[] CONTENT = {"Apple", "Boy", "Cat", "Dog"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
    }

    public static class DetailsActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                DetailsFragment details = new DetailsFragment();
                details.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
            }
        }
    }

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curCheck", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            updateUI(position);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_activated_1, MainActivity.TITLES));

            View detailsFrame = getActivity().findViewById(R.id.details);
            mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if(savedInstanceState != null) {
                mCurCheckPosition = savedInstanceState.getInt("curCheck", 0 );
            }

            if(mDualPane) {
                //highlight selected
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                //update UI
                updateUI(mCurCheckPosition);
            }
        }

        void updateUI(int index) {
            mCurCheckPosition = index;

            if (mDualPane) {
                getListView().setItemChecked(index, true);

                DetailsFragment details = (DetailsFragment)
                        getFragmentManager().findFragmentById(R.id.details);

                if (details == null || details.getSelectedIndex() != index) {
                    details = DetailsFragment.newInstance(index);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.details, details);
                    /*if (index == 0) {
                        ft.replace(R.id.details, details);
                    } else {
                        ft.replace(R.id.titles, details);
                    }*/
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            } else {
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra("position", index);
                startActivity(intent);
            }
        }
    }

    public static class DetailsFragment extends Fragment {

        public static DetailsFragment newInstance(int position) {
            DetailsFragment df = new DetailsFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            df.setArguments(args);

            return df;
        }

        public int getSelectedIndex() {
            return getArguments().getInt("position", 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(container==null) {
                return null;
            }

            ScrollView sv = new ScrollView(getActivity());
            TextView tv = new TextView(getActivity());
            int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    4, getActivity().getResources().getDisplayMetrics());

            tv.setPadding(padding, padding, padding, padding);
            sv.addView(tv);
            tv.setText(MainActivity.CONTENT[getSelectedIndex()]);

            return sv;
        }
    }
}
