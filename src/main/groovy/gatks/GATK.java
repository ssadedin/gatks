package gatks;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadinstitute.sting.commandline.RodBinding;
import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;

public class GATK {
    
	private static String [] argv = null;
	
	private Map<String, Object> config;
	
	private static Map<String, Object> defaults = new HashMap<String, Object>();
	
	private static boolean hasRegion = false;
	
	private static boolean hasISR = false;
	
	
	/**
	 * Create a map containing the default set of injectable calls
	 * for walkers to perform.
	 */
	public static Map<String, List<Closure>> getDefaultInjectionMap() {
	    Map<String, List<Closure>> injections = new HashMap<String, List<Closure>>();
        for(String defaultInjection : new String [] {"initializers","mapfilters"}) {
        	injections.put(defaultInjection, new ArrayList<Closure>());
        }
        return injections;
	}
    
    public GATK(Map<String, Object> config) {
    	this.config = new HashMap<String, Object>();
    	this.config.putAll(defaults);
    	this.config.putAll(config);
	}

	public static void init(String [] argv) {
		
		List<String> passThroughArgs = new ArrayList<String>();
        
        // Pull out special arguments we are interested in
        String consume = null;
        for(String arg : argv) {
        	if(arg.equals("-o")) {
        		consume = "out";
        	}
        	else
        	if(arg.equals("-I") || arg.equals("--input_file")) {
        		consume = "bam";
        	}
        	else
        	if(arg.equals("-vcf")) {
        		consume = "vcf";
        	}
        	else
        	if(arg.equals("-L")) {
        		hasRegion = true;
        	}
        	else
        	if(arg.equals("-isr")) {
        		hasISR = true;
        	}
        	else
        	if(consume != null) {
        		defaults.put(consume, arg);
        		consume = null;
        	}
        	else {
        		passThroughArgs.add(arg);
        	}
        }
        
        GATK.argv = passThroughArgs.toArray(new String[passThroughArgs.size()]);
    }
    
    void eachLocus(Closure c) {
        GATKSCommandLine instance = new GATKSCommandLine(config, new GATKSLocusWalker(c));
        instance.setHasISR(hasISR);
        instance.setHasRegion(hasISR);
        instance.start(argv);
    }
    
    void eachROD(Closure c) {
        GATKSCommandLine instance = new GATKSCommandLine(config, new GATKSRodWalker(c));
        instance.setHasISR(hasISR);
        instance.setHasRegion(hasISR);
        instance.start(argv);
    }
    
    void eachVariant(Closure c) {
    	config.put("bed", config.get("vcf"));
        GATKSCommandLine instance = new GATKSCommandLine(config, new GATKSLocusWalker(c));
        instance.setHasISR(hasISR);
        instance.setHasRegion(hasISR);
        instance.getInjections().get("mapfilters").add(new Closure(instance) {
        	
        	/**
        	 * TODO - expand to multiple VCF files, each will be a separate field.  
        	 */
        	Field field = null;
        	
			@Override
			public Object call(Object... args) {
				
				Object walker = getDelegate();
				try {
					if(field == null) {
						try {
							field = walker.getClass().getField("vcf");
						} 
						catch (NoSuchFieldException e) {
							field = walker.getClass().getField("vcf1");
						}
					}
					
					RefMetaDataTracker meta = (RefMetaDataTracker) args[0];
					
					if(meta == null)
						return Boolean.FALSE;
					
					AlignmentContext context = (AlignmentContext) args[2];
				    List<VariantContext> variants = meta.getValues((RodBinding<VariantContext>) field.get(walker), context.getLocation());
				    if(variants == null)
				    	return Boolean.FALSE;
				    if(variants.isEmpty())
				    	return Boolean.FALSE;
				    
				    return Boolean.TRUE;
				} 
				catch (SecurityException e) {
					e.printStackTrace();
				} 
				catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return Boolean.FALSE;
			}
        });
        instance.start(argv);
    }
    
    static GATK gatk(Map<String,Object> config) {
    	return new GATK(config);
    }
}
