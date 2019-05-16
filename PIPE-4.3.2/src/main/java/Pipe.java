import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pipe.controllers.PipeApplicationController;
import pipe.models.PipeApplicationModel;

import javax.swing.*;
public class Pipe
{

	private PipeApplicationModel applicationModel;
	@SuppressWarnings("unused")
	private PipeApplicationController applicationController;

    //private static final Logger logger= LogManager.getLogger(Pipe.class);

	private Pipe(String version)
    {
        //logger.trace("初始化Pipe");


        applicationModel = new PipeApplicationModel(version);
        applicationController = new PipeApplicationController(applicationModel);
    }
    public static void main(String args[])
    {
        Runnable runnable = pipeRunnable();
        SwingUtilities.invokeLater(runnable);
    }
	protected static Runnable pipeRunnable()
	{
		Runnable runnable = new Runnable()
                            {
                                public void run()
                                {
                                    @SuppressWarnings("unused")
									Pipe pipe = new Pipe("v4.3.0");
                                }
                            };
		return runnable;
	}
    protected static void runPipeForTesting() throws InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(pipeRunnable()); 
    }
}
