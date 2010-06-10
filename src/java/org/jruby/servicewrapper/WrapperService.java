package org.jruby.servicewrapper;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Java base class for Ruby service implementations
 */
public class WrapperService implements Runnable{
    private Thread _javaThread;

    public int start(){
        return -1;
    }

    public int stop(){
        return -1;
    }

    public String name() {
        return null;
    }

    public void spin_java_thread() {
        System.out.println("Creating Idle-Non-Daemon-Thread");
        _javaThread = new Thread(this);
        _javaThread.setName("Idle-Non-Daemon-Thread");
        _javaThread.start();
    }

    public void run() {
        System.out.println("Idle-Non-Daemon-Thread started");
        try {
            while (true) {
                _javaThread.join();
            }
        }
        catch (InterruptedException ie) {
            System.out.println("Idle-Non-Daemon-Thread interrupted");
        }
    }

    public void kill_java_thread() {
        if (_javaThread != null) {
            System.out.println("Interrupting Idle-Non-Daemon-Thread");
            _javaThread.interrupt();
        }
    }

    public void abort_service(int exitCode) {
        System.err.println("Service Abort requested");
        WrapperManager.stop(exitCode);
    }
}