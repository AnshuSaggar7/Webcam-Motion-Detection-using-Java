

import java.awt.*;

import java.awt.event.*;

import javax.media.*;

import javax.media.control.TrackControl;

import javax.media.Format;

import javax.media.format.*;

import javax.media.protocol.*;

import javax.media.datasink.*;

import javax.media.control.*;

public class TestMotionDetection extends Frame implements ControllerListener 
{

    Processor p;

     DataSink fileW = null;
 
    Object waitSync = new Object();
 
    boolean stateTransitionOK = true;

 
    public TestMotionDetection() 
	{
      
		super("Test Motion Detection");
    
	}
    
 
    public boolean open(MediaLocator ds) 
	{

	
		try 
		{
	    
			p = Manager.createProcessor(ds);
	
		}
		 catch (Exception e) 
		{

		    System.err.println("Failed to create a processor from the given datasource: " + e);
	
	    return false;

		}

	
		p.addControllerListener(this);

	
		p.configure();
	
		if (!waitForState(p.Configured)) 
		{
	
		    System.err.println("Failed to configure the processor.");
	    
		    return false;
	
		}

	
	

	p.setContentDescriptor(null);
	
	
	TrackControl tc[] = p.getTrackControls();

	
		if (tc == null) 
		{
	
		    System.err.println("Failed to obtain track controls from the processor.");
	
		    return false;
	
		}

	
	
	TrackControl videoTrack = null;

	
		for (int i = 0; i < tc.length; i++) 
		{

		    if (tc[i].getFormat() instanceof VideoFormat) 
			{
		
		videoTrack = tc[i];
		
				break;
	    
			}
	
		}

	
		if (videoTrack == null) 
		{

		    System.err.println("The input media does not contain a video track.");
	
	    return false;
	
	}


	
	

	try 
		{
	
	    Codec codec[] = {  new MotionDetectionEffect(), new TimeStampEffect()};
	
	    videoTrack.setCodecChain(codec);

		} 
		catch (UnsupportedPlugInException e) 
		{
	
	    System.err.println("The processor does not support effects.");
	
	}

	

	
	p.prefetch();

		if (!waitForState(p.Prefetched)) 
		{
	
	    System.err.println("Failed to realize the processor.");
	 
		    return false;
	
		}
	
	

	setLayout(new BorderLayout());

	
		Component cc;

	
		Component vc;
	
		if ((vc = p.getVisualComponent()) != null) 
		{
	    
			add("Center", vc);
	
		}

	
		if ((cc = p.getControlPanelComponent()) != null) 
		{
	    
			add("South", cc);
	
		}

	

		p.start();
	

	setVisible(true);
	

	addWindowListener(new WindowAdapter()
		 {
		
	    public void windowClosing(WindowEvent we)
				 {
		
					p.close();
			
		System.exit(0);

				 }

		});
      
        p.start();

	return true;
    }

    public void addNotify() {
	super.addNotify();
	pack();
    }

        boolean waitForState(int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() != state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }


        public void controllerUpdate(ControllerEvent evt) {

        System.out.println(this.getClass().getName()+evt);
	if (evt instanceof ConfigureCompleteEvent ||
	    evt instanceof RealizeCompleteEvent ||
	    evt instanceof PrefetchCompleteEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = true;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof ResourceUnavailableEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = false;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof EndOfMediaEvent) {
	    p.close();
	    System.exit(0);
	}
    }

  public static void main(String [] args) {

        if (args.length == 0) {
            prUsage();
            System.exit(0);
        }

        String url = args[0];

        if (url.indexOf(":") < 0) {
            prUsage();
            System.exit(0);
        }

        MediaLocator ml;
   if ((ml = new MediaLocator(url)) == null) {
            System.err.println("Cannot build media locator from: " + url);
            System.exit(0);
        }

        TestMotionDetection fa = new TestMotionDetection();

        if (!fa.open(ml))
            System.exit(0);
    }

    static void prUsage() {
        System.err.println("Usage: java TestMotionDetection <url>");
    }


}
