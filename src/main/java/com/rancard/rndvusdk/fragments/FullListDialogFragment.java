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
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rancard.rndvusdk.R;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.utils.EndlessRecyclerOnScrollListener;
import com.rancard.rndvusdk.utils.VerticalWrapLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FullListDialogFragment extends DialogFragment
{
    private DialogInterface.OnDismissListener onDismissListener;
    RecyclerView recyclerView;
    List<String> items,count, msisdn;
    HashMap<String,ArrayList<String>> topicsList;
    TextView txtName,txtSub;
    Button doneBtn;
    ProgressBar prog;
    int pagee = 0;
    Handler handler;
    VerticalWrapLayoutManager lm;
    String name ="";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.full_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rendezvous_list_full);
        txtName = (TextView) rootView.findViewById(R.id.rendezvous_txtTitle);
        txtSub = (TextView) rootView.findViewById(R.id.rendezvous_txtSubTitle);
        prog = (ProgressBar) rootView.findViewById(R.id.rendezvous_progress);
        doneBtn = (Button) rootView.findViewById(R.id.rendezvous_list_doneBtn);
        return rootView;
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        handler = new Handler();
        RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        items = new ArrayList<>();
        topicsList = new HashMap<>();
        count = new ArrayList<>();
        msisdn = new ArrayList<>();
        lm = new VerticalWrapLayoutManager(getActivity());

        Bundle mArgs = getArguments();
        name = mArgs.getString("where");

        if(name.equalsIgnoreCase("friends"))
        {
            txtName.setText(Html.fromHtml("Friends"));
            txtSub.setText(Html.fromHtml("Get opinions of your friends and see their recommendations"));
        }else
        if(name.equalsIgnoreCase("friendsNoTopics"))
        {
            txtName.setText(Html.fromHtml("Other Friends"));
            txtSub.setText(Html.fromHtml("Recommend new topics to these friends"));
        }
        else
        {
            txtName.setText(Html.fromHtml("People You May Know "));
            txtSub.setText(Html.fromHtml("To see recommendations from your friends, add them"));
        }

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        getFriends(1);
    }

    private class FriendsListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<T> items, countNum;
        HashMap<String, ArrayList> topicsList;
        Context mContext;
        private final int VIEW_ITEM = 1;
        private final int VIEW_PROG = 0;
        RecyclerView recyclerVieww;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private EndlessRecyclerOnScrollListener onLoadMoreListener;
        String where;
        Set<String> set;
        List<String> friendList;
        SharedPreferences sharedPrefs;

        public FriendsListAdapter (Context context,
                                   List<T> items,
                                   List<T> count,
                                   HashMap topicsList,
                                   String where,
                                   RecyclerView recyclerVieww)
        {
            this.items = items;
            this.mContext = context;
            this.countNum = count;
            this.topicsList = topicsList;
            this.recyclerVieww = recyclerVieww;
            this.where = where;


            friendList = new ArrayList<>();
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            final VerticalWrapLayoutManager linearLayoutManager =
                    (VerticalWrapLayoutManager) recyclerVieww.getLayoutManager();

            recyclerVieww.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading
                            && totalItemCount <= (lastVisibleItem + visibleThreshold))
                    {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder vh;

            if (viewType == VIEW_ITEM)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                if(where.equalsIgnoreCase("friends"))
                    set = sharedPrefs.getStringSet("rate", null);
                else if(where.equalsIgnoreCase("friendsNoTopics"))
                    set = sharedPrefs.getStringSet("rateNo", null);
                else
                    set = sharedPrefs.getStringSet("people", null);
                vh = new ItemViewHolder(view);
            } else
            {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progressbar_item, parent, false);

                vh = new ProgressViewHolder(v);
            }

            return vh;
        }

        @Override
        public int getItemViewType(int position)
        {
            return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ItemViewHolder) {

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
                    String newsItem = "", count = "", msisdnn = "";
                    try {
                        newsItem = items.get(position).toString();
                        count = countNum.get(position).toString();
                        msisdnn = msisdn.get(position).toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try
                    {
                        ((ItemViewHolder) holder).name.setText(newsItem);
                        ((ItemViewHolder) holder).topics.setText(Html.fromHtml("is following <b>"+count+"</b> topics"));
                        if(set != null){
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

                                if(name.equalsIgnoreCase("friends")) {
                                    ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.friendsBtn);
                                }else  if(name.equalsIgnoreCase("friendsNoTopics")) {
                                    ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.noFriendsBtn);
                                }
                                else{
                                    ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.peopleBtn);
                                }
                            }
                        }else
                        {
                            if(name.equalsIgnoreCase("friends")) {
                                ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.friendsBtn);
                            }else  if(name.equalsIgnoreCase("friendsNoTopics")) {
                                ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.noFriendsBtn);
                            }
                            else{
                                ((ItemViewHolder) holder).seeMoreBtn.setText(Rendezvous.peopleBtn);
                            }
                        }




                        final String finalKey = key;
                        final String finalNewsItem = newsItem;
                        final String finalMsisdnn = msisdnn;
                        ((ItemViewHolder) holder).seeMoreBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("name", finalNewsItem);
                                args.putString("where", name);
                                args.putInt("pos", position);
                                args.putString("msisdn", finalMsisdnn);
                                args.putStringArrayList("topics", topicsList.get(finalKey));
                                TopicDialogFragment dFragment = new TopicDialogFragment();
                                dFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        recyclerView.getAdapter().notifyDataSetChanged();
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

            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }
        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setOnLoadMoreListener(EndlessRecyclerOnScrollListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
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

    public static class ProgressViewHolder extends RecyclerView.ViewHolder
    {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    private void getFriends(final int page)
    {

        if(name.equalsIgnoreCase("friends"))
        {
//            Rndvu.getClient().getFriends("", Rndvu.msisdn, String.valueOf(page), "10", true, "gt0",new RndvuRequestListener() {
//                @Override
//                public void beforeRndvuRequest() {
//
//                }
//
//                @Override
//                public void processRndvuResponse(RndvuResponse response) {
//
//                    try {
//                        String res = response.getBody();
//                        System.out.println("Get Friends " + res);
//                        JSONObject jObj = new JSONObject(res);
//
//
//                        JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
//                        for (int i = 0; i < arr.length(); i++) {
//                            ArrayList<String> topics = new ArrayList<String>();
//                            JSONObject jj = arr.getJSONObject(i);
//                            count.add(String.valueOf(jj.getInt("countTopics")));
//                            items.add(jj.getString("name"));
//                            if(jj.has("msisdn")) {
//                                msisdn.add(String.valueOf(jj.getLong("msisdn")));
//                            }else{
//                                msisdn.add("");
//                            }
//                            JSONArray arrTopics = jj.getJSONArray("following");
//                            if (arrTopics.length() > 0) {
//                                for (int j = 0; j < arrTopics.length(); j++) {
//                                    topics.add(arrTopics.getString(j));
//                                }
//                            }
//                            topicsList.put(String.valueOf(i), topics);
//
//                        }
//                        if (getActivity() == null) {
//
//                        } else {
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    recyclerView.setLayoutManager(lm);
//                                    final FriendsListAdapter mAdapter = new FriendsListAdapter(getActivity(), items, count, topicsList, name, recyclerView);
//                                    recyclerView.setAdapter(mAdapter);
//                                    recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
//                                    prog.setVisibility(View.GONE);
//
//
//                                    mAdapter.setOnLoadMoreListener(new EndlessRecyclerOnScrollListener() {
//                                        @Override
//                                        public void onLoadMore() {
//                                            //add null , so the adapter will check view_type and show progress bar at bottom
//                                            if (items.size() > 9) {
//                                                items.add(null);
//                                                count.add(null);
//                                                msisdn.add(null);
//                                                topicsList.put(null, null);
//
//                                                mAdapter.notifyItemInserted(items.size() - 1);
//                                                items.remove(items.size() - 1);
//                                                count.remove(items.size() - 1);
//                                                msisdn.remove(items.size() - 1);
//                                                topicsList.remove(items.size() - 1);
//                                                mAdapter.notifyItemRemoved(items.size());
//
//                                                pagee = (items.size() / 10) + 1;
//
//                                                getFriendsWithPages(pagee, mAdapter);
//
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    prog.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onRndvuRequestTimeout(String message) {
//
//                }
//            });
        }else
        if(name.equalsIgnoreCase("friendsNoTopics"))
        {
//            Rndvu.getClient().getFriends("", Rndvu.msisdn, String.valueOf(page), "10",true, "eq0",new RndvuRequestListener() {
//                @Override
//                public void beforeRndvuRequest() {
//
//                }
//
//                @Override
//                public void processRndvuResponse(RndvuResponse response) {
//
//                    try {
//                        String res = response.getBody();
//                        System.out.println("Get Friends " + res);
//                        JSONObject jObj = new JSONObject(res);
//
//
//                        JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
//                        for (int i = 0; i < arr.length(); i++) {
//                            ArrayList<String> topics = new ArrayList<String>();
//                            JSONObject jj = arr.getJSONObject(i);
//                            count.add(String.valueOf(jj.getInt("countTopics")));
//                            items.add(jj.getString("name"));
//                            if(jj.has("msisdn")) {
//                                msisdn.add(String.valueOf(jj.getLong("msisdn")));
//                            }else{
//                                msisdn.add("");
//                            }
//                            JSONArray arrTopics = jj.getJSONArray("following");
//                            if (arrTopics.length() > 0) {
//                                for (int j = 0; j < arrTopics.length(); j++) {
//                                    topics.add(arrTopics.getString(j));
//                                }
//                            }
//                            topicsList.put(String.valueOf(i), topics);
//
//                        }
//                        if (getActivity() == null) {
//
//                        } else {
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    recyclerView.setLayoutManager(lm);
//                                    final FriendsListAdapter mAdapter = new FriendsListAdapter(getActivity(), items, count, topicsList, name, recyclerView);
//                                    recyclerView.setAdapter(mAdapter);
//                                    recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
//                                    prog.setVisibility(View.GONE);
//
//
//                                    mAdapter.setOnLoadMoreListener(new EndlessRecyclerOnScrollListener() {
//                                        @Override
//                                        public void onLoadMore() {
//                                            //add null , so the adapter will check view_type and show progress bar at bottom
//                                            if (items.size() > 9) {
//                                                items.add(null);
//                                                count.add(null);
//                                                msisdn.add(null);
//                                                topicsList.put(null, null);
//
//                                                mAdapter.notifyItemInserted(items.size() - 1);
//                                                items.remove(items.size() - 1);
//                                                count.remove(items.size() - 1);
//                                                msisdn.remove(items.size() - 1);
//                                                topicsList.remove(items.size() - 1);
//                                                mAdapter.notifyItemRemoved(items.size());
//
//                                                pagee = (items.size() / 10) + 1;
//
//                                                getFriendsWithPages(pagee, mAdapter);
//
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    prog.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onRndvuRequestTimeout(String message) {
//
//                }
//            });
        }
        else
        {
//            Rndvu.getClient().getPeopleYouMayKnow("", Rndvu.msisdn, "1", "10", new RndvuRequestListener() {
//                @Override
//                public void beforeRndvuRequest() {
//
//                }
//
//                @Override
//                public void processRndvuResponse(RndvuResponse response) {
//
//                    try {
//                        String res = response.getBody();
//                        System.out.println("People List " + res);
//                        JSONObject jObj = new JSONObject(res);
//                        JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
//                        for (int i = 0; i < arr.length(); i++) {
//                            ArrayList<String> topics = new ArrayList<String>();
//                            JSONObject jj = arr.getJSONObject(i);
//                            if (jj.has("countTopics")) {
//                                count.add(String.valueOf(jj.getInt("countTopics")));
//                            } else {
//                                count.add("0");
//                            }
//                            if (jj.has("name")) {
//                                items.add(jj.getString("name"));
//                            } else {
//                                items.add("");
//                            }
//
//                            if(jj.has("msisdn")) {
//                                msisdn.add(String.valueOf(jj.getLong("msisdn")));
//                            }else{
//                                msisdn.add("");
//                            }
//
//                            if (jj.has("following")) {
//                                JSONArray arrTopics = jj.getJSONArray("following");
//                                if (arrTopics.length() > 0) {
//                                    for (int j = 0; j < arrTopics.length(); j++) {
//                                        topics.add(arrTopics.getString(j));
//                                    }
//                                }
//                            } else {
//                                topics.add("");
//                            }
//                            topicsList.put(String.valueOf(i), topics);
//
//                        }
//                        if (getActivity() == null) {
//
//                        } else {
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    recyclerView.setLayoutManager(lm);
//                                    final FriendsListAdapter mAdapter = new FriendsListAdapter(getActivity(), items, count, topicsList, name, recyclerView);
//                                    recyclerView.setAdapter(mAdapter);
//                                    recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
//                                    prog.setVisibility(View.GONE);
//
//
//                                    mAdapter.setOnLoadMoreListener(new EndlessRecyclerOnScrollListener() {
//                                        @Override
//                                        public void onLoadMore() {
//                                            //add null , so the adapter will check view_type and show progress bar at bottom
//                                            items.add(null);
//                                            count.add(null);
//                                            msisdn.add(null);
//                                            topicsList.put(null, null);
//
//                                            mAdapter.notifyItemInserted(items.size() - 1);
//                                            items.remove(items.size() - 1);
//                                            count.remove(items.size() - 1);
//                                            topicsList.remove(items.size() - 1);
//                                            msisdn.remove(items.size() - 1);
//                                            mAdapter.notifyItemRemoved(items.size());
//
//                                            pagee = (items.size() / 10) + 1;
//
//                                            getFriendsWithPages(pagee, mAdapter);
//
//                                        }
//                                    });
//
//                                }
//                            });
//
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    prog.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onRndvuRequestTimeout(String message) {
//
//                }
//            });
        }
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

    private void getFriendsWithPages(int page, final FriendsListAdapter mAdapter)
    {
        if (name.equalsIgnoreCase("friends"))
        {
//            Rndvu.getClient().getFriends("", Rndvu.msisdn, String.valueOf(page), "10", true, "gt0", new RndvuRequestListener() {
//                @Override
//                public void beforeRndvuRequest() {
//
//                }
//
//                @Override
//                public void processRndvuResponse(RndvuResponse response) {
//
//                    try {
//                        String res = response.getBody();
//                        System.out.println("Get Friends PAGES " + res.toString());
//                        JSONObject jObj = new JSONObject(res);
//                        JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
//                        for (int i = 0; i < arr.length(); i++) {
//                            ArrayList<String> topics = new ArrayList<String>();
//                            JSONObject jj = arr.getJSONObject(i);
//                            count.add(String.valueOf(jj.getInt("countTopics")));
//                            items.add(jj.getString("name"));
//                            if(jj.has("msisdn")) {
//                                msisdn.add(String.valueOf(jj.getLong("msisdn")));
//                            }else{
//                                msisdn.add("");
//                            }
//
//                            JSONArray arrTopics = jj.getJSONArray("following");
//                            if (arrTopics.length() > 0) {
//                                for (int j = 0; j < arrTopics.length(); j++) {
//                                    topics.add(arrTopics.getString(j));
//                                }
//                            }
//                            topicsList.put(String.valueOf(i), topics);
//                            if(getActivity() != null){
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mAdapter.notifyItemInserted(items.size());
//                                    }
//                                });
//                            }
//
//                        }
//                        mAdapter.setLoaded();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    prog.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onRndvuRequestTimeout(String message) {
//
//                }
//            });

        }else
        if (name.equalsIgnoreCase("friendsNoTopics"))
        {
//            Rndvu.getClient().getFriends("", Rndvu.msisdn, String.valueOf(page), "10", true, "eq0",new RndvuRequestListener() {
//                @Override
//                public void beforeRndvuRequest() {
//
//                }
//
//                @Override
//                public void processRndvuResponse(RndvuResponse response) {
//
//                    try {
//                        String res = response.getBody();
//                        System.out.println("Get Friends PAGES " + res.toString());
//                        JSONObject jObj = new JSONObject(res);
//                        JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
//                        for (int i = 0; i < arr.length(); i++) {
//                            ArrayList<String> topics = new ArrayList<String>();
//                            JSONObject jj = arr.getJSONObject(i);
//                            count.add(String.valueOf(jj.getInt("countTopics")));
//                            items.add(jj.getString("name"));
//                            if(jj.has("msisdn")) {
//                                msisdn.add(String.valueOf(jj.getLong("msisdn")));
//                            }else{
//                                msisdn.add("");
//                            }
//                            JSONArray arrTopics = jj.getJSONArray("following");
//                            if (arrTopics.length() > 0) {
//                                for (int j = 0; j < arrTopics.length(); j++) {
//                                    topics.add(arrTopics.getString(j));
//                                }
//                            }
//
//                            topicsList.put(String.valueOf(i), topics);
//
//                            if(getActivity() != null){
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mAdapter.notifyItemInserted(items.size());
//                                    }
//                                });
//
//                            }
//
//                        }
//                        mAdapter.setLoaded();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    prog.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onRndvuRequestTimeout(String message) {
//
//                }
//            });

        }

        else {
//            Rndvu.getClient().getPeopleYouMayKnow("", Rndvu.msisdn, String.valueOf(page), "10", new RndvuRequestListener() {
//                @Override
//                public void beforeRndvuRequest() {
//
//                }
//
//                @Override
//                public void processRndvuResponse(RndvuResponse response) {
//
//                    try {
//                        String res = response.getBody();
//                        System.out.println("Get People PAGES " + res.toString());
//                        JSONObject jObj = new JSONObject(res);
//                        JSONArray arr = jObj.getJSONObject("data").getJSONArray("friends");
//                        for (int i = 0; i < arr.length(); i++) {
//                            ArrayList<String> topics = new ArrayList<String>();
//                            JSONObject jj = arr.getJSONObject(i);
//                            if (jj.has("countTopics")) {
//                                count.add(String.valueOf(jj.getInt("countTopics")));
//                            } else {
//                                count.add("0");
//                            }
//                            if (jj.has("name")) {
//                                items.add(jj.getString("name"));
//                            } else {
//                                items.add("");
//                            }
//
//                            if(jj.has("msisdn")) {
//                                msisdn.add(String.valueOf(jj.getLong("msisdn")));
//                            }else{
//                                msisdn.add("");
//                            }
//                            if (jj.has("following")) {
//                                JSONArray arrTopics = jj.getJSONArray("following");
//                                if (arrTopics.length() > 0) {
//                                    for (int j = 0; j < arrTopics.length(); j++) {
//                                        topics.add(arrTopics.getString(j));
//                                    }
//                                }
//                            } else {
//                                topics.add("");
//                            }
//                            topicsList.put(String.valueOf(i), topics);
//                            if(getActivity() != null){
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mAdapter.notifyItemInserted(items.size());
//                                    }
//                                });
//                            }
//
//                        }
//                        mAdapter.setLoaded();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    prog.setVisibility(View.GONE);
//
//                                }
//                            });
//                        }
//
//                    }
//
//
//                }
//
//                @Override
//                public void onRndvuRequestTimeout(String message) {
//
//                }
//            });

        }
    }
}