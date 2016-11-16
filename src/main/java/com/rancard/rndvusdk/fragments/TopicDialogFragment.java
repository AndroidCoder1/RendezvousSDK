package com.rancard.rndvusdk.fragments;

/**
 * Created by RSL-PROD-003 on 1/20/16.
 */

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rancard.rndvusdk.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicDialogFragment extends DialogFragment
{
    private DialogInterface.OnDismissListener onDismissListener;
    List<String> items, selectedTopics;
    RecyclerView recyclerView;
    TextView txtName;
    Button doneBtn;
    String msisdn,namee ="";
    int pos = 0;
    ProgressBar prog;
    ArrayList<Integer> arrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.topicslist, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.topicsList);
        txtName = (TextView) rootView.findViewById(R.id.txtName);
        doneBtn = (Button) rootView.findViewById(R.id.doneBtn);
        prog = (ProgressBar) rootView.findViewById(R.id.topicProg);
        return rootView;
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Bundle mArgs = getArguments();
        namee = mArgs.getString("where");
        msisdn = mArgs.getString("msisdn");
        pos = mArgs.getInt("pos");
        items = new ArrayList<>();
        arrayList = new ArrayList<>();
        selectedTopics = new ArrayList<>();
        String name = mArgs.getString("name");

        txtName.setText(Html.fromHtml("Get the opinion of <font color='#0198E1'><b>" + name + "</b></font> on the following topics"));

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTopics(selectedTopics);
                dismiss();
            }
        });

        getTopics();
    }

    private class FriendsListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<T> items;
        Context mContext;

        public FriendsListAdapter (Context context, List<T> items,RecyclerView recyclerView)
        {
            this.items = items;
            this.mContext = context;
            selectedTopics = new ArrayList<>();
        }




        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder vh;
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.topicslistitem, parent, false);
            vh = new ItemViewHolder(view);

            return vh;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
        {
            if (holder instanceof ItemViewHolder)
            {
                if(items != null)
                {
                    final String newsItem = items.get(position).toString();

                    if(selectedTopics.contains(newsItem)){
                        ((ItemViewHolder)holder).seeMoreBtn.setChecked(true);
                    }else{
                        ((ItemViewHolder)holder).seeMoreBtn.setChecked(false);
                    }



                    try
                    {
                        ((ItemViewHolder) holder).name.setText(newsItem);
                        ((ItemViewHolder)holder).seeMoreBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final String newsItemm = items.get(position).toString();

                                if (!selectedTopics.contains(newsItemm)) {

                                    selectedTopics.add(newsItemm);

                                }else {

                                    selectedTopics.remove(newsItemm);

                                }

                            }
                        });


                        System.out.println("sel top "+selectedTopics.toString());

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }



    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;
        public CheckBox seeMoreBtn;

        public ItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.topicName);
            seeMoreBtn = (CheckBox) itemView.findViewById(R.id.check);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void sendTopics(List<String> topics)
    {
        if(!topics.isEmpty())
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Set<String> set;
            if(namee.equalsIgnoreCase("friends"))
            {
                if(sharedPrefs.getStringSet("rate", null) == null){
                    set = new HashSet<String>();
                }else{
                    set = sharedPrefs.getStringSet("rate", null);
                }
                set.add(String.valueOf(pos));
                editor.putStringSet("rate", set);

            }else
            if(namee.equalsIgnoreCase("friendsNoTopics"))
            {
                if(sharedPrefs.getStringSet("rateNo", null) == null){
                    set = new HashSet<String>();
                }else{
                    set = sharedPrefs.getStringSet("rateNo", null);
                }
                set.add(String.valueOf(pos));
                editor.putStringSet("rateNo", set);
            }
            else{

                if(sharedPrefs.getStringSet("people", null) == null){
                    set = new HashSet<String>();
                }else{
                    set = sharedPrefs.getStringSet("people", null);
                }
                set.add(String.valueOf(pos));
                editor.putStringSet("people", set);
            }

            editor.apply();
        }
        StringBuilder topicStr = new StringBuilder();

        for(int i = 0; i < topics.size(); i++)
        {
            topicStr.append(topics.get(i));
            if(i != topics.size() -1){
                topicStr.append(",");
            }
        }

//        Rndvu.getClient().sendTopics(Rndvu.msisdn, msisdn, "1", topicStr.toString(), new RndvuRequestListener() {
//            @Override
//            public void beforeRndvuRequest() {
//
//            }
//
//            @Override
//            public void processRndvuResponse(RndvuResponse response) {
//                System.out.println("Topics sent "+response.getBody());
//            }
//
//            @Override
//            public void onRndvuRequestTimeout(String message) {
//
//            }
//        });
        System.out.println("Topics Selected" + topics.toString());
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private void getTopics()
    {
//        Rndvu.getClient().getTopics(Rndvu.msisdn,false, new RndvuRequestListener() {
//            @Override
//            public void beforeRndvuRequest() {
//
//            }
//
//            @Override
//            public void processRndvuResponse(RndvuResponse response) {
//
//                String responseStr = response.getBody();
//                try {
//                    JSONObject jObj = new JSONObject(responseStr);
//
//                    JSONArray arr = jObj.getJSONArray("data");
//
//                    if(arr.length() > 0){
//                        JSONObject jj  = arr.getJSONObject(0);
//                        JSONArray arrTopics = jj.getJSONArray(Rndvu.msisdn);
//                        for(int i = 0; i < arrTopics.length(); i++){
//                            items.add(arrTopics.getString(i));
//                        }
//
//                        if (items != null){
//
//                            if (!items.isEmpty()) {
//
//                                if(getActivity() != null) {
//
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            recyclerView.setAdapter(new FriendsListAdapter(getActivity(), items, recyclerView));
//                                            recyclerView.setLayoutManager(new VerticalWrapLayoutManager(getActivity()));
//                                            recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
//
//                                        }
//                                    });
//
//                                }
//                            }
//                        }
//
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            prog.setVisibility(View.GONE);
//                        }
//                    });
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if(getActivity() != null) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                prog.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onRndvuRequestTimeout(String message) {
//
//            }
//        });
    }

}