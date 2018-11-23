package pipe.models;

import java.io.Serializable;

public class VirtualArc extends Arc implements Serializable {
    public VirtualArc()
    {
        super("virtual");
    }

    public VirtualArc(Connectable source, Connectable target)
    {
        super(source, target);
    }
}
