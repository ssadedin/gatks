package gatks;
import java.util.Map;

import groovy.lang.Closure;

public class GATK {
    
	private static String [] argv = null;
	
	private Map<String, Object> config;
    
    public GATK(Map<String, Object> config) {
    	this.config = config;
	}

	public static void init(String [] argv) {
        GATK.argv = argv;
    }
    
    void eachLocus(Closure c) {
        if(argv == null) 
            throw new IllegalStateException("It looks like you ran your script without calling GATK.init().  Please add this call before calling any other GATKS functions!");
        
        GATKSCommandLine instance = new GATKSCommandLine(config, new GATKSLocusWalker(c));
        instance.start(argv);
    }
    
    void eachROD(Closure c) {
        if(argv == null) 
            throw new IllegalStateException("It looks like you ran your script without calling GATK.init().  Please add this call before calling any other GATKS functions!");
        
        GATKSCommandLine instance = new GATKSCommandLine(config, new GATKSRodWalker(c));
        instance.start(argv);
    }
    
    
    static GATK gatk(Map<String,Object> config) {
    	return new GATK(config);
    }
}
