package com.example.uva_app.utils;

public interface IResponse {
	
	/**
	 * process http request response.
	 * @param respStr response content.
	 */
	void onResponse(Object respContent);

}
