package gatks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.Closure;

import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.LocusWalker;

public class GATKSLocusWalker extends LocusWalker implements GATKSWalker {
    
    Closure mapBody = null;
    
    /**
     * List of closures of injectable methods
     */
    Map<String, List<Closure>> injections = GATK.getDefaultInjectionMap();
    
    public GATKSLocusWalker() {
    }
  
    public GATKSLocusWalker(Closure mapBody) {
        this.mapBody = mapBody;
    }

    @Override
    public Object map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
    	
    	Object[] args = new Object[] { tracker, ref, context};
    	
    	for(Closure c : injections.get("mapfilters")) {
    		c.setDelegate(this);
    		Object result = c.call(args);
    		if(result instanceof Boolean) {
    			if(!(Boolean)result)
    				return null;
    		}
    	}
    	
    	mapBody.setDelegate(this);
        return mapBody.call(args);
    }
    
    

    @Override
	public void initialize() {
		super.initialize();
    	for(Closure init: injections.get("initializers")) {
    		init.setDelegate(this);
    		init.call();
    	}		
	}

	@Override
    public Object reduceInit() {
        return null;
    }

    @Override
    public Object reduce(Object value, Object sum) {
        return null;
    }
    
    public Closure getMapBody() {
		return mapBody;
	}

	public void setMapBody(Closure mapBody) {
		this.mapBody = mapBody;
	}

	@Override
	public void inject(String name, Closure c) {
		this.injections.get(name).add(c);
	}
}
