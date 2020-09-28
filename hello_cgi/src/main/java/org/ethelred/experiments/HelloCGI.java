package org.ethelred.experiments;

/**
 * TODO
 *
 * @author eharman
 * @since 2020-09-20
 */
public class HelloCGI
{
    public static void main(String[] args)
    {
        System.out.println("Content-type:text/plain");
        System.out.println();
        System.out.println("Hello, world!");
    }
}
