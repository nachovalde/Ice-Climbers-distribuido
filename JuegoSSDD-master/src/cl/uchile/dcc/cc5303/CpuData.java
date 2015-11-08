package cl.uchile.dcc.cc5303;

import org.hyperic.sigar.*;

public class CpuData {
	
	public static double getCpuUsage(){
		Sigar sigar = new Sigar();
		double load=0;
		try {
			CpuPerc perc = sigar.getCpuPerc();
			load=perc.getCombined();
			if(load>0.70){
				System.out.println("Carga mayor que 70%");
			}
		} catch (SigarException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return load;
	}
}
