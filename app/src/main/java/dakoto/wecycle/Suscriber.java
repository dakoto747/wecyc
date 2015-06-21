package dakoto.wecycle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Suscriber.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Suscriber#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Suscriber extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button schedule;
    private Button view_history;
    private SessionManager session;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Suscriber.
     */
    // TODO: Rename and change types and number of parameters
    public static Suscriber newInstance(String param1, String param2) {
        Suscriber fragment = new Suscriber();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Suscriber() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        schedule=(Button)view.findViewById(R.id.suscriber_schedule_button);

        session=new SessionManager(this.getActivity().getApplicationContext());
        final AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setIcon(R.drawable.wecyclers);

        schedule.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                HttpClient client = new DefaultHttpClient();
//                curl -i -H "Content-Type:application/json" -H
//                "Authorization: Bearer 461DB80AB9BE17C1938F590BCEEE72121856C29D40B1B4FA01DA11CD6FEFD81C"
//                -X POST -d '{}' https://wecyclers-api.herokuapp.com/api/v1/orders
                try
                {
                    HttpPost request = new HttpPost("http://wecyclers-api.herokuapp.com/api/v1/orders");
                    StringEntity json =new StringEntity("{}");
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Authorization", "Bearer "+session.getToken());
                    request.setEntity(json);
                    HttpResponse response = client.execute(request);
//                Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    String json_response = reader.readLine();
                    JSONTokener tokener = new JSONTokener(json_response);
                    JSONObject jsonarray = new JSONObject(tokener);
                    if(jsonarray.isNull("error")){
                        alertDialog.setMessage("Your order has been set.\n The following are the details:\n"+jsonarray.getString("date")+"\n"+jsonarray.getString("partner_id"));
//                        Toast.makeText(, session.getToken(), Toast.LENGTH_LONG).show();
                        alertDialog.show();
                    }else{
                        alertDialog.setMessage(jsonarray.getString("error"));
//                        Toast.makeText(getBaseContext(), session.getToken(), Toast.LENGTH_LONG).show();
                        alertDialog.show();
                    }
                }

                catch(Exception ex)
                {
                    ex.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "exception", Toast.LENGTH_LONG).show();
//                  Log.d(TAG, "Register Response: " + ex.getMessage());
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suscriber, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
