package com.udacity.nano_bookinglist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Books>> {
    public static final String QUERY = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final int LOADER_ID = 1;
    public String urlQuert;
    public BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    public Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Button Search = (Button) findViewById(R.id.SearchB);
        final LoaderManager loaderManager = getLoaderManager();
        bundle =new Bundle();
        bundle.putString("key","value");
        loaderManager.initLoader(LOADER_ID, bundle,this);

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkInfo != null && networkInfo.isConnected()) {

                    EditText et = (EditText) findViewById(R.id.SearchQ);
                    urlQuert = QUERY + et.getText().toString();

                    loaderManager.restartLoader(LOADER_ID, bundle, MainActivity.this);



                    ListView listView = (ListView) findViewById(R.id.list);
                    mAdapter = new BookAdapter(MainActivity.this, new ArrayList<Books>());
                    listView.setAdapter(mAdapter);
                    mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
                    listView.setEmptyView(mEmptyStateTextView);
                }
                else {
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText("no internet connection");
                }
            }

        });

    }

    @Override
    public Loader<List<Books>> onCreateLoader(int i, Bundle bundle) {
        if(bundle != null ){
            if(bundle.containsKey("key")) {
                return new BookLoader(this, null);
            }
        }
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        // Create a new loader for the given URL
        return new BookLoader(this, urlQuert);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> BookList) {
        mAdapter.clear();
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Clear the adapter of previous data
        if (BookList != null && !BookList.isEmpty()) {
            mAdapter.addAll(BookList);
        }
        else {
            mEmptyStateTextView.setText("no books were found");
        }
        Toast.makeText(this, "" + urlQuert, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}



