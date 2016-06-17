package his.app;

import java.util.Date;

import android.net.TrafficStats;

public class NetworkStatistics {
	long MobileStatistics[];
	
	public NetworkStatistics(){
		MobileStatistics = new long[9];
	}
	
	public long[] updateStatisitc(){
		MobileStatistics[0] = TrafficStats.getMobileRxBytes();//数据下载字节
		MobileStatistics[1] = TrafficStats.getMobileRxPackets();//数据下载包
		MobileStatistics[2] = TrafficStats.getMobileTxBytes();//数据上传字节
		MobileStatistics[3] = TrafficStats.getMobileTxPackets();//数据上传包
		MobileStatistics[4] = TrafficStats.getTotalRxBytes();//全部下载字节
		MobileStatistics[5] = TrafficStats.getTotalRxPackets();//全部下载包
		MobileStatistics[6] = TrafficStats.getTotalTxBytes();//全部下载字节
		MobileStatistics[7] = TrafficStats.getTotalTxPackets();//全部下载包
		MobileStatistics[8] = new Date().getTime();
		return MobileStatistics;
	}
}
