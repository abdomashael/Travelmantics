package com.abdo_mashael.travelmantics.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdo_mashael.travelmantics.R;
import com.abdo_mashael.travelmantics.data.UserListItem;
import com.abdo_mashael.travelmantics.ui.adapters.UserItemRecyclerAdapter;
import com.abdo_mashael.travelmantics.ui.utils.DealsFetch;
import com.abdo_mashael.travelmantics.ui.utils.FirebaseUtil;
import com.abdo_mashael.travelmantics.ui.utils.SelectDeal;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserActivity extends AppCompatActivity implements DealsFetch, SelectDeal {

    private static final String TAG = UserActivity.class.getSimpleName();
    public static final String IS_NEW = "is new";
    public static final String DEAL = "Deal";
    private FirebaseUtil mFirebaseUtil;
    @BindView(R.id.userRecyclerView)
    RecyclerView userRecyclerView;
    private UserItemRecyclerAdapter mUserItemRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getString(R.string.app_name));

        mFirebaseUtil= FirebaseUtil.getInstance();
        mFirebaseUtil.login(this);
        mFirebaseUtil.getDeals(this);


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == FirebaseUtil.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //startActivity(SignedInActivity.createIntent(this, response));
                Log.d(TAG, "onActivityResult: "+response.toString());
                Toast.makeText(this,"login successfully",Toast.LENGTH_LONG).show();
                mFirebaseUtil.checkAdmin(FirebaseAuth.getInstance().getCurrentUser().getUid(),this);
                /*startActivity(new Intent(this,UserActivity.class));
                finish();*/
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    //  showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    // showSnackbar(R.string.no_internet_connection);
                    return;
                }

                //showSnackbar(R.string.unknown_error);
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);

        MenuItem insertMenu = menu.findItem(R.id.insertItem);
        if (FirebaseUtil.isIsAdmin()) {
            insertMenu.setVisible(true);
        }
        else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutItem) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task ->
                            startActivity(Intent.makeRestartActivityTask(getIntent().getComponent()))
                    );
            return true;
        }else if (id == R.id.insertItem){
            Intent mIntent =new Intent(UserActivity.this,AdminActivity.class);
            mIntent.putExtra(IS_NEW,true);

            startActivity(mIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void dealsReady(List<UserListItem> listItems, Boolean isRemove, int removedPosition) {
        if (mUserItemRecyclerAdapter == null) {
            userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            userRecyclerView.setAdapter(new UserItemRecyclerAdapter(this, listItems,this));
        }else if (isRemove){
            mUserItemRecyclerAdapter.notifyItemRemoved(removedPosition);
        }else {
            mUserItemRecyclerAdapter.notifyItemInserted(listItems.size() - 1);

        }
    }

    @Override
    public void onDealClick(UserListItem userListItem) {
        Intent mIntent= new Intent(this,AdminActivity.class);
        mIntent.putExtra(IS_NEW,false);
        mIntent.putExtra(DEAL,userListItem);
        startActivity(mIntent);
    }
}


