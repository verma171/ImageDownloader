package utils;

import org.json.JSONObject;

/**
 * Created by aniruddh.rathore on 3/29/17.
 */

/**
 * Model class for Image API response.
 */
public class Response
{

	int pageCount;

	boolean isError;

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	JSONObject data;

	public Response(boolean isError,JSONObject data,int pageCount)
	{
		this.isError = isError;
		this.data = data;
		this.pageCount = pageCount;
	}
}
