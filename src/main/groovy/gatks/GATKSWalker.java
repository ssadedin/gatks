package gatks;
import groovy.lang.Closure;

/**
 * Methods that GATKS walkers have in addition to normal walkers
 * 
 * @author ssadedin
 */
@SuppressWarnings("rawtypes")
public interface GATKSWalker {
	
	void setMapBody(Closure mapBody);

	Closure getMapBody();
	
	void inject(String name, Closure c);
}
