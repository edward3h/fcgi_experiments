package org.ethelred.experiments.graal;

import org.ethelred.experiments.graal.libfcgi.FCGX_ParamArray;
import org.ethelred.experiments.graal.libfcgi.FCGX_Request;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.ethelred.experiments.graal.libfcgi.LibFCGI.*;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-10-02
 */
public class ParamsGraal
{
    public static void main(String[] args)
    {
        ParamsGraal app = new ParamsGraal();
        app.run();
    }

    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private final Lock acceptLock = new ReentrantLock();
    private final Semaphore newJob = new Semaphore(1);

    private void run()
    {
        FCGX_Init();
        while (true)
        {
            try
            {
                newJob.acquire();
                executorService.execute(new Handler());
            } catch (InterruptedException ignore) {
                // ignore
            }
        }
    }

    class Handler implements Runnable {

        @Override
        public void run()
        {
            FCGX_Request request = StackValue.get(FCGX_Request.class);
            FCGX_InitRequest(request, 0, 0);

            acceptLock.lock();
            try {
                FCGX_Accept_r(request);
            } finally
            {
                acceptLock.unlock();
                newJob.release();
            }

            PrintStream out = new PrintStream(new FCGXOutputStream(request.getOut()));
            out.println("Content-type:text/plain");
            out.println();
            out.println("Params");
            FCGX_ParamArray params = request.getEnvp();
            for (int i = 0; ; i++) {
                CCharPointer charPointer = params.read(i);
                if (charPointer.isNull()) {
                    break;
                }
                String entry = CTypeConversion.toJavaString(charPointer);
                out.println(entry);
            }
            String cl = _getParam("CONTENT_LENGTH", request);
            try {
                if (Integer.parseInt(cl) > 0) {
                    out.println("Body");
                    BufferedInputStream inputStream = new BufferedInputStream(new FCGXInputStream(request.getIn()));
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > -1) {
                        out.write(buf, 0, len);
                    }
                    out.println();
                }
            } catch (NumberFormatException | IOException ignore) {
                // ignore
            }

            FCGX_Finish_r(request);
        }
    }

    private static String _getParam(String paramName, FCGX_Request request)
    {
        try (
                CTypeConversion.CCharPointerHolder name = CTypeConversion.toCString(paramName))
        {
            return CTypeConversion.toJavaString(FCGX_GetParam(name.get(), request.getEnvp()));
        }
    }
}
