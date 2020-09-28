#!/usr/bin/perl
# from https://help.dreamhost.com/hc/en-us/articles/216512598-Perl-overview
#use lib qw( /home/myhome/lib/perl/5.8.4 );
use FCGI;
use Socket qw( :crlf ); # server agnostic line endings in $CRLF

my $counter = 0;
while($request->Accept() >= 0) {
   $counter++;

   print
        "Content-Type: text/plain",
        $CRLF,
        $CRLF,
        "Hello World, in Perl FastCGI!",
        $CRLF,
        "I am process $$.",
        $CRLF,
        "I have served $counter request(s).",
        $CRLF;
 }