package his.app;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class WifiConfigLogic {

	public static WifiConfiguration wificonfiguration;

	/*
	 * 普通构造函数
	 * @param 
	 * SSID wifi对应的名称
	 * pwd 密码
	 */
	public WifiConfigLogic(String SSID, String pwd) {
		wificonfiguration = new WifiConfiguration();
		wificonfiguration.SSID = "\"" + SSID + "\"";// \"转义字符，代表"
		wificonfiguration.preSharedKey = "\"" + pwd + "\"";// WPA-PSK密码
		wificonfiguration.hiddenSSID = false;
		wificonfiguration.status = WifiConfiguration.Status.ENABLED;
		//SetWifiAdvance();
	}

	/*
	 * 带高级设置的构造函数
	 * @param
	 * SSID wifi名称
	 * pwd 密码
	 * IpAddress 静态IP地址
	 * GateWay 网关地址
	 */
	public WifiConfigLogic(String SSID,String pwd,String IpAddress,String GateWay){
		wificonfiguration = new WifiConfiguration();
		wificonfiguration.SSID = "\"" + SSID + "\"";// \"转义字符，代表"
		wificonfiguration.preSharedKey = "\"" + pwd + "\"";// WPA-PSK密码
		wificonfiguration.hiddenSSID = false;
		wificonfiguration.status = WifiConfiguration.Status.ENABLED;
		SetWifiAdvance(IpAddress,GateWay);
	}
	
	/*
	 * 高级设置
	 * @param
	 * ip 静态ip地址
	 * GateWay 网关地址
	 */
	public void SetWifiAdvance(String ip,String GateWay) {
		try{
			int networkPrefixLength = 24;
			InetAddress intetAddress = InetAddress.getByName(ip);
			int intIp = inetAddressToInt(intetAddress);
			//String dns = (intIp & 0xFF) + "." + ((intIp >> 8) & 0xFF) + "."	+ ((intIp >> 16) & 0xFF) + ".1";
			setIpAssignment("STATIC", wificonfiguration); // "STATIC" or "DHCP"
			setIpAddress(intetAddress, networkPrefixLength, wificonfiguration);
			setGateway(InetAddress.getByName(GateWay), wificonfiguration);
			setDNS(InetAddress.getByName(GateWay), wificonfiguration);//用网关作为DNS地址
		} catch (Exception e) {

		}
	}

	private static void setIpAddress(InetAddress addr, int prefixLength,
			WifiConfiguration wifiConf) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException,
			InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;
		Class laClass = Class.forName("android.net.LinkAddress");
		Constructor laConstructor = laClass.getConstructor(new Class[] {
				InetAddress.class, int.class });
		Object linkAddress = laConstructor.newInstance(addr, prefixLength);
		ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties,
				"mLinkAddresses");
		mLinkAddresses.clear();
		mLinkAddresses.add(linkAddress);
	}

	public static void setGateway(InetAddress gateway,
			WifiConfiguration wifiConf) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, InstantiationException,
			InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;
		Class routeInfoClass = Class.forName("android.net.RouteInfo");
		Constructor routeInfoConstructor = routeInfoClass
				.getConstructor(new Class[] { InetAddress.class });
		Object routeInfo = routeInfoConstructor.newInstance(gateway);
		ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties,
				"mRoutes");
		mRoutes.clear();
		mRoutes.add(routeInfo);
	}

	public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;
		ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(
				linkProperties, "mDnses");
		mDnses.clear(); // or add a new dns address , here I just want to
						// replace DNS1
		mDnses.add(dns);
	}

	public static void setIpAssignment(String assign, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		setEnumField(wifiConf, assign, "ipAssignment");
	}

	public static void setEnumField(Object obj, String value, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
	}

	private static Object getField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		Object out = f.get(obj);
		return out;
	}

	private static Object getDeclaredField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		Object out = f.get(obj);
		return out;
	}

	public static int inetAddressToInt(InetAddress inetAddr)
			throws IllegalArgumentException {
		byte[] addr = inetAddr.getAddress();
		if (addr.length != 4) {
			throw new IllegalArgumentException("Not an IPv4 address");
		}
		return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16)
				| ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
	}
}
