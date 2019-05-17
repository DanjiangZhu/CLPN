package pipe.calculations;


import pipe.controllers.PipeApplicationController;
import pipe.gui.widgets.FileBrowser;
import pipe.models.PipeApplicationModel;
import pipe.modules.reachability.ReachabilityGraphGenerator;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.PetriNetView;

import java.io.File;

public class test4hca {
    private static PipeApplicationModel applicationModel;
    private static PipeApplicationController applicationController;

    public static void main(String args[])
    {
//        applicationModel = new PipeApplicationModel("v4.3.0");
//        applicationController = new PipeApplicationController(applicationModel);
        String  userPath = null;
        File filePath= new FileBrowser(userPath).openFile();

        PetriNetView pn=new PetriNetView(filePath.getPath(),1);

        (new ReachabilityGraphGenerator()).start(pn);
    }
}
