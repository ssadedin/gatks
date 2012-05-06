package gatks;
import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;

import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.Allows;
import org.broadinstitute.sting.gatk.walkers.DataSource;
import org.broadinstitute.sting.gatk.walkers.RodWalker;

@Allows({DataSource.READS, DataSource.REFERENCE})
@SuppressWarnings("rawtypes")
public class GATKSRodWalker extends RodWalker<Object, Object> implements GATKSWalker {
    
	Closure mapBody = null;
    
    /**
     * List of closures invoked before by reduceInit
     */
    List<Closure> initializers = new ArrayList<Closure>();
    
    public GATKSRodWalker() {
    }
    
    public GATKSRodWalker(Closure mapBody) {
        this.mapBody = mapBody;
    }

    @Override
    public Object map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
    	mapBody.setDelegate(this);
        return mapBody.call(new Object[] { tracker, ref, context});
    }
    
    @Override
	public void initialize() {
		super.initialize();
    	for(Closure init: initializers) {
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
	public void addInitializer(Closure c) {
		this.initializers.add(c);
	}
}
