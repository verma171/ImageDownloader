package sample.droidrank.com.droidrank;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import utils.HttpHandler;
import utils.ImageAdapter;
import utils.ImageInfo;
import utils.MyWorkerThread;
import utils.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 20;
    private static final int REQUEST_CODE = 112;
    public static final int GRID_COLUMN = 3;
    Handler mhandler;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    boolean isLoading = false;
    int pageCount = 1;
    MyWorkerThread mworkerThread;
    Handler mUiHandler = new Handler();
    ImageAdapter imageadapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mhandler = new Handler(Looper.getMainLooper());


        mworkerThread = new MyWorkerThread("droidthread");
        mworkerThread.start();
        mworkerThread.prepareHandler();


        initUI();

        loadPhotoList();
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar)findViewById(R.id.pbHeaderProgress);
        layoutManager = new GridLayoutManager(this, GRID_COLUMN);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.setAdapter(imageadapter);
    }


    private void loadPhotoList()
    {

        if(!isNetworkAvailable())
        {
            Toast.makeText(this,"No Internet Connection! Check your connection and try again!!",Toast.LENGTH_LONG).show();
            showProgress(false);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                 loadMoreItems(pageCount);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

            }
        }
        else
        {
            loadMoreItems(pageCount);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
           loadMoreItems(pageCount);
        }
    }




    // RecyclerView listerner for on demanding loading of request.
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    isLoading = true;
                    showProgress(isLoading);
                    loadMoreItems(++pageCount);
                }
            }
        }
    };

	/**
     * send api request.
     * @param pageCount
     */
    private void loadMoreItems(final int pageCount) {


        mworkerThread.postTask(new Runnable() {
            @Override
            public void run() {
                final Response response = new HttpHandler().makeServiceCall(pageCount);

                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading=false;
                        showProgress(isLoading);
                        LoadData(response.getData());
                    }
                });
            }
        });
    }


	/**
	 * set data to recyclerview adapter.
     * @param response
     */
    public void LoadData(JSONObject response)
    {
        if(response == null)
            return;

        JSONArray jsonArray  = null;
        try {
            jsonArray = response.getJSONArray("images");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        List<ImageInfo> list= new ArrayList<ImageInfo>();
        //convert data to model;
        if(jsonArray != null && jsonArray.length()> 0)
        {
            for(int count=0; count<jsonArray.length(); count++)
            {
                JSONObject object =  jsonArray.optJSONObject(count);
                if(object != null)
                {
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setImageUrl(object.optString("imageUrl"));
                    imageInfo.setImageDescription(object.optString("imageDescription"));
                    list.add(imageInfo);
                }
            }
        }

        if(imageadapter == null) {
            imageadapter = new ImageAdapter(list,this);
            recyclerView.setAdapter(imageadapter);
        }
        else
        {
            imageadapter.setMjsonArray(pageCount,list);
        }
    }

    private void showProgress(boolean isLoading)
    {
        if(isLoading)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
