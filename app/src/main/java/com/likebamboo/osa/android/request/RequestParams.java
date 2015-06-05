/*
 * Created by Storm Zhang, Feb 13, 2014.
 */

package com.likebamboo.osa.android.request;

import java.util.HashMap;

public class RequestParams extends HashMap<String, String> {
	private static final long serialVersionUID = 8112047472727256876L;

	public RequestParams add(String key, String value) {
		put(key, value);
		return this;
	}
}
