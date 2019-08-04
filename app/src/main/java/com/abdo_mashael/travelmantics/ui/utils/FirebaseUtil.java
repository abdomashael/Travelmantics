package com.abdo_mashael.travelmantics.ui.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abdo_mashael.travelmantics.data.UserListItem;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    private static final String TAG = FirebaseUtil.class.getSimpleName();


    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static FirebaseAuth sAuth;

    public static final int RC_SIGN_IN = 123;
    private static FirebaseUtil firebaseUtil;
    private static boolean isAdmin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    public static FirebaseUtil getInstance() {
        if (firebaseUtil == null)
            firebaseUtil = new FirebaseUtil();

        return firebaseUtil;
    }

    public static boolean isIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        FirebaseUtil.isAdmin = isAdmin;
    }

    public static DatabaseReference getDatabaseReference() {
        return sDatabaseReference;
    }

    public StorageReference getStorageRef() {
        return mStorageRef;
    }

    public FirebaseStorage getStorage() {
        return mStorage;
    }

    public void login(AppCompatActivity activity) {

        sAuth = FirebaseAuth.getInstance();

        //   mAuthListener = firebaseAuth -> {
        //  Log.d(TAG, "login: "+firebaseAuth.getCurrentUser());
        //  if (firebaseAuth.getCurrentUser() == null) {
        // Choose authentication providers
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );

            // Create and launch sign-in intent
            activity.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
           /* } else {
                String userId = firebaseAuth.getUid();
                checkAdmin(userId,activity);
            }*/
        }
        // };
        storageLink();
        databaseLink("Deals");

    }

    private void databaseLink(String ref) {
        if (sFirebaseDatabase == null)
            sFirebaseDatabase = FirebaseDatabase.getInstance();
        sDatabaseReference = sFirebaseDatabase.getReference().child(ref);


    }

    private void storageLink() {
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }

    public void checkAdmin(String userId, AppCompatActivity activity) {
        DatabaseReference ref = sFirebaseDatabase.getReference().child("administrators")
                .child(userId);
/*
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseUtil.isAdmin = true;
                activity.invalidateOptionsMenu();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
*/
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();
                Log.d(TAG, "onDataChange: " + value);

                if (value != null)
                    FirebaseUtil.isAdmin = true;
                else
                    isAdmin = false;
                activity.invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void getDeals(DealsFetch dealsFetch) {
        List<UserListItem> deals = new ArrayList<>();
        sDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserListItem value = dataSnapshot.getValue(UserListItem.class);
                if (value.getId() == null) {
                    value.setId(dataSnapshot.getKey());
                }
                deals.add(value);
                dealsFetch.dealsReady(deals, false, -1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getDeals(dealsFetch);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                UserListItem mUserListItem = dataSnapshot.getValue(UserListItem.class);
                if (mUserListItem.getId() == null) {
                    mUserListItem.setId(dataSnapshot.getKey());
                }
                Log.d(TAG, "user: " + mUserListItem);

                for (UserListItem deal : deals) {
                    if (deal.getId().equals(mUserListItem.getId())) {
                        int pos = deals.indexOf(deal);
                        deals.remove(deal);
                        dealsFetch.dealsReady(deals, true, pos);
                        Log.d(TAG, "onChildRemoved: " + mUserListItem);
                        break;
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void addDeal(UserListItem deal, String mDealId) {
        if (mDealId.equals(""))
            sDatabaseReference.push().setValue(deal);
        else
            sDatabaseReference.child(mDealId).setValue(deal);
    }


}
