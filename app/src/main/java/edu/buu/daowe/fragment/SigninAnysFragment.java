package edu.buu.daowe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljm.pieview.PieEntry;
import com.ljm.pieview.PieView;

import java.util.ArrayList;
import java.util.List;

import edu.buu.daowe.DaoWeApplication;
import edu.buu.daowe.R;

public class SigninAnysFragment extends Fragment {

    private PieView pieView;
    DaoWeApplication app;
    TextView tv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (DaoWeApplication) getActivity().getApplication();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin_anys, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onStart() {

        super.onStart();
        pieView = getView().findViewById(R.id.pie_view);
        List<PieEntry> list = new ArrayList<>();
        list.add(new PieEntry(90,"正常"));
        list.add(new PieEntry(3,"旷课",true));
        list.add(new PieEntry(1,"病假"));
        list.add(new PieEntry(6,"迟到",true));
        pieView.setData(list)
                .setAnimatorDuration(2000)
                .setCenterTextColor(0xff000000)
                .setCenterText(app.getRealName()+"的考勤分析")
                .setShowAnimator(true)
                .refresh();

    }
}
