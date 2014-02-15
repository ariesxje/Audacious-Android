package com.Audacious.audacious;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	public ImageView ssView;
	private WebView webview;
	private SpinningOnlyProgressDialog progress;
	private boolean isOffline = false;
	private String url;
	private Menu menu;
	
	private MenuItem stopRefreshItem;
	private MenuItem backItem;
	private MenuItem forwardItem;
	private boolean refresh = false;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ssView = (ImageView)findViewById(R.id.imageView1);
		
		webview = (WebView)findViewById(R.id.homepage);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		if (!isNetworkConnected()) {
			isOffline = true;
			url = getString(R.string.localUrl);
			dismissSSView();
		}
		else {
			isOffline = false;
			url = getString(R.string.url);
			webview.setWebViewClient(new WebViewClient() {
				boolean timeout = true;
	 			
	 			public void onPageStarted(WebView view, String url, Bitmap favicon) {
	 				refresh = false;
	 				if (menu != null) {
	 					stopRefreshItem.setIcon(R.drawable.ic_menu_stop);
	 					updateIcons();
	 				}
//	 		 		webview.loadUrl("javascript:window.onload=function(){Mobi2Go.onReady(function(){alert('GFHJFHGJFG')})}");
//	 				Log.i(TAG, "started");
	 				if (!isNetworkConnected() && !isOffline) {
//	 					showSpinningDialog();
	 					Runnable run = new Runnable() {
							public void run() {
								if (timeout) {
									alert("error","reload");
//									startActivity(getIntent());
									webview.loadUrl(getString(R.string.localUrl));
								}
							}
	 					};
	 					Handler myHandler = new Handler();
	 					myHandler.postDelayed(run,10000);
	 				}
	 				if (!url.startsWith(getString(R.string.url))) {
	 					openBrowser(url);
	 				}
	 			}
	 			
	 			public void onPageFinished(WebView view, String url) {
//	 				Log.v(TAG, "loaded");
//	 				CookieSyncManager.getInstance().sync();
	 				if (menu != null) {
	 					updateIcons();
 						stopRefreshItem.setIcon(R.drawable.ic_menu_refresh);
	 				}
					refresh = true;
	 				if (isNetworkConnected() || isOffline) {
//	 					dismissSpinningDialog();
	 					dismissSSView();
	 					timeout = false;
	 				}
	 		    }
//	 			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//	 				alert("Oops",description);
//	 				if (isNetworkConnected())
//	 					dismissSpinningDialog();
//	 				super.onReceivedError(view, errorCode, description, failingUrl);
//	 			}
	 			
	 		    public boolean shouldOverrideUrlLoading(WebView view, String url){
	 		        Log.i("url", url);
	 		        if (!url.startsWith(getString(R.string.url)) && !isOffline) {
	 		        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	 					startActivity(browserIntent);
	 					return true;
	 		        }
	 		    	return false;
	 		    }
			});
		}
		webview.loadUrl(url);
		Log.i("url = ",url);
		webview.setWebChromeClient(new WebChromeClient());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		stopRefreshItem = menu.findItem(R.id.stop);
		backItem = menu.findItem(R.id.goBack);
		forwardItem = menu.findItem(R.id.goForward);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { //this method is used for handling menu items' events
		int itemId = item.getItemId();
		if (itemId == R.id.goBack) {
			if(webview.canGoBack())
				webview.goBack();
			updateIcons();
			return true;
		} else if (itemId == R.id.goForward) {
			if(webview.canGoForward())
				webview.goForward();
			updateIcons();
			return true;
		} else if (itemId == R.id.stop) {
			if (refresh)
				webview.reload();
			else
				webview.stopLoading();
			return true;
		} else if (itemId == R.id.openInBrowser) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webview.getOriginalUrl()));
			startActivity(browserIntent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	        webview.goBack();
	        return true;
	    }
	    // If it wasn't the Back key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}
	
	private void updateIcons() {
		if (webview.canGoForward())
			forwardItem.setEnabled(true);
		else
			forwardItem.setEnabled(false);
		if (webview.canGoBack())
			backItem.setEnabled(true);
		else
			backItem.setEnabled(false);
			
	}
	
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		}
		else
			return true;
	}
	
	public void showSpinningDialog() {
		progress = SpinningOnlyProgressDialog.show(this, null, null, true, false, null);
	}
	
	public void dismissSpinningDialog() {
		progress.dismiss();
	}

	public void dismissSSView() {
		ssView.setVisibility(View.GONE);
	}
	
	public void alert(final String title, final String message) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(title);
		alertBuilder
			.setMessage(message)
			.setCancelable(true)
			.setNeutralButton("Try again", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		AlertDialog alertDialog = alertBuilder.create();
		alertDialog.show();
	}
	
	public void openBrowser(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

}
