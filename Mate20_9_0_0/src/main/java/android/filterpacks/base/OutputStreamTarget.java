package android.filterpacks.base;

import android.app.ActivityManagerInternal;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class OutputStreamTarget extends Filter {
    @GenerateFieldPort(name = "stream")
    private OutputStream mOutputStream;

    public OutputStreamTarget(String name) {
        super(name);
    }

    public void setupPorts() {
        addInputPort(ActivityManagerInternal.ASSIST_KEY_DATA);
    }

    public void process(FilterContext context) {
        ByteBuffer data;
        Frame input = pullInput(ActivityManagerInternal.ASSIST_KEY_DATA);
        if (input.getFormat().getObjectClass() == String.class) {
            data = ByteBuffer.wrap(((String) input.getObjectValue()).getBytes());
        } else {
            data = input.getData();
        }
        try {
            this.mOutputStream.write(data.array(), 0, data.limit());
            this.mOutputStream.flush();
        } catch (IOException exception) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("OutputStreamTarget: Could not write to stream: ");
            stringBuilder.append(exception.getMessage());
            stringBuilder.append("!");
            throw new RuntimeException(stringBuilder.toString());
        }
    }
}
