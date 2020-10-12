package org.ethelred.experiments.graal;

import org.ethelred.experiments.graal.libfcgi.FCGX_Stream;
import org.ethelred.experiments.graal.libfcgi.LibFCGI;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-10-02
 */
public class FCGXInputStream extends InputStream
{
    private final FCGX_Stream in;

    public FCGXInputStream(FCGX_Stream in)
    {
        this.in = in;
    }

    @Override
    public int read() throws IOException
    {
        return LibFCGI.FCGX_GetChar(in);
    }
}
