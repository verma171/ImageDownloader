package utils;

/**
 * Created by ravi on 29-Mar-17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sample.droidrank.com.droidrank.R;


/**
 * Class using Threadpool of 5 thread to load the images from Http url.
 * After downloading the image it smaple the image for memory optimization.
 */
public class AsyncImageLoader {

    private  int REQUIRED_SIZE = 0 ;
    private static AsyncImageLoader imageLoader = null;


   public static  AsyncImageLoader getInstance(Context context)
    {
        if(imageLoader == null)
        {
         return  imageLoader = new AsyncImageLoader(context);
        }
        else
            return imageLoader;
    }


    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    Context context ;
    ExecutorService executorService;

    private AsyncImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
        REQUIRED_SIZE=Utils.calculateGridWidth(context);
        this.context = context;
    }

	/**
     *
     * @param url loading image Url
     * @param imageView imageview holder;
     */
    int placeholder = R.drawable.noimage;
    public void DisplayImage(String url, ImageView imageView)
    {
        imageView.setTag(url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(placeholder);
        }
    }

	/**
     * Queue the image loading to threadpool
     * @param url
     * @param imageView
     */
    private void queuePhoto(String url, ImageView imageView)
    {
        Picture p=new Picture(url, imageView);
        executorService.submit(new PictureLoader(p));
    }

	/**
	 *
     * check image in exertnal storage if exist then return
     * else download the image from network
     * @param url image url
     * @return
     */
    private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;


        try {
            Bitmap bitmap=null;
            InputStream is=  new HttpHandler().makeImageRequest(url);
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //decode image to small sample.
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=Utils.calculateInSampleSize(o,REQUIRED_SIZE,Utils.dpToPx(context,100));
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

	/**
     * Class contains basic Image Properties.
     */
    private class Picture
    {
        public String url;
        public ImageView imageView;
        public Picture(String u, ImageView i){
            url=u;
            imageView=i;
        }

        public boolean isRecycled()
        {
            String actualUrl = imageView.getTag().toString();
            return !(url.equalsIgnoreCase(actualUrl));
        }
    }

	/**
     * Runnable to load the image from Cached Storage or Network,
     * It launch {@link PictureDisplayer} runnable on UI thread.
     */
    class PictureLoader implements Runnable {
        Picture photoToLoad;
        PictureLoader(Picture photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            if(imageViewRecycled(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewRecycled(photoToLoad))
                return;
            PictureDisplayer bd=new PictureDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

	/**
     * Function check ImageView is recycled by recyclerview if image is recycled then discard the resposne.
     * @param photoToLoad
     * @return
     */
    boolean imageViewRecycled(Picture photoToLoad){
        return photoToLoad.isRecycled();
    }

    //Used to display bitmap in the UI thread
    class PictureDisplayer implements Runnable
    {
        Bitmap bitmap;
        Picture photoToLoad;
        public PictureDisplayer(Bitmap b, Picture p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewRecycled(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(R.drawable.noimage);

        }
    }

}

