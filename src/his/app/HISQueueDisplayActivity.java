package his.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class HISQueueDisplayActivity extends Activity {

	private String webUrl = "";
	private WebView webView;
	private Button configBtn;
	private LinearLayout hideLayout;
	private Button exitBtn;
	private Button refreshBtn;
	private Boolean isConnected = false;

	private final Handler connectHandler = new Handler();
	private final Runnable connectTask = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			webView.loadUrl(webUrl);
			webView.loadUrl("javascript:androidCall()");
			if (isConnected == false) {
				connectHandler.postDelayed(this, 5000);// 未连接上网络，等待5秒后再连接
			} else {
				connectHandler.removeCallbacks(this);
			}
		}
	};

	private final Handler hideHandler = new Handler();
	private final Runnable hideTask = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			hideHandler.removeCallbacks(this);
			hideConfigLayout();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		int a = (6 >>> 9);
		webView = (WebView) findViewById(R.id.wv_display);
		configBtn = (Button) findViewById(R.id.btn_config);
		configBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(HISQueueDisplayActivity.this,
						DisplayConfigActivity.class);
				startActivity(intent);
				HISQueueDisplayActivity.this.finish();
			}
		});
		hideLayout = (LinearLayout) findViewById(R.id.hideRelayout);
		showConfigLayout();
		webView.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				showConfigLayout();
				return true;
			}
		});
		webView.addJavascriptInterface(this, "androidCallback");// (obj,

		//webView.getSettings().setJavaScriptEnabled(true);		
		// "runOnAndroidJavaScript")
		exitBtn = (Button) findViewById(R.id.btn_exit);
		exitBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitActivity();
			}
		});

		refreshBtn = (Button) findViewById(R.id.btn_refresh);
		refreshBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (webView != null) {
					webView.reload();
				}
			}
		});

		SharedPreferences sp = getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
		webUrl = sp.getString("DISPLAY_WEBSERVICE", "");

		if (webUrl.equals("")) {
			Intent intent = new Intent();
			intent.setClass(HISQueueDisplayActivity.this,
					DisplayConfigActivity.class);
			startActivity(intent);
			HISQueueDisplayActivity.this.finish();
		} else {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setWebViewClient(new WebViewClient());
			webView.loadUrl(webUrl);
		}
		/*
		 * // 设置网页展示窗口在App中，而不打开浏览器 webView.setWebViewClient(new WebViewClient()
		 * { public boolean shouldOverrideUrlLoading(WebView view, String url) {
		 * view.loadUrl(url); return true; } });
		 * 
		 * webView.loadUrl("javascript:androidCall()"); if (isConnected ==
		 * false) { connectHandler.postDelayed(connectTask, 5000); }
		 * 
		 * hideHandler.postDelayed(hideTask, 5000); }
		 */
	}

	private void hideConfigLayout() {
		LinearLayout.LayoutParams curParams = (LinearLayout.LayoutParams) hideLayout
				.getLayoutParams();
		curParams.height = 0;
		hideLayout.setLayoutParams(curParams);
	}

	private void showConfigLayout() {
		LinearLayout.LayoutParams curParams = (LinearLayout.LayoutParams) hideLayout
				.getLayoutParams();
		curParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		hideLayout.setLayoutParams(curParams);

		hideHandler.postDelayed(hideTask, 5000);
	}

	private void exitActivity() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								HISQueueDisplayActivity.this.finish();
								System.exit(0);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitActivity();
		}
		return true;

	}

	public void setConnect() {
		isConnected = true;
	}

	public void ToTestActivity(View view) {
		Intent intent = new Intent();
		intent.setClass(HISQueueDisplayActivity.this, WifiSwitchActivity.class);
		startActivity(intent);
		HISQueueDisplayActivity.this.finish();
	}

}