package com.rancard.rndvusdk.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rancard.rndvusdk.R;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.RendezvousResponse;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;
import com.rancard.rndvusdk.utils.DividerDecoration;
import com.rancard.rndvusdk.utils.VerticalWrapLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by RSL-PROD-003 on 1/18/16.
 */
public class FriendsInRendezvous extends Fragment
{
    View view;
    RecyclerView recyclerView;
    List<String> items,count,msisdn;
    HashMap<String,ArrayList<String>> topicsList;
    ProgressBar prog;
    Button seeMoreBtn;
    CardView mainCard;
    Rendezvous mRendezvousSDK;
    public static boolean isEmpty = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.friends_list, container, false);
        recyclerView = (RecyclerView)view. findViewById(R.id.friendsList);
        seeMoreBtn = (Button) view.findViewById(R.id.seeMoreFriendsBtn);
        prog = (ProgressBar)view.findViewById(R.id.progFriends);
        mainCard = (CardView)view.findViewById(R.id.mainCard);
        mRendezvousSDK = Rendezvous.getInstance();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        items = new ArrayList<>();
        topicsList = new HashMap<>();
        count = new ArrayList<>();
        msisdn = new ArrayList<>();

        seeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("where", "friends");
                FullListDialogFragment dFragment = new FullListDialogFragment();
                dFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        try {
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                dFragment.setArguments(args);
                dFragment.show(getChildFragmentManager(), "friends");
            }
        });

        getFriends();
    }


    private class FriendsListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<T> items, countNum;
        HashMap<String, ArrayList> topicsList;
        Context mContext;
        Set<String> set;
        List<String> friendList;
        SharedPreferences sharedPrefs;

        public FriendsListAdapter (Context context,
                                   List<T> items,
                                   List<T> count,
                                   HashMap topicsList,
                                   RecyclerView recyclerView)
        {
            this.items = items;
            this.mContext = context;
            this.countNum = count;
            this.topicsList = topicsList;
            friendList = new ArrayList<>();
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        }


        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder vh;

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            vh = new ItemViewHolder(view);

            set = sharedPrefs.getStringSet("rate", null);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
        {
            if (holder instanceof ItemViewHolder)
            {
                if(items != null)
                {
                    List<String> copyOfKeys = new ArrayList<>();
                    String key = "";
                    if(topicsList != null || !topicsList.isEmpty())
                    {
                        try {
                            key = String.valueOf(position);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    final String newsItem = items.get(position).toString();
                    // final String count = countNum.get(position).toString();
                    final String msisdnn = msisdn.get(position).toString();

                    try
                    {
                        ((ItemViewHolder) holder).name.setText(newsItem);
                        ((ItemViewHolder) holder).topics.setText(Html.fromHtml("<b>" + msisdnn + "</b>"));


                        if(set != null){
                            friendList =new ArrayList<String>(set);
                        }

                        ((ItemViewHolder) holder).seeMoreBtn.setVisibility(View.GONE);
                        if(!friendList.isEmpty())
                        {
                            ((ItemViewHolder) holder).seeMoreBtn.setSelected((friendList.contains(String.valueOf(position))));
                            if (friendList.contains(String.valueOf(position))) {
                                ((ItemViewHolder) holder).seeMoreBtn.setText("         \u2713          ");
                            } else {
                                ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.friendsBtn);
                            }
                        }else
                        {
                            ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.friendsBtn);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent("com.multimedia.tagFriendsActivity");
                                startActivity(intent);
                            }
                        });

//                            final String finalKey = key;
//                            ((ItemViewHolder) holder).seeMoreBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Bundle args = new Bundle();
//                                    args.putString("name", newsItem);
//                                    args.putString("msisdn", msisdnn);
//                                    args.putStringArrayList("topics", topicsList.get(finalKey));
//                                    args.putString("where", "friends");
//                                    args.putInt("pos", position);
//                                    TopicDialogFragment dFragment = new TopicDialogFragment();
//                                    dFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                        @Override
//                                        public void onDismiss(DialogInterface dialogInterface) {
//                                            recyclerView.getAdapter().notifyDataSetChanged();
//                                        }
//                                    });
//                                    dFragment.setArguments(args);
//                                    dFragment.show(getChildFragmentManager(), "friends");
//                                }
//                            });
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
        public TextView topics;
        public ImageView profilePix;
        public Button seeMoreBtn;

        public ItemViewHolder(View itemView)
        {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tagName);
            topics = (TextView) itemView.findViewById(R.id.tagUsers);
            profilePix = (ImageView) itemView.findViewById(R.id.profilePix);
            seeMoreBtn = (Button) itemView.findViewById(R.id.followBtn);
        }
    }

    private void getFriends()
    {
        mRendezvousSDK.getFriends("patrick.asare-frimpong@rancardsolutions.com", 1, 20, true, "gt0", new RendezvousRequestListener() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onResponse(RendezvousResponse response) {
                try
                {
                    JSONObject jObj = new JSONObject(response.getBody());
                    JSONArray arr = jObj.getJSONArray("data").getJSONObject(0).getJSONArray("contacts");
                    for (int i = 0; i < arr.length(); i++)
                    {
                        ArrayList<String> topics = new ArrayList<String>();
                        JSONObject jj = arr.getJSONObject(i);
                        //count.add(String.valueOf(jj.getInt("countTopics")));
                        if(jj.has("title")) {
                            items.add(jj.getString("title"));
                        }else{
                            items.add("");
                        }

                        if(jj.has("email")) {
                            msisdn.add(String.valueOf(jj.getString("email")));
                        }else{
                            msisdn.add("");
                        }
//                        JSONArray arrTopics = jj.getJSONArray("following");
//                        if (arrTopics.length() > 0) {
//                            for (int j = 0; j < arrTopics.length(); j++) {
//                                topics.add(arrTopics.getString(j));
//                            }
//                        }
//                        topicsList.put(String.valueOf(i), topics);

                    }

                    if (arr.length() == 0) {

                    } else
                    {
                        if (getActivity() == null)
                        {

                        } else
                        {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(new FriendsListAdapter(getActivity(), items, new ArrayList(), topicsList, recyclerView));
                                    recyclerView.setLayoutManager(new VerticalWrapLayoutManager(getActivity()));
                                    recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
                                    prog.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    if (getActivity() != null)
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prog.setVisibility(View.GONE);
                            }
                        });
                    }
                }

            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

}