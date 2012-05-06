package gatks;
import org.broadinstitute.sting.gatk.GenomeAnalysisEngine;
import org.broadinstitute.sting.gatk.walkers.Walker;

@SuppressWarnings("rawtypes")
public class GATKSEngine extends GenomeAnalysisEngine {
    
    Walker w = null;
    
    public GATKSEngine(Walker w) {
        super();
        this.w = w;
    }

    @Override
    public Walker<?, ?> getWalkerByName(String walkerName) {
        return w;
    }

}
