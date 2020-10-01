package org.ethelred.experiments.graal;

import org.ethelred.experiments.graal.libfcgi.FCGX_Request;
import org.ethelred.experiments.graal.libfcgi.FCGX_Stream;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_Accept_r;
import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_Finish_r;
import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_GetParam;
import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_Init;
import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_InitRequest;
import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_PutChar;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-09-29
 */
public class HelloGraal
{
    static final int THREAD_COUNT = 5;
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args)
    {
        FCGX_Init();
        Executor executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 1; i < THREAD_COUNT; i++)
        {
            int finalI = i;
            executor.execute(() -> _handleRequests(finalI));
        }
        _handleRequests(0);
    }

    private static void _handleRequests(final int threadNum)
    {
        FCGX_Request request = StackValue.get(FCGX_Request.class);
        FCGX_InitRequest(request, 0, 0);

        int rc = 0;

        while ((rc = _accept(request)) >= 0)
        {
            PrintStream out = new PrintStream(new FCGXOutputStream(request.getOut()));
            String serverName = _getParam("SERVER_NAME", request);
            out.println("Content-type:text/plain");
            out.println();
            out.println("Hello, world!");
            out.printf("Count: %d%n", counter.incrementAndGet());
            out.printf("Process: %d%n", ProcessHandle.current().pid());
            out.printf("Thread: %d%n", threadNum);
            out.printf("Server: %s%n", serverName);

            try
            {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            }
            catch (InterruptedException ignore)
            {
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

    private static synchronized int _accept(FCGX_Request request)
    {
        return FCGX_Accept_r(request);
    }

    static class FCGXOutputStream extends OutputStream
    {
        private final FCGX_Stream out;

        FCGXOutputStream(FCGX_Stream out)
        {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException
        {
            int r = FCGX_PutChar(b, out);
            if (r == -1)
            {
                throw new IOException("Error writing output");
            }
        }
    }
}
