package org.ethelred.experiments.graal.libfcgi;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-09-30
 */
@CContext(LibFCGI.LibFCGIDirectives.class)
@CStruct("FCGX_Stream")
public interface FCGX_Stream extends PointerBase
{
}
