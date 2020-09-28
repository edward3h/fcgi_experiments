package org.ethelred.experiments;

import java.lang.reflect.InvocationTargetException;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-09-26
 */
public class HelloFCGI
{ 
    public static void main (String args[]) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        System.err.println("Starting HelloFCGI");
    int count = 0;
    FCGIInterface foo = new FCGIInterface();
    while(foo.FCGIaccept()>= 0) {
        System.err.println("foo");
        count ++;
        System.out.println("Content-type: text/html\n\n");
        System.out.println("<html>");
        System.out.println(
                "<head><TITLE>FastCGI-Hello Java stdio</TITLE></head>");
        System.out.println("<body>");
        System.out.println("<H3>FastCGI-HelloJava stdio</H3>");
        System.out.println("request number " + count +
                " running on host "
        + System.getProperty("SERVER_NAME"));
        System.out.println("</body>");
        System.out.println("</html>");
    }
}
}
