package dui.com.receipt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import dui.com.receipt.db.Photo;
import dui.com.receipt.db.ReceiptDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ReceiptDetailsActivity extends AppCompatActivity {
    public static String INTENT_EXTRA_RECEIPT_ID = "intentExtra.ReceiptDetailsActivity.receiptId";
    private View receiptDetailsView;
    private ReceiptDetailsImageAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long receiptId = getIntent().getLongExtra(INTENT_EXTRA_RECEIPT_ID, 0);
        receiptDetailsView = getLayoutInflater().inflate(R.layout.activity_receipt_details, null);
        setContentView(receiptDetailsView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView imageListView = findViewById(R.id.receipt_image_list_recyclerview);
        photoAdapter = new ReceiptDetailsImageAdapter(this);
        imageListView.setAdapter(photoAdapter);
        imageListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ReceiptDatabase.getInstance(this)
                .getPhotosByReceiptId(receiptId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Photo>>() {
                    @Override
                    public void accept(List<Photo> photos) throws Exception {
                        photoAdapter.updateItems(photos);
                    }
                });
    }
}