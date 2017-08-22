package his.app;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WifiSwitchActivity extends Activity {

	TextView tv1;
	// TextView tv2;
	// TextView tv3;
	Handler myHandler = null;
	MyThread t1 = null;
	// Button btnSwitch;
	Intent startIntent;

	Button btnStartService, btnStopService;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_switch);

		btnStartService = (Button) findViewById(R.id.btnStartService);
		btnStopService = (Button) findViewById(R.id.btnStopService);
		tv1 = (TextView) findViewById(R.id.tv1);
		//connectWiFi("QLYT-AP", "88382088");连接指定的wifi网络
		
		AutoConnectWifi();
		btnStartService.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startIntent = new Intent();
				startIntent.setClass(WifiSwitchActivity.this,
						WifiSwitchService.class);
				startIntent.putExtra("test", "This is a Test");
				startService(startIntent);
				btnStartService.setEnabled(false);
				btnStopService.setEnabled(true);
			}
		});

		btnStopService.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				stopService(startIntent);
				btnStartService.setEnabled(true);
				btnStopService.setEnabled(false);
			}
		});

		myHandler = new Handler() {
			public void handleMessage(Message msg) {
				GetWifiState();
			}

		};
		t1 = new MyThread();
		t1.start();
		/*
		 * tv1 = (TextView) findViewById(R.id.tv1); tv2 = (TextView)
		 * findViewById(R.id.tv2); tv3 = (TextView) findViewById(R.id.tv3);
		 * btnSwitch = (Button) findViewById(R.id.btnSwitch);
		 * 
		 * btnSwitch.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View arg0) { SwitchNet(); }
		 * 
		 * });
		 * 
		 * myHandler = new Handler() { public void handleMessage(Message msg) {
		 * GetWifiState(); }
		 * 
		 * }; MyThread t1 = new MyThread(); t1.start();
		 */
	}

	public class MyThread extends Thread {
		public void run() {
			while (true) {
				myHandler.sendMessage(new Message());
				try {
					Thread.currentThread().sleep(2000);
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
		}
	}

	private void GetWifiState() {
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifi_info = wifi_service.getConnectionInfo();
		String s = "";

		String SSID = wifi_info.getSSID();
		int Rssi = wifi_info.getRssi() + 100;

		s += "BSSID : " + wifi_info.getBSSID() + "\n";
		s += "SSID : " + SSID + "\n";
		s += "Mac : " + wifi_info.getMacAddress() + "\n";
		s += "LinkSpeed : " + wifi_info.getLinkSpeed() + "\n";
		s += "IpAddress : "
				+ Formatter.formatIpAddress(wifi_info.getIpAddress()) + "\n";
		s += "RSSI : " + Rssi + "\n";// 其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线。

		tv1.setText(s);
		/*
		 * wifi_service.startScan(); // 得到扫描结果 List<ScanResult> l =
		 * wifi_service.getScanResults(); // 得到配置好的网络连接 List<WifiConfiguration>
		 * lg = wifi_service.getConfiguredNetworks();
		 */
		// if (SSID.length() > 0 && Rssi > 0 && Rssi < 29) {
		// SwitchNet();
		// }
	}

	public int AddWifiConfig(List<ScanResult> wifiList, String ssid, String pwd) {
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		int wifiId = -1;
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult wifi = wifiList.get(i);
			if (wifi.SSID.equals(ssid)) {
				WifiConfigLogic wl = new WifiConfigLogic(ssid, pwd,"192.168.1.132","192.168.1.1");
				WifiConfiguration w = wl.wificonfiguration;
				DeleteFromNetList(ssid);//删除冗余的网络配置（已存储的SSID信息）
				wifiId = wifi_service.addNetwork(w);// 将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
				if (wifiId != -1) {
					return wifiId;
				}
			}
		}
		return wifiId;
	}

	public void connectWiFi(String ssId, String password) {
		boolean wifiIsInScope = false;
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		for (int i = 0; i < wifi_service.getScanResults().size(); i++) {
			if (wifi_service.getScanResults().get(i).SSID.equals(ssId)) {
				wifiIsInScope = true;
			}
		}
		if (!wifiIsInScope) {
			return;
		}
		int netId = AddWifiConfig(wifi_service.getScanResults(), ssId, password);
		wifi_service.enableNetwork(netId, true);
	}

	public boolean DeleteFromNetList(String ssid) {
		boolean re = false;
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);

		List<WifiConfiguration> wcf = wifi_service.getConfiguredNetworks();

		for (int i = 0; i < wcf.size(); i++) {
			if (wcf.get(i).SSID.equals("\""+ssid+"\"")) {
				wifi_service.removeNetwork(wcf.get(i).networkId);
				re = true;
			}
		}

		return re;
	}
	
	public List<ScanResult> WifiScanResult(){
		List<String> re = new ArrayList<String>();
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		List<ScanResult> sr = wifi_service.getScanResults();
		
		Collections.sort(sr, new Comparator() {
            public int compare(Object a, Object b) {
                int one = ((ScanResult) a).level;
                int two = ((ScanResult) b).level;
                return two - one;
            }
        });
		
		return sr;
	}

	public void AutoConnectWifi(){
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		List<ScanResult> sr = WifiScanResult();
		List<WifiConfiguration> wcf = wifi_service.getConfiguredNetworks();
		boolean isOut = false;
		for(int i = 0 ; i < sr.size() ; i++){
			for(int j = 0 ; j < wcf.size() ;j++){
				if(wcf.get(j).SSID.equals("\"" + sr.get(i).SSID + "\"")){
					wifi_service.enableNetwork(wcf.get(j).networkId, true);
					isOut = true;
					break;
				}
			}
			if(isOut){
				break;
			}
		}
	}
}
