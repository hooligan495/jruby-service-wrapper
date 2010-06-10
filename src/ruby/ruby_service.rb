require 'java'
require File.dirname(__FILE__)+'servicewrapper.jar'

module Services
  import_java 'org.jruby.servicewrapper.wrapperservice'
=begin rdoc
  The RubyService is a base class that gives your service class the 
  hooks to the outer service wrappers.  The start and stop can be used
  as is.  All you need to do in your derived class in implement
  the spin_up and spin_down methods.  OW. you can override the start and
  stop and manage threads yourself (that is preferred this is more here 
  as an example
=end
  class RubyService < WrapperService
    attr_reader :name
    def initialize
      #configure the service for the common stuff
      @name = "RubyService"
    end  

    def start (args=nil)
      @started = true

      # Need a sticky 'non-Daemon' thread to keep the
      # Java Service Wrapper happy
      spin_java_thread

      # Run the child's spin_up function in it's own thread
      # so this function will return 'immediately'.
      @thread = Thread.new { spin_up }
    end
  
    def stop
      # Service is no longer running
      @started = false

      # Shutdown the child service
      spin_down
      
      # Abort the sticky thread to make the JSW happy
#      kill_java_thread
    end  
  end
end
