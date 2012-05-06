package gatks;

import java.util.HashMap;

import groovy.lang.Closure;

public class GATKS {
	
	static void eachLocus(Closure c) {
		new GATK(new HashMap<String, Object>()).eachLocus(c);
	}
	
	static void eachVariant(Closure c) {
		new GATK(new HashMap<String, Object>()).eachVariant(c);
	}

	static void eachROD(Closure c) {
		new GATK(new HashMap<String, Object>()).eachROD(c);
	}
	
}
