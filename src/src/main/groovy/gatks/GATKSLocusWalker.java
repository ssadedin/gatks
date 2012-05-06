package gatks;
import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;

import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.LocusWalker;

public class GATKSLocusWalker extends LocusWalker implements GATKSWalker {
    
    Closure mapBody = null;
    
   /**
     * List of closures invoked before by reduceInit
     */
    List<Closure> initializers = new ArrayList<Closure>();
  
    public GATKSLocusWalker(Closure mapBody) {
        this.mapBody = mapBody;
    }

    @Override
    public Object map(RefMetaDataTracker tracker, ReferenceContext ref, AlignmentContext context) {
        return mapBody.call(new Object[] { tracker, ref, context});
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
