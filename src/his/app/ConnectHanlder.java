package his.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectHanlder {
	
	private InetAddress ip;
	private String regx1 = "(http://|https://)?([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}){1}(:[0-9]{1,4}?/?.*)?";
	private String regx2 = "(http://|https://)?(\\w+(\\.\\w+){1,3}){1}(/?.*)?";//正则表达式
    private String[] reg = {"regx1","regx2"};
	
	public ConnectHanlder() {
	}

	private boolean SetUrl(String s) {//对Url进行处理
		Pattern pattern = Pattern.compile(regx2);
		Matcher matcher = pattern.matcher(s);
		boolean b = matcher.matches();

		if (b) {
			try {
				ip = InetAddress.getByName(matcher.group(2));
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				ip = null;
				return false;
			}
		} else {
			ip = null;
			return false;
		}
	}

	public String GetIp() {//获得当前Ping的IP
		if(ip == null){
			return null;
		}
		return ip.getHostAddress();
	}

	public boolean isTouchable2() {//不用
		try {
			boolean a = ip.isReachable(1000);
			boolean b = ip.isReachable(1000);
			boolean c = ip.isReachable(1000);

			return a == false ? (b == false ? false : (c == false ? false
					: true))
					: (b == false ? (c == false ? false : true) : true);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isTouchable(String ipAddress){//给定已处理过的URL 进行Ping
		BufferedReader in = null;  
		Runtime r = Runtime.getRuntime(); 
		String pingCommand = "ping -c 3 " + ipAddress;  
		try {   
			Process p = r.exec(pingCommand);   
			if (p == null) {    
				return false;   
			}   
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
			int connectedCount = 0;   
			String line = null;  
			while ((line = in.readLine()) != null){    
				connectedCount += line.contains("ttl")==true?1:0;   
			}
			return connectedCount >= 2;  } 
		catch (Exception ex) {   
			ex.printStackTrace();
			return false;  
		}
		finally {   
			try {    
				in.close();   
			} 
			catch (IOException e) {
				e.printStackTrace();   
			}
		}
	}
    
	public boolean selfTouchableTest(){//ping本机
		try {
			InetAddress sip = InetAddress.getLocalHost();
			return isTouchable(sip.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			ip = null;
			return false;
		}
	}

	public boolean Ping(String s){//给定网址测试是否Ping通
		if(SetUrl(s)){
			return isTouchable(ip.getHostAddress());
		}
		else{
			return false;
		}
	}
}
