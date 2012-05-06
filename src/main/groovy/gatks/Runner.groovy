/*
 * Copyright (c) GATKS Authors, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */  
package gatks

import java.text.*;

import java.util.logging.*;

/**
 * A small wrapper that parses command line arguments and forwards execution
 * to the user's script
 * 
 * @author ssadedin@gmail.com
 */
class Runner {
    
    private static Logger log = Logger.getLogger("gatks.Runner");
    
    final static String version = System.getProperty("gatks.version")
    
    final static String builddate = System.getProperty("gatks.builddate")?:System.currentTimeMillis()
    
    static CliBuilder runCli = new CliBuilder(usage: """gatks <script> <arguments ...>""")
    
    static CliBuilder stopCommandsCli = new CliBuilder(usage: "bpipe stopcommands\n")
    
    public static OptionAccessor opts = runCli.parse([])
    
    public static void main(String [] args) {
        
        def parentLog = log.getParent()
        parentLog.getHandlers().each { parentLog.removeHandler(it) }
        
        // The current log file
        FileHandler fh = new FileHandler("gatks.log")
        fh.setFormatter(new GATKSLogFormatter())
        parentLog.addHandler(fh)
       
        log.info("Starting")
        
        
        def groovyArgs = ["--main", "groovy.ui.GroovyMain", "-e"] 
        def cli = runCli
        cli.with {
             h longOpt:'help', 'usage information'
             v longOpt:'verbose', 'print internal logging to standard error'
        }
        
        String versionInfo = "\nGATSK Version $version   Built on ${new Date(Long.parseLong(builddate))}\n"
        def opt = cli.parse(args)
        if(!opt) 
            System.exit(1)
            
        if(!opt.arguments()) {
            println versionInfo
            cli.usage()
            println "\n"
            System.exit(1)
        }
		
        opts = opt
		if(opts.v) {
            ConsoleHandler console = new ConsoleHandler()
            console.setFormatter(new GATKSLogFormatter())
            console.setLevel(Level.FINE)
            parentLog.addHandler(console)
		}
        
		File scriptFile = new File(opt.arguments()[0])
        if(!scriptFile.exists()) {
            println "\nCould not understand command $scriptFile or find it as a file\n"
            cli.usage()
            println "\n"
            System.exit(1)
        }
        
		groovyArgs += "import static gatks.GATK.*; import static gatks.GATKS.*; gatks.GATK.init(args);" + scriptFile.text
		if(opt.arguments().size() > 1) 
			groovyArgs += opt.arguments()[1..-1]
        
        org.codehaus.groovy.tools.GroovyStarter.main(groovyArgs as String[])
    }
}
