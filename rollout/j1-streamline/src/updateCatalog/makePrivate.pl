#!/usr/local/bin/perl -w
#
# makePrivate.pl
#usage: >makePrivate.pl
#
#assumes: paths.properties file is located in "."
#
#This tool only works for jason version b data.
#-er 10/29/07
#-yc 05/02/11 - Removed /data to work with version c and new archive layout


use strict;
use File::Copy;

my $pathsFile = "./paths.properties";
my $gdrRelease = "";
my $sgdrRelease = "";
my $gdrSource = "";
my $sgdrSource = "";
my @cycles2return = ();
my @returnPasses = ();

open(PATHS, "<$pathsFile");

while(<PATHS>) {

     chomp;
     next if($_=~/^#/);
     
     if(/^sgdrRelease/)
     {
          $sgdrRelease = (split('='))[1];
          $sgdrRelease =~ s/^\s*//;
     }elsif(/^gdrRelease/)
     {
          $gdrRelease =  (split('='))[1] ;
          $gdrRelease =~ s/^\s*//;
     }elsif(/^gdr_dir/)
     {
          $gdrSource = (split('='))[1];
          $gdrSource =~ s/^\s*//;
     }elsif(/^sgdr_dir/)
     {
          $sgdrSource = (split('='))[1];
          $sgdrSource =~ s/^\s*//;
     }

}

close PATHS;

$gdrRelease = "$gdrRelease/gdr_c";
opendir(GDR_RELEASE, $gdrRelease) or die "can't open $gdrRelease: $!";
my $temp = readdir(GDR_RELEASE);
@cycles2return = grep {/^c/} readdir(GDR_RELEASE);
close GDR_RELEASE;

foreach my $cycle (@cycles2return){
     opendir(RETURN_GDR, "$gdrRelease/$cycle");
     @returnPasses = grep {/^JA1/} readdir(RETURN_GDR);
     close RETURN_GDR;

     $returnPasses[0] =~ /^JA1_GDR_2P(\w{1})P\d{3}_\d{3}\.(\w{4})/;
     my $version = $1;
     my $author = lc $2;

     my $source = "$gdrRelease/$cycle";
     my $destination = "$gdrSource/gdr_$author"."_$version/$cycle";
     move($source, $destination) or die "can't move $source to $destination: $!";
}

system("mv $sgdrRelease/sgdr_c/c* $sgdrSource/sgdr_c");
