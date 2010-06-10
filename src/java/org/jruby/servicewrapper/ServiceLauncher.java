
package org.jruby.servicewrapper;


import static org.kohsuke.args4j.ExampleMode.ALL;

import org.jruby.Ruby;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


/**
 *In order to get service functionality for you can use this service launcher with 
 * the java service wrapper and just "drop" your app into the wrapper.
 * In order to launch a jruby app from java and still be able to launch our "main" ruby code
 * outside of the service with "jruby service.rb" we needed to get some common infrastructure in place.  
 * That's what this class is.  
 * 
 */
public class ServiceLauncher implements WrapperListener
{	
	   public static void main( String[] args )
	    {
	        // Start the application.  If the JVM was launched from the native
	        //  Wrapper then the application will wait for the native Wrapper to
	        //  call the application's start method.  Otherwise the start method
	        //  will be called immediately.
	        WrapperManager.start( new ServiceLauncher(), args );
	    }
	
	
	public ServiceLauncher(){
	}
	
    /* ******** Wrapper methods are implemented here *********** */
    /**
     * The start method is called when the WrapperManager is signaled by the 
     *	native wrapper code that it can start its application.  This
     *	method call is expected to return, so a new thread should be launched
     *	if necessary.
     *
     * @param args List of arguments used to initialize the application.
     *
     * @return Any error code if the application should exit on completion
     *         of the start method.  If there were no problems then this
     *         method should return null.
     */
	public Integer start( String[] args)
	{
		if (_service == null)
		{
			LauncherOptions opts = new LauncherOptions();
			CmdLineParser parser = new CmdLineParser(opts);
			try{
				parser.parseArgument(args);
			} catch( CmdLineException e ) {
				System.err.println(e.getMessage());
				System.err.println("  Example: java ServiceLauncher"+parser.printExample(ALL));			
				parser.printUsage(System.err);
				System.err.println();
				return -1;
			}
			initialize(opts.serviceClassName, opts.serviceFileName, opts.serviceInstanceName);
			
		}
		
		_service.start();
		
		//We may need to let the JVM complete it's startup
		try
		{
			Thread.sleep(500);
		} catch (Exception e) {}
		//TODO: uncomment this if you want to see a thread dump after we start.  If there are 
		// no Non-Daemon threads then tanuki will shutdown on you
		org.tanukisoftware.wrapper.WrapperManager.requestThreadDump();
		return null;
	}
	
    /**
     * Called when the application is shutting down.  The Wrapper assumes that
     *  this method will return fairly quickly.  If the shutdown code code
     *  could potentially take a long time, then WrapperManager.signalStopping()
     *  should be called to extend the timeout period.  If for some reason,
     *  the stop method can not return, then it must call
     *  WrapperManager.stopped() to avoid warning messages from the Wrapper.
     *
     * @param exitCode The suggested exit code that will be returned to the OS
     *                 when the JVM exits.
     *
     * @return The exit code to actually return to the OS.  In most cases, this
     *         should just be the value of exitCode, however the user code has
     *         the option of changing the exit code if there are any problems
     *         during shutdown.
     */	
	public int stop(int exitCode)
	{
		System.out.println("Stop called");
		if (_service != null)
		{
			//TODO: We may need to update this to handle exit codes
			_service.stop();
		}
		
		return exitCode;
	}
	
	/**
     * Called whenever the native wrapper code traps a system control signal
     *  against the Java process.  It is up to the callback to take any actions
     *  necessary.  Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT, 
     *    WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or 
     *    WRAPPER_CTRL_SHUTDOWN_EVENT
     *
     * @param event The system control signal.
     */
    public void controlEvent( int event )
    {
        if (WrapperManager.isControlledByNativeWrapper()) {
            // The Wrapper will take care of this event
        } else {
            // We are not being controlled by the Wrapper, so
            //  handle the event ourselves.
            if ((event == WrapperManager.WRAPPER_CTRL_C_EVENT) ||
                (event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT) ||
                (event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT)){
				_service.stop();
                WrapperManager.stop(0);
            }
			//TODO: if we need to handle any other control sequences we should do that here
        }
	}
	/* ******************** End WrapperListener handling **************** */

    public String getServiceName()
    {
    	if (_service != null)
    		return _service.name();
    	else
    		return null;
    }


	/**
	 *  Setup the jruby vm for use with our service, and then "launch" the designated service class
	 *  @param serviceClassName the Ruby class name for the service to be launched
	 *  @param serviceFile the ruby file for the service to be launched  
	 *  @param serviceName the service's instance id.
	 */
	public void initialize(String serviceClassName, String serviceFile, String serviceName)
	{		
		try {
			String filename = serviceFile.split("\\.")[0];
			// Create runtime instance of the Ruby VM
			_rubyRuntime = Ruby.newInstance();
			_rubyRuntime.evalScriptlet(String.format("require '%s'",filename));
			// so the serviceName parameter isn't really needed... 
			//  I was thinking if you had multiple of the same service you might want to name them differently
			WrapperService _rfj = (WrapperService)_rubyRuntime.evalScriptlet(String.format("%s.new",serviceClassName, serviceName));
			rfj = org.jruby.javasupport.JavaEmbedUtils.rubyToJava(_rubyRuntime, (org.jruby.runtime.builtin.IRubyObject) rfj, 
									WrapperService.class);

			_service = (WrapperService)rfj;

		} catch (Throwable e) { 
			System.err.println("Exception creating the service: ");
			e.printStackTrace(System.err);
		}		
	}

	private Ruby _rubyRuntime;
	private WrapperService _service;
}