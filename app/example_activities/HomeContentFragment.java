package or.school.android.navigationdrawer;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import or.school.android.navigationdrawer.R;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class HomeContentFragment extends Fragment {

  private static final String TEXT_ID = "Reg_Log";

  public static HomeContentFragment newInstance(@StringRes int textId) {


    Bundle args = new Bundle();
    args.putInt(TEXT_ID, textId);

    HomeContentFragment frag = new HomeContentFragment();
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
  Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.home_fragment, container, false);

    if (getArguments() != null) {
      String text = getString ( getArguments ().getInt ( TEXT_ID ) );
      ((TextView) layout.findViewById ( R.id.text )).setText ( text );

      if (text.equals ( "About" ))
        layout = inflater.inflate ( R.layout.about , container , false );
      if (text.equals ( "Guide" ))
        layout = inflater.inflate ( R.layout.guide , container , false );
     // כאן ניתן להוסיף פרגמנטים נוספים
    }
    else {
      throw new IllegalArgumentException("Argument " + TEXT_ID + " is mandatory");
    }

    return layout;
  }
}

