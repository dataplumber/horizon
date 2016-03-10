#!/usr/bin/perl -w
# -*-Perl-*-
#
### =============================================================== ###
#                                                                     #
#  The HORIZON Application Launcher Configuration                        #
#                                                                     #
#  Function:                                                          #
#  This module provides utility subroutines for configurating the     #
#  HORIZON Ingestion Service command line programs.                   #
#                                                                     #
#  Assumptions:                                                       #
#  - Perl 5 is installed on the target platform in /usr/bin/perl      #
#  - The $HORIZON environment variable must point to the directory       #
#    containing the domain file, the SSL certificate, and             #
#    ingestconfig.pm files.                                           #
#                                                                     #
#  Copyright (c) 2007, Jet Propulsion Laboratory,                     #
#  California Institute of Technology.  All rights reserved           #
#                                                                     #
#  Created:                                                           #
#  Aug. 27, 2007 T. Huang {Thomas.Huang@jpl.nasa.gov}                 #
#                                                                     #
#  Modifications:                                                     #
#                                                                     #
### =============================================================== ###
#
# $Id: $
#

use strict;
use File::Spec;
use File::Basename;

# the global default JRE version
use constant DEFAULT_JRE_VERSION => 1.5;

# the global array to track childen PIDs
my @KID_PIDS;
$SIG{INT} = \&killer;
$SIG{QUIT} = \&killer;
$SIG{TERM} = \&killer;

# signal handling routine
sub killer {
   foreach my $kid (reverse(@KID_PIDS)) {
      kill 15, $kid;
   }
}

##
# Subroutine to check the user JVM version and return the correct
# Java executable using the default required JRE version
#
# @param $debug the debug flag
#
sub getCmd {
   my ($debug) = @_;
   return &getCmdReqVersion (DEFAULT_JRE_VERSION, $debug);
}


##
# Subroutine to check the user JVM version and return the correct
# Java executable
#
# @param $reqVersion the minimum required JVM version
# @param $debug the debug flag
#
sub getCmdReqVersion {
   my ($reqVersion, $debug) = @_;

   print "[DEBUG] Requires JRE version $reqVersion.\n" if ($debug);

   my $v2Jdk = $ENV{V2JDK} ? $ENV{V2JDK} : "";
   if ($v2Jdk eq "") {
      $v2Jdk = 
         File::Spec->catfile(File::Spec->rootdir(), 'usr', 'java');
   }

   my $javaHome = $ENV{JAVA_HOME} ? $ENV{JAVA_HOME} : "";
   if ($javaHome eq "") {
      $javaHome = $v2Jdk;
   }

   my $javaCmd = File::Spec->catdir($javaHome, 'bin', 'java');
   my $version = `$javaCmd -version 2>&1`;
   if ($version =~ m/([0-9]+\.[0-9]+)/) {
      $version = $1;
      print "[DEBUG] $version", "\n" if ($debug);
      if ($version < $reqVersion) {
         print "[ERROR] This software requires JRE version ";
         print "$reqVersion or above.\n";
         die "[ERROR] Current JRE location $javaHome.\n";
      }
   } else {
      print "[ERROR] Unable to determine JRE version.\n";
      die "[ERROR] Current JRE location $javaHome.\n";
   }
   return $javaCmd; #"$javaHome/bin/java";
}


##
# Subroutine to return the JVM arguments for the Ingest client
# application.
#
# @param $classpath the user input classpath
# @param $debug the debug flag
#
sub getJVMArgs {
   my ($classpath, $debug) = @_;
   return &getJVMArgsCheckGUI ($classpath, 0, $debug);
}


##
# Subroutine to return the JVM arguments for the Ingest client
# applications.  It checks the GUI flag to determine if
# GUI-related VM parameters needs to be set.
#
# @param $classpath the user input classpath
# @param $gui the GUI boolean flag
# @param $debug the debug flag
#
sub getJVMArgsCheckGUI {

   my ($classpath, $gui, $debug) = @_;

   my $horizon = 
      &setHORIZON (File::Spec->catdir($ENV{PWD}, "..", "config"), $debug);
   $classpath = &getClasspath ($classpath, $debug);
   my $app = File::Basename::basename($0);

   my @args;
   push @args, "-classpath", "$classpath";
   #push @args, "-Djavax.net.ssl.trustStore=$horizon/horizon.keystore";
   push @args, "-Dhorizon.config.dir=$horizon";
   push @args, "-Dhorizon.domain.file=$horizon/horizon_domain.xml";
   push @args, "-Dhorizon.logging.config=$horizon/horizon_client.lcf";
   push @args, "-Dhorizon.user.application=$app";
   push @args, "-Djava.net.preferIPv4Stack=true";

   if ($^O eq "darwin" && !$gui) {
      push @args, "-Dcom.apple.backgroundOnly=true";
      push @args, "-Djava.awt.headless=true";
   }

   if ($debug) {
      push @args, "-Dhorizon.enable.debug";
   }

   return @args;
}


##
# Subroutine to set and return the HORIZON environment variable
#
# @param $default the default location if the environment is not set
# @param $debug the debug flag
#
sub setHORIZON {
   my ($default, $debug) = @_;
   my $horizon = $ENV{HORIZON} ? $ENV{HORIZON} : "";
   if ($horizon eq "") {
      warn "HORIZON environment variable is not set.\n";
      warn "Using default location $default.\n";
      $horizon = $default;
      $ENV{HORIZON} = $horizon;
   }
   return $horizon;
}

##
# Subroutine to set and return the application classpath
#
# @param $classpath the user input classpath to be prepend to the 
#    application classpath
# @param $debug the debug flag
#
sub getClasspath {
   my ($classpath, $debug) = @_;
                                                                                           
   $classpath = defined $classpath ? $classpath : "";
   my $horizon = 
      &setHORIZON (File::Spec->catdir($ENV{PWD}, "..", "config"), $debug);
   my $seperator = (($^O eq "MSWin32") || 
                   ($^O eq "dos") || 
                   ($^O eq "cygwin")) ? ";" : ":";
                                                                                           
   my $horizonjarpattern = 
      File::Spec->catdir($horizon, "..", "lib", "*.jar");
   my @horizonjarfiles =glob($horizonjarpattern);
   print "[DEBUG] HORIZON jarfiles=@horizonjarfiles\n" if ($debug);
   foreach my $jar (@horizonjarfiles ) {
      $classpath .="$seperator$jar";
   }
   $classpath .="$seperator$horizon";
   return $classpath;
}

##
# Subroutine to run the input program string in a child process and track its PID
#
# @param $program the program execution string
# $param $debug the debug flag
#
sub run {
   my ($program, $debug) = @_;

   my $returnValue = 0;

   my $pid = fork();
   if ($pid == 0) {
      # child process
      &trackpid($pid);
      $returnValue = system $program;
      exit $returnValue;
   } elsif ($pid > 0) {
      # parent process
      &trackpid($pid);
      waitpid($pid, 0);
   } elsif ($pid < 0) {
      warn ("Unable to spawn java process.\n");
   }

   return $returnValue >> 8;
}

##
# Subroutine to track PIDs to make sure they are distinct
#
# @param $pid the process id
#
sub trackpid {
   my ($pid) = @_;
   my %members;
   undef %members;
   for (@KID_PIDS) {
      $members{$_} = 1;
   }

   if (!$members{$pid}) {
      push @KID_PIDS, $pid;
   } 
}

1;
