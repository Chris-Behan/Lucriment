package Misc;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucriment.lucriment.R;

/**
 * Created by ChrisBehan on 5/29/2017.
 */

public class MapFragment extends Fragment  {
    private static View view;
    public MapFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_session_details, container, false);
        return v;
    }
}
