package gatks

import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.codecs.vcf.VCFUtils;
import org.broadinstitute.sting.utils.codecs.vcf.VCFWriter;

class VCFHeaderInitializer extends Closure {
	
	public VCFHeaderInitializer(Object owner) {
		super(owner);
	}

	Object call() {
		VCFWriter writer = this.delegate.out
		writer.writeHeader(new VCFHeader(VCFUtils.getHeaderFields(this.delegate.toolkit)));
	}
}
