package com.rancard.rndvusdk.interfaces;

import com.rancard.rndvusdk.RendezvousLogActivity;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 4:02 PM
 * Package: com.rancard.rndvusdk.interfaces
 * Project: Rendezvous SDK
 */
public interface RendezvousClient
{


    void logActivity(long itemId,
                     String msisdn,
                     String userType,
                     String userId,
                     RendezvousLogActivity activity,
                     RendezvousRequestListener requestListener);


    void subscribe(String userType,
                     String user,
                     String action,
                     RendezvousRequestListener requestListener);

    void logActivity(final String userType,
                     final String userId,
                     final RendezvousLogActivity activity,
                     final RendezvousRequestListener requestListener);

    void createComment(
            long itemId,
            long parentId,
            String text,
            String authorType,
            String authorReference,
            RendezvousRequestListener requestListener);

    void getComments (
            long itemId,
            long parentId,
            long page,
            long limit,
            RendezvousRequestListener requestListener);

    void getComment (
            long itemId,
            long commentId,
            boolean showMinimalData,
            RendezvousRequestListener requestListener);

    void getItems(
            long page,
            long limit,
            String returnFields,
            RendezvousRequestListener requestListener);

    void getItems(
            long page,
            long limit,
            long categoryId,
            String returnFields,
            RendezvousRequestListener requestListener);

    void getTags(RendezvousRequestListener requestListener);

    void tagImages(
            String property,
            String msisdn,
            String email,
            long page,
            long limit,
            boolean abbreviate,
            long count,
            String[] tags,
            RendezvousRequestListener requestListener);


    void tagNews(String params,
                 long count,
                 String[] tags,
                 RendezvousRequestListener requestListener);


    void getNotifications(
            String email,
            long page,
            long limit,
            long abbreviate,
            boolean showRecommendation,
            RendezvousRequestListener requestListener);

   void getCategories(RendezvousRequestListener requestListener);

    void getCategoriesWithGenres(
            RendezvousRequestListener requestListener
    );

    void signUp(
            String password,
            String userId,
            String userType,
            String fullname,
            String avatarUrl,
            RendezvousRequestListener requestListener
    );


    void getItemsByTags(long page,
                        long limit,
                        String tags,
                        String returnFields,
                        RendezvousRequestListener requestListener);

    void getFriends(String msisdn,
                           int page,
                           int limit,
                           Boolean friendsOnly,
                           String topics,
                           RendezvousRequestListener requestListener);

    void getFriendsInGraph(String userId,
                           String userType,
                           int page,
                           int limit,
                           Boolean friendsOnly,
                           RendezvousRequestListener requestListener);

    void getTopics(String msisdn,
                          boolean countOnly,
                          RendezvousRequestListener requestListener);

    void signIn(String password,
                       String email,
                       RendezvousRequestListener requestListener);

    void getPeopleYouMayKnow(String msisdn,
                                    int page,
                                    int limit,
                                    RendezvousRequestListener requestListener);

    public void getItems(int page,
                         int limit,
                         String urll,
                         RendezvousRequestListener requestListener);

    void sendTopics(String msisdn,
                    String receiver,
                    String limit,
                    String tags,
                    RendezvousRequestListener requestListener);

    void getSearchItems(long page,
                        long limit,
                        String query,
                        String returnFields,
                        RendezvousRequestListener requestListener);


    void getFeaturedItems(String byCategories,
                          String returnFields,
                          RendezvousRequestListener requestListener);

    void getFriendsInApp(String email,
                         boolean registeredUsers,
                         RendezvousRequestListener requestListener);

    void uploadUserContacts(String email,
                                   String userId,
                                   String contactsJson,
                                   RendezvousRequestListener rndvuRequestListener);

    void getStagingResponse(RendezvousRequestListener
                                    requestListener);

    }
