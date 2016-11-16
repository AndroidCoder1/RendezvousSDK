package com.rancard.rndvusdk.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
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
public class FriendsNoTopicsInRendezvous extends Fragment
{
    View view;
    RecyclerView recyclerView;
    List<String> items,count, msisdn;
    List<Boolean> friendRequest;
    HashMap<String,ArrayList<String>> topicsList;
    ProgressBar prog;
    Button seeMoreBtn;
    TextView friendsTxt;
    CardView mainCard;
    public static boolean isEmpty = false;
    Rendezvous mRendezvousSDK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.friends_no_topics_list, container, false);
        recyclerView = (RecyclerView)view. findViewById(R.id.friendsListNoTopics);
        seeMoreBtn = (Button) view.findViewById(R.id.seeMoreFriendsBtnNoTopics);
        prog = (ProgressBar)view.findViewById(R.id.progFriendsNoTopics);
        friendsTxt = (TextView)view.findViewById(R.id.txtFriendsNoTopics);
        mainCard = (CardView) view.findViewById(R.id.mainCardNoTopics);
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
        friendRequest = new ArrayList<>();

        friendsTxt.setText("Other Friends");
        seeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("where", "friendsNoTopics");
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
        List<String> friendList;
        SharedPreferences sharedPrefs;
        Set<String> set;

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

            set = sharedPrefs.getStringSet("rateNo", null);
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
                    final String count = countNum.get(position).toString();
                    final String msisdnn = msisdn.get(position).toString();



                    try {
                        ((ItemViewHolder) holder).name.setText(newsItem);
                        ((ItemViewHolder) holder).topics.setText(Html.fromHtml("is following <b>"+count+"</b> topic(s)"));

                        if(set != null)
                        {
                            friendList =new ArrayList<String>(set);
                            System.out.println("Selected "+friendList.toString());
                        }
                        if(!friendList.isEmpty())
                        {
                            //                           final Integer friendReq = Integer.parseInt(friendList.get(position));
                            ((ItemViewHolder) holder).seeMoreBtn.setSelected((friendList.contains(String.valueOf(position))));
                            if (friendList.contains(String.valueOf(position))) {
                                ((ItemViewHolder) holder).seeMoreBtn.setText("         \u2713          ");
                            } else {
                                ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.noFriendsBtn);
                            }
                        }else{
                            ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.noFriendsBtn);
                        }

                        final String finalKey = key;
                        ((ItemViewHolder) holder).seeMoreBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Bundle args = new Bundle();
                                args.putString("name", newsItem);
                                args.putString("msisdn", msisdnn);
                                args.putStringArrayList("topics", topicsList.get(finalKey));
                                args.putInt("pos", position);
                                args.putString("where", "friendsNoTopics");

                                final TopicDialogFragment dFragment = new TopicDialogFragment();
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

        public ItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tagName);
            topics = (TextView) itemView.findViewById(R.id.tagUsers);
            profilePix = (ImageView) itemView.findViewById(R.id.profilePix);
            seeMoreBtn = (Button) itemView.findViewById(R.id.followBtn);
        }
    }

    private void getFriends()
    {
        mRendezvousSDK.getFriends(Rendezvous.msisdn, 1, 5, true, "eq0",new RendezvousRequestListener() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onResponse(RendezvousResponse response) {
                try
                {
                    JSONObject jObj = new JSONObject(response.getBody());
                    JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
                    for (int i = 0; i < arr.length(); i++)
                    {
                        friendRequest.add(false);
                        ArrayList<String> topics = new ArrayList<String>();
                        JSONObject jj = arr.getJSONObject(i);
                        if (jj.has("countTopics"))
                        {
                            count.add(String.valueOf(jj.getInt("countTopics")));
                        } else {
                            count.add("0");
                        }
                        if (jj.has("name"))
                        {
                            items.add(jj.getString("name"));
                        } else {
                            items.add("");
                        }

                        if (jj.has("msisdn")) {
                            msisdn.add(String.valueOf(jj.getLong("msisdn")));
                        } else {
                            msisdn.add("");
                        }

                        JSONArray arrTopics = jj.getJSONArray("following");
                        if (arrTopics.length() > 0)
                        {
                            for (int j = 0; j < arrTopics.length(); j++) {
                                topics.add(arrTopics.getString(j));
                            }
                        }
                        topicsList.put(String.valueOf(i), topics);
                    }

                    if (arr.length() == 0)
                    {
                        if (getActivity() == null) {

                        } else {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainCard.setVisibility(View.GONE);
                                    isEmpty = true;
                                }
                            });
                        }

                    } else
                    {
                        if (getActivity() == null) {

                        } else
                        {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    recyclerView.setAdapter(new FriendsListAdapter(getActivity(), items, count, topicsList, recyclerView));
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

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (items.isEmpty()) {

                                    mainCard.setVisibility(View.GONE);
                                    isEmpty = true;
                                }

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