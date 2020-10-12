package org.ethelred.experiments.graal;

import org.ethelred.experiments.graal.libfcgi.FCGX_Stream;

import java.io.IOException;
import java.io.OutputStream;

import static org.ethelred.experiments.graal.libfcgi.LibFCGI.FCGX_PutChar;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-10-01
 */
class FCGXOutputStream extends OutputStream
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
