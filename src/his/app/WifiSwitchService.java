package his.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.widget.Toast;

public class WifiSwitchService extends Service{

	Handler myHandler = null;
	MyThread t1 = null;
	String s = null;
	boolean isStop = false;
	
	@Override
	public void onCreate(){
		super.onCreate();
		myHandler = new Handler() {
			public void handleMessage(Message msg) {
				 AutoConnectWifi();
				//GetWifiState();
				/*
				Toast toast = Toast.makeText(getApplicationContext(),
					     s, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				*/
			}

		};
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		isStop = false;
		t1 = new MyThread();
		t1.start();
		s = intent.getStringExtra("test");
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDestroy(){
		isStop = true;
		super.onDestroy();
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public class MyThread extends Thread {
		public void run() {
			while (!isStop) {
				myHandler.sendMessage(new Message());
				try {
					Thread.currentThread().sleep(5000);
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
		
		/*
		s += "BSSID : " + wifi_info.getBSSID() + "\n";
		s += "SSID : " + SSID + "\n";
		s += "Mac : " + wifi_info.getMacAddress() + "\n";
		s += "LinkSpeed : " + wifi_info.getLinkSpeed() + "\n";
		s += "IpAddress : "
				+ Formatter.formatIpAddress(wifi_info.getIpAddress()) + "\n";
		s += "RSSI : " + Rssi + "\n";// 其中0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线。

		tv1.setText(s);
		*/
		
		/*
		 * wifi_service.startScan(); // 得到扫描结果 List<ScanResult> l =
		 * wifi_service.getScanResults(); // 得到配置好的网络连接 List<WifiConfiguration>
		 * lg = wifi_service.getConfiguredNetworks();
		 */
		if (SSID.length() > 0 && Rssi > 0 && Rssi < 29) {
			SwitchNet();
		}
	}
		
	//重启wifi，令其自动重新连接信号较强的wifi
	private void SwitchNet(){
		WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifi_service.isWifiEnabled()) {
			wifi_service.setWifiEnabled(false);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!wifi_service.isWifiEnabled()) {
			wifi_service.setWifiEnabled(true);
		}
	}

	//连接当前信号最强的wifi
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

}
