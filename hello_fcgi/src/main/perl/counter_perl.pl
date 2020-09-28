#!/usr/bin/perl

use FCGI;

my $counter = 0;
my $request = FCGI::Request();
 
while($request->Accept() >= 0) {
     $counter++;
     print "Content-type:text/html\n\n";
     print "I have run $counter times.";
}