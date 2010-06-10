package org.jruby.servicewrapper;
import org.kohsuke.args4j.Option;

/**
 *  A means of defining our command line option set for the ServiceLauncher.
 */
public class LauncherOptions{
	@Option(name="-fn",required=true, usage="declares the RUBY filename (with or without the RB extension) that the Ruby service class is defined in.")
	public String serviceFileName;
	@Option(name="-cn", required=true, usage="declares the RUBY class name (camel casing is expected)")
	public String serviceClassName;
	@Option(name="-in", required=true, usage="The running ruby service instance id")
	public String serviceInstanceName;
}