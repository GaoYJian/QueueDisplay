package his.app;

import java.util.Date;

import android.net.TrafficStats;

public class NetworkStatistics {
	long MobileStatistics[];
	
	public NetworkStatistics(){
		MobileStatistics = new long[9];
	}
	
	public long[] updateStatisitc(){
		MobileStatistics[0] = TrafficStats.getMobileRxBytes();//���������ֽ�
		MobileStatistics[1] = TrafficStats.getMobileRxPackets();//�������ذ�
		MobileStatistics[2] = TrafficStats.getMobileTxBytes();//�����ϴ��ֽ�
		MobileStatistics[3] = TrafficStats.getMobileTxPackets();//�����ϴ���
		MobileStatistics[4] = TrafficStats.getTotalRxBytes();//ȫ�������ֽ�
		MobileStatistics[5] = TrafficStats.getTotalRxPackets();//ȫ�����ذ�
		MobileStatistics[6] = TrafficStats.getTotalTxBytes();//ȫ�������ֽ�
		MobileStatistics[7] = TrafficStats.getTotalTxPackets();//ȫ�����ذ�
		MobileStatistics[8] = new Date().getTime();
		return MobileStatistics;
	}
}
