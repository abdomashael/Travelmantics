package com.abdo_mashael.travelmantics.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abdo_mashael.travelmantics.R;
import com.abdo_mashael.travelmantics.data.UserListItem;
import com.abdo_mashael.travelmantics.ui.utils.FirebaseUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 122;
    private static final String TAG = AdminActivity.class.getSimpleName();
    @BindView(R.id.titleText)
    EditText titleText;
    @BindView(R.id.descriptionText)
    EditText descriptionText;
    @BindView(R.id.priceText)
    EditText priceText;
    @BindView(R.id.imageBtn)
    Button imageBtn;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.uploadLayout)
    LinearLayout uploadLayout;
    private Uri mDownloadUri;
    private String mDealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setTitle(getString(R.string.app_name));
        if (!getIntent().getBooleanExtra(UserActivity.IS_NEW, true)) {

            UserListItem mUserListItem = getIntent().getParcelableExtra(UserActivity.DEAL);
            mDealId = mUserListItem.getId();
            titleText.setText(mUserListItem.getTvTitle());
            descriptionText.setText(mUserListItem.getTvDescription());
            priceText.setText(mUserListItem.getTvPrice());
            mDownloadUri = Uri.parse(mUserListItem.getTvImageUrl());
            Glide.with(this).load(mDownloadUri).into(imageView);
        }

        if (FirebaseUtil.isIsAdmin())
            enableEditTexts(true);
        else
            enableEditTexts(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            uploadLayout.setVisibility(View.VISIBLE);
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.getInstance().getStorageRef().child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mDownloadUri = task.getResult();
                    Log.d("Url: ", mDownloadUri.toString());
                    Glide.with(AdminActivity.this).load(mDownloadUri).into(imageView);
                } else {
                    // Handle failures
                    // ...
                }

                uploadLayout.setVisibility(View.GONE);
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        MenuItem saveItem = menu.findItem(R.id.saveItem);
        MenuItem deleteItem = menu.findItem(R.id.deleteItem);
        if (FirebaseUtil.isIsAdmin()) {
            saveItem.setVisible(true);
            deleteItem.setVisible(true);
        } else {
            deleteItem.setVisible(false);
            saveItem.setVisible(false);
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
        if (id == R.id.saveItem) {
            if (titleText.getText().toString().equals("")) {
                Toast.makeText(this, "Enter Title", Toast.LENGTH_LONG).show();
            } else if (descriptionText.getText().toString().equals("")) {
                Toast.makeText(this, "Enter Description", Toast.LENGTH_LONG).show();
            } else if (priceText.getText().toString().equals("")) {
                Toast.makeText(this, "Enter Price", Toast.LENGTH_LONG).show();

            } else if (mDownloadUri == null) {
                Toast.makeText(this, "Select Image", Toast.LENGTH_LONG).show();

            } else {
                UserListItem mDeal = new UserListItem(titleText.getText().toString(),
                        descriptionText.getText().toString(),
                        priceText.getText().toString(),
                        mDownloadUri.toString());
                if (mDealId != null) {
                    mDeal.setId(mDealId);
                    FirebaseUtil.addDeal(mDeal,mDealId);
                } else
                    FirebaseUtil.addDeal(mDeal,"");

                finish();
            }
            return true;
        } else if (id == R.id.deleteItem) {
            FirebaseUtil.getDatabaseReference().child(mDealId).removeValue();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.imageBtn)
    public void onViewClicked() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent.createChooser(intent,
                "Insert Picture"), PICTURE_RESULT);
    }

    private void enableEditTexts(boolean isEnabled) {
        titleText.setEnabled(isEnabled);
        descriptionText.setEnabled(isEnabled);
        priceText.setEnabled(isEnabled);
        imageBtn.setEnabled(isEnabled);
    }
}
