package logismikou.texnologia.tamethepython;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;


public class HomeFragment extends Fragment {

    WebView intro, extra_intro;
    Button read_more, read_less;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        intro = v.findViewById(R.id.intro);
        extra_intro = v.findViewById(R.id.extra_intro);
        read_more = v.findViewById(R.id.read_more);
        read_less = v.findViewById(R.id.read_less);

        intro.loadUrl("file:///android_asset/intro.html");
        extra_intro.loadUrl("file:///android_asset/extra_intro.html");

        extra_intro.setVisibility(View.GONE);
        read_less.setVisibility(View.GONE);

        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extra_intro.setVisibility(View.VISIBLE);
                read_less.setVisibility(View.VISIBLE);
            }
        });

        read_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extra_intro.setVisibility(View.GONE);
                read_less.setVisibility(View.GONE);
            }
        });

        return v;
    }

}
