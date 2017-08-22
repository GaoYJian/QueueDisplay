package his.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class TestActivity extends Activity {
	NetworkStatistics ns;
	MyThread t1 = null;
	Handler myHandler = null;
	String networkSum = "";
	long StartTime,EndTime,Download,Upload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置不待机
		
		setContentView(R.layout.activity_test);
		TextView tv1 = (TextView) findViewById(R.id.textView2);
		tv1.setText(getInternetInstance() + "\n" + ShellExec());
		StartTime = 0;
		EndTime = 0;
		myHandler = new Handler() {
			public void handleMessage(Message msg) {
				NotifyTextView(null, 0);
				Refresh(null);
				GetNewWorkStatistic();
			}

		};
		ns = new NetworkStatistics();
		t1 = new MyThread();
		t1.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	private String getInternetInstance() {// 判断运营商网络类型
		ConnectivityManager nw = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = nw.getActiveNetworkInfo();
		if (netinfo == null) {
			return "当前网络不可用";
		} else {
			if (netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
				if (netinfo.isConnected()) {

					return "正在使用wifi网络\nSSID:"+ netinfo.getExtraInfo();
				}
			} else if (netinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String s = "正在使用运营商网络\n";
				switch (netinfo.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_CDMA:
					s += "电信2G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_EDGE:
					s += "移动2G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					s += "电信3G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					s += "电信3G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
					s += "电信3G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					s += "联通3G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_GPRS:
					if (GetSimAlpha() != null) {
						s += GetSimAlpha() + "2G\n";
					} else {
						s += "不能确定\n";
					}
					break;
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					s += "联通3G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
					s += "联通3G\n";
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					if (GetSimAlpha() != null) {
						s += GetSimAlpha() + "4G\n";
					} else {
						s += "不能确定\n";
					}
					break;
				default:
					s += "不能确定" + netinfo.getSubtype() + "\n";
					break;
				}
				return s;
			}
			return "无法识别";
		}
	}

	private String getInternetIp() {// 取IP
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	public String GetSum() {
		return networkSum;
	}

	public void Refresh(View view) {// 刷新
		TextView tv1 = (TextView) findViewById(R.id.textView2);
		String networkState = getInternetInstance();
		if (networkState.contains("不")) {
			networkSum = networkState;
		} else if (networkState.contains("wifi")) {
			networkSum = networkState + "\n" + GetWIFIState();
		} else {
			networkSum = networkState + "\n" + "Ip: " + getInternetIp() + "\n"
					+ "DNS: " + ShellExec();
		}
		NotifyTextView(networkSum, 1);
	}

	public void BackForward(View view) {// 退出
		Intent intent = new Intent();
		intent.setClass(TestActivity.this, HISQueueDisplayActivity.class);
		startActivity(intent);
		TestActivity.this.finish();
	}

	private String ShellExec() {// 获得DNS
		String re = "";
		try {
			Process localProcess = Runtime.getRuntime()
					.exec("getprop net.dns1");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					localProcess.getInputStream()));
			String line = null;

			int i = 0;
			while ((line = in.readLine()) != null) {
				i++;
				re += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re;
	}

	private String GetSimAlpha() {// SIM卡运营商
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		String operator = telManager.getSimOperator();

		if (operator != null) {

			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {

				return "移动";

			} else if (operator.equals("46001")) {

				return "联通";

			} else if (operator.equals("46003")) {

				return "电信";

			}
			return null;

		}
		return null;
	}

	private String GetWIFIState() {// wifi网络下状态信息
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		DhcpInfo di = wm.getDhcpInfo();
		String s = "";
		s += "Ip: " + getInternetIp() + "\n";
		s += "NetMask: " + FormatString(di.netmask) + "\n";
		s += "GateWay: " + FormatString(di.gateway) + "\n";
		s += "DNS: " + FormatString(di.dns1) + "\n";
		return s;
	}

	private void GetNewWorkStatistic() {
		long a[] = ns.updateStatisitc();
		if (StartTime == 0) {
			StartTime = a[8];
			EndTime = StartTime;
			Upload = a[6];
			Download = a[4];
		}
		String s = "";
		EndTime = a[8];
		if (StartTime == EndTime) {
			s += "下载: 0 Byte/s \n";
			s += "上传: 0 Byte/s \n";
		} else {
			s += "下载: " + (a[4]-Download)/((EndTime-StartTime)/1000) + " Byte/s \n";
			s += "上传: " + (a[6]-Upload)/((EndTime-StartTime)/1000) + " Byte/s \n";
		}
		StartTime=EndTime;
		Download = a[4];
		Upload = a[6];
		NotifyTextView(s, 2);
	}

	public String FormatString(int value) {
		String strValue = "";
		byte[] ary = intToByteArray(value);
		for (int i = ary.length - 1; i >= 0; i--) {
			strValue += (ary[i] & 0xFF);
			if (i > 0) {
				strValue += ".";
			}
		}
		return strValue;
	}

	public byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	public class MyThread extends Thread {
		public void run() {
			while (true) {
				myHandler.sendMessage(new Message());
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(TestActivity.this, HISQueueDisplayActivity.class);
		startActivity(intent);
		TestActivity.this.finish();
	}

	public void NotifyTextView(String s, int Type) {
		TextView tv1 = (TextView) findViewById(R.id.textView2);
		switch (Type) {
		case 0:
			tv1.setText("");
			break;
		case 1:
			tv1.setText(s + '\n');
			break;
		case 2:
			tv1.append('\n' + s + '\n');
			break;
		}
	}
}
