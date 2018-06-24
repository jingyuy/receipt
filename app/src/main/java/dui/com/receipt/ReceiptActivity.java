package dui.com.receipt;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dui.com.receipt.db.Photo;
import dui.com.receipt.db.ReceiptDatabase;
import io.reactivex.functions.Consumer;

public class ReceiptActivity extends AppCompatActivity implements
        ReceiptPhotoItemTouchHelper.RecyclerItemTouchHelperListener{
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private View receiptView;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String currentPhotoPath;
    private MenuItem saveMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiptView = getLayoutInflater().inflate(R.layout.activity_receipt, null);
        setContentView(receiptView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.receipt_image_list);
        layoutManager = new LinearLayoutManager(this);
        adapter = new PhotoAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePhotoIntent();
            }
        });
        if (savedInstanceState == null) {
            dispatchTakePhotoIntent();
        }
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ReceiptPhotoItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receipt, menu);
        saveMenuItem = menu.getItem(0);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (saveMenuItem != null) {
                saveMenuItem.setVisible(true);
            }
            addPhotoToGallery();
            adapter.addItem(new Photo(currentPhotoPath, Calendar.getInstance().getTime()));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                saveReceiptPhotos();
                return true;
            case R.id.action_cancel:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveReceiptPhotos() {
        ReceiptDatabase.getInstance(this).saveReceiptPhotos(adapter.getItems()).subscribe(new Consumer<List<Long>>() {
            @Override
            public void accept(List<Long> aVoid) throws Exception {
                new ProcessReceiptsAsyncTask(getApplication()).execute(null, null, null);
            }
        });
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "dui.com.receipt.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
                Snackbar.make(receiptView, R.string.failed_to_create_image_file, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof PhotoAdapter.ViewHolder) {
            // backup of removed item for undo purpose
            final Photo deletedItem = adapter.getItem(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.remove(viewHolder.getAdapterPosition());
            if (adapter.getItemCount() <=0 && saveMenuItem != null) {
                saveMenuItem.setVisible(false);
            }

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(receiptView, R.string.removed_from_receipt, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restore(deletedItem, deletedIndex);
                    if (saveMenuItem != null) {
                        saveMenuItem.setVisible(true);
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
