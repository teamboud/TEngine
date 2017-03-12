package toolbox;

public class FrameTime {
    static double delta;
	static long timing;
	
	public static void updateTime(){
	    long newTime = System.nanoTime();
	    delta = (newTime - timing) / 1_000_000_000.0;
	    timing = newTime;
	}
	
	public static double getDelta() {
	    return delta;
	}
	
}
