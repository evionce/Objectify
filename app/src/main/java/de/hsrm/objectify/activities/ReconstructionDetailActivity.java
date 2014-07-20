package de.hsrm.objectify.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import de.hsrm.objectify.R;
import de.hsrm.objectify.activities.fragments.IReconstructionFragment;
import de.hsrm.objectify.activities.fragments.ImageViewerFragment;
import de.hsrm.objectify.activities.fragments.ModelViewerFragment;
import de.hsrm.objectify.camera.Constants;
import de.hsrm.objectify.database.DatabaseAdapter;
import de.hsrm.objectify.database.DatabaseProvider;
import de.hsrm.objectify.rendering.ReconstructionService;
import de.hsrm.objectify.utils.Storage;

/**
 * An activity representing a single Reconstruction detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ReconstructionListActivity}.
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link de.hsrm.objectify.activities.fragments.ImageViewerFragment}.
 */
public class ReconstructionDetailActivity extends Activity
        implements IReconstructionFragment.OnFragmentInteractionListener {

    private final String TAG = "ReconstructionDetailActivity";
    private SpinnerAdapter mSpinnerAdapter;
    private IReconstructionFragment mCurrentFragment;
    private Bitmap heights;
    private Bitmap normals;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "inside BroadcastReceiver");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String galleryId = bundle.getString(ReconstructionService.GALLERY_ID);
                ContentResolver cr = getContentResolver();
                Uri galleryItemUri = DatabaseProvider.CONTENT_URI.buildUpon()
                        .appendPath("gallery").build();
                Cursor c = cr.query(galleryItemUri, null, DatabaseAdapter.GALLERY_ID_KEY + "=?",
                        new String[] { galleryId }, null);
                c.moveToFirst();
                // Storage.getExternalRootDirectory() + "/" + dirName + "/model.kaw";
                String dirName = c.getString(DatabaseAdapter.GALLERY_IMAGE_PATH_COLUMN);
                String objectId = c.getString(DatabaseAdapter.GALLERY_OBJECT_ID_COLUMN);
                c.close();

                heights = BitmapFactory.decodeFile(
                        Storage.getExternalRootDirectory() + "/" + dirName + "/heights.png");
                normals = BitmapFactory.decodeFile(
                        Storage.getExternalRootDirectory() + "/" + dirName + "/normals.png");

                Log.i(TAG, "update current fragment");
                mCurrentFragment.update(null, heights, normals);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconstruction_detail);

        mSpinnerAdapter = new ArrayAdapter<CharSequence>(this, R.layout.subtitled_spinner_item,
                android.R.id.text1, getResources().getStringArray(R.array.reconstruction_views));

        /* show the Up button in the action bar. */
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, spinnerNavigationCallback());

        /* savedInstanceState is non-null when there is fragment state saved from previous
         * configurations of this activity (e.g. when rotating the screen from portrait to
         * landscape). In this case, the fragment will automatically be re-added to its container
         * so we don't need to manually add it */
        if (savedInstanceState == null) {
            /* create the detail fragment and add it to the activity using a fragment transaction */
            mCurrentFragment = new ImageViewerFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.reconstruction_detail_container, mCurrentFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "registerReceiver");
        registerReceiver(receiver, new IntentFilter(ReconstructionService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "unregisterReceiver");
        unregisterReceiver(receiver);
    }

    private ActionBar.OnNavigationListener spinnerNavigationCallback() {
        return new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                switch (itemPosition) {
                    case Constants.ReconstructionType.NORMALMAP:
                    case Constants.ReconstructionType.HEIGHTMAP:
                        mCurrentFragment = new ImageViewerFragment();
                        break;
                    case Constants.ReconstructionType.RECONSTRUCTION:
                        mCurrentFragment = new ModelViewerFragment();
                        break;
                }

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.reconstruction_detail_container, mCurrentFragment);
                transaction.commit();

                /* update current active fragment */
                mCurrentFragment.update(null, heights, normals);
                return true;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* inflate the menu; this adds items to the action bar, if it is present */
        getMenuInflater().inflate(R.menu.reconstruction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_about:
                return true;
            case android.R.id.home:
                /* this ID represents the Home or Up button. In the case of this activity, the Up
                 * button is shown. Use NavUtils to allow users to navigate up one level in the
                 * application structure */
                NavUtils.navigateUpTo(this, new Intent(this, ReconstructionListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
