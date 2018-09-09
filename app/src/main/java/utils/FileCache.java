package utils;

import android.content.Context;

import java.io.File;

/**
 * Created by ravi on 29-Mar-17.
 */

public class FileCache {

    private File cacheDir;

    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TempImages");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

	/**
	 * create new file
     * @param url create cache file on disk with name as url hashcode
     * @return
     */
    public File getFile(String url){
        String filename=String.valueOf(Math.abs(url.hashCode()));
        File f = new File(cacheDir, filename);
        return f;

    }



}