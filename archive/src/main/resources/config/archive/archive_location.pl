#!/usr/local/bin/perl
#
# Copyright 2008-2009, California Institute of Technology.
# ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
#
# $Id: archive_location.pl 4159 2009-11-03 17:43:31Z gangl $

# This script will create an archive directory structure in the local
# environment for testing and then update the specified database with
# the local root directory for the archive base path.

# Get the input parameters and display them to the operator for confirmation.
# Note: The password parameter is not echoed.
use File::Path;

print "\n\nExecuting script archive_location.pl.\n\n";

print "Specify the root directory where you would like\n";
print "the archive directory structure created. Please\n";
print "enter an absolute path specification that exists.\n\n";

$rootdir = &prompt ("Root Directory");
if (rindex ($rootdir, "/") ne length ($rootdir) -1) {
   $rootdir = $rootdir . "/";
}
$dbRootdir = 'file://' . $rootdir;

print "Would you also like to update the archive policy\n";
print "in the database? This requires sqlplus to be in\n";
print "your executable path.\n\n";

REPROMPT: {
   $dbupdate = &prompt ("Database Update [y|n] {y}");

   if ($dbupdate eq "") {
      $dbupdate = "y";
   }
   unless ($dbupdate =~ /^y$|^n$/) {
      print "Unrecognized input; try again.\n\n";
      redo REPROMPT;
   }
}

if ($dbupdate eq "y") {
   $sid = &prompt ("Oracle SID");

   $username = &prompt ("Username");

   system 'stty', '-echo';
   $inputPassword = &prompt ("Password");
   system 'stty', 'echo';
   print "\n";
   $password = $inputPassword . "@" . $sid;
}


print "Are these the correct parameter(s)?\n";
print "--------------------------------------------------------\n";
print "Root Directory:   $rootdir\n";
if ($dbupdate eq "y") {
   print "Oracle SID:       $sid\n";
   print "Username:         $username\n";
}
print "--------------------------------------------------------\n\n";

# Prompt for acceptance of input parameters.
REPROMPT: {
   $accept = &prompt ("Accept Parameters [y|n] {y}");

   if ($accept eq "") {
      $accept = "y";
   }
   unless ($accept =~ /^y$|^n$/) {
      print "Unrecognized input; try again.\n\n";
      redo REPROMPT;
   }
}
if ($accept eq 'n') {
   print "Exiting because operator indicated that the input parameters ";
   print "are incorrect.\n\n";
   exit (1);
}

# Set up the date/time variables.
($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) =
    localtime(time);
$year = substr ($year, 1, 2);
$mon = $mon+1;
if (length ($mon) == 1) {$mon = 0 . $mon;}
if (length ($mday) == 1) {$mday = 0 . $mday;}
if (length ($hour) == 1) {$hour = 0 . $hour;}
if (length ($min) == 1) {$min = 0 . $min;}
if (length ($sec) == 1) {$sec = 0 . $sec;}
$dateString = $year . $mon . $mday;
$timeString = $hour . $min . $sec;
chop ($pwd = `pwd`);

# Set up sql file and log file variables.
$logFile = "$pwd/archive_location_$dateString$timeString.log";
$sqlFile = "$pwd/archive_location_$dateString$timeString.sql";

# Create the archive directory structure.
print "Performing the archive directory creation.\n\n";
chdir ($rootdir) or 
   die "Can't access root directory '$rootdir': $!\n";
mkdir ("store") or 
   warn "Can't create directory '$rootdir/store': $!\n";
mkdir ("store/ghrsst") or 
   warn "Can't create directory '$rootdir/store/ghrsst': $!\n";
mkdir ("store/ghrsst/open") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open': $!\n";
mkdir ("store/ghrsst/open/data") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data': $!\n";
mkdir ("store/ghrsst/open/data/L2P") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AMSRE") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AMSRE': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AMSRE/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AMSRE/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AMSRE/REMSS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AMSRE/REMSS': $!\n";
mkdir ("store/ghrsst/open/data/L2P/ATS_NR_2P") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/ATS_NR_2P': $!\n";
mkdir ("store/ghrsst/open/data/L2P/ATS_NR_2P/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/ATS_NR_2P/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/ATS_NR_2P/UPA") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/ATS_NR_2P/UPA': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR_METOP_A") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR_METOP_A': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR_METOP_A/EUR") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR_METOP_A/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR16_G") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR16_G': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR16_G/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR16_G/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR16_L") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR16_L': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR16_L/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR16_L/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_G") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_G': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_G/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_G/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_G/NAVO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_G/NAVO': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_L") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_L': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_L/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_L/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_L/NAVO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_L/NAVO': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR17_L/NEODAAS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR17_L/NEODAAS': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR18_G") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR18_G': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR18_G/NAVO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR18_G/NAVO': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR18_L") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR18_L': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR18_L/NAVO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR18_L/NAVO': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRR18_L/NEODAAS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR18_L/NEODAAS': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRRMTA_G") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRRMTA_G': $!\n";
mkdir ("store/ghrsst/open/data/L2P/AVHRRMTA_G/NAVO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRRMTA_G/NAVO': $!\n";
mkdir ("store/ghrsst/open/data/L2P/GOES11") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/GOES11': $!\n";
mkdir ("store/ghrsst/open/data/L2P/GOES11/OSDPD") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/GOES11/OSDPD': $!\n";
mkdir ("store/ghrsst/open/data/L2P/GOES12") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/GOES12': $!\n";
mkdir ("store/ghrsst/open/data/L2P/GOES12/OSDPD") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/GOES12/OSDPD': $!\n";
mkdir ("store/ghrsst/open/data/L2P/MODIS_A") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/MODIS_A': $!\n";
mkdir ("store/ghrsst/open/data/L2P/MODIS_A/JPL") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/MODIS_A/JPL': $!\n";
mkdir ("store/ghrsst/open/data/L2P/MODIS_T") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/MODIS_T': $!\n";
mkdir ("store/ghrsst/open/data/L2P/MODIS_T/JPL") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/MODIS_T/JPL': $!\n";
mkdir ("store/ghrsst/open/data/L2P/NAR16_SST") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/NAR16_SST': $!\n";
mkdir ("store/ghrsst/open/data/L2P/NAR16_SST/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/NAR16_SST/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/NAR17_SST") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/NAR17_SST': $!\n";
mkdir ("store/ghrsst/open/data/L2P/NAR17_SST/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/NAR17_SST/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/NAR18_SST") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/NAR18_SST': $!\n";
mkdir ("store/ghrsst/open/data/L2P/NAR18_SST/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/NAR18_SST/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/SEVIRI_SST") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/SEVIRI_SST': $!\n";
mkdir ("store/ghrsst/open/data/L2P/SEVIRI_SST/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/SEVIRI_SST/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/TMI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/TMI': $!\n";
mkdir ("store/ghrsst/open/data/L2P/TMI/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/TMI/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L2P/TMI/REMSS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/TMI/REMSS': $!\n";
mkdir ("store/ghrsst/open/data/L2P_GRIDDED") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P_GRIDDED': $!\n";
mkdir ("store/ghrsst/open/data/L2P_GRIDDED/AMSRE") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P_GRIDDED/AMSRE': $!\n";
mkdir ("store/ghrsst/open/data/L2P_GRIDDED/AMSRE/REMSS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P_GRIDDED/AMSRE/REMSS': $!\n";
mkdir ("store/ghrsst/open/data/L2P_GRIDDED/TMI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P_GRIDDED/TMI': $!\n";
mkdir ("store/ghrsst/open/data/L2P_GRIDDED/TMI/REMSS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P_GRIDDED/TMI/REMSS': $!\n";
mkdir ("store/ghrsst/open/data/L4") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4': $!\n";
mkdir ("store/ghrsst/open/data/L4/AUS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/AUS': $!\n";
mkdir ("store/ghrsst/open/data/L4/AUS/ABOM") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/AUS/ABOM': $!\n";
mkdir ("store/ghrsst/open/data/L4/AUS/ABOM/RAMSSA_09km") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/AUS/ABOM/RAMSSA_09km': $!\n";
mkdir ("store/ghrsst/open/data/L4/GAL") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GAL': $!\n";
mkdir ("store/ghrsst/open/data/L4/GAL/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GAL/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L4/GAL/EUR/ODYSSEA") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GAL/EUR/ODYSSEA': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/ABOM") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/ABOM': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/ABOM/GAMSSA_28km") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/ABOM/GAMSSA_28km': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/EUR/ODYSSEA") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/EUR/ODYSSEA': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/NAVO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/NAVO': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/NAVO/K10_SST") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/NAVO/K10_SST': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/NCDC") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/NCDC': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/NCDC/AVHRR_AMSR_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/NCDC/AVHRR_AMSR_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/NCDC/AVHRR_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/NCDC/AVHRR_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/REMSS") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/REMSS': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/REMSS/amsre_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/REMSS/amsre_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/REMSS/mw_ir_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/REMSS/mw_ir_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/REMSS/tmi_amsre_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/REMSS/tmi_amsre_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/REMSS/tmi_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/REMSS/tmi_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/UKMO") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/UKMO': $!\n";
mkdir ("store/ghrsst/open/data/L4/GLOB/UKMO/OSTIA") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/GLOB/UKMO/OSTIA': $!\n";
mkdir ("store/ghrsst/open/data/L4/MED") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/MED': $!\n";
mkdir ("store/ghrsst/open/data/L4/MED/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/MED/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L4/MED/EUR/ODYSSEA") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/MED/EUR/ODYSSEA': $!\n";
mkdir ("store/ghrsst/open/data/L4/NSEABALTIC") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/NSEABALTIC': $!\n";
mkdir ("store/ghrsst/open/data/L4/NSEABALTIC/DMI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/NSEABALTIC/DMI': $!\n";
mkdir ("store/ghrsst/open/data/L4/NSEABALTIC/DMI/DMI_OI") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/NSEABALTIC/DMI/DMI_OI': $!\n";
mkdir ("store/ghrsst/open/data/L4/NWE") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/NWE': $!\n";
mkdir ("store/ghrsst/open/data/L4/NWE/EUR") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/NWE/EUR': $!\n";
mkdir ("store/ghrsst/open/data/L4/NWE/EUR/ODYSSEA") or 
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L4/NWE/EUR/ODYSSEA': $!\n";
mkpath("store/ghrsst/open/data/L3P/GLOB/AVHRR_METOP_A/EUR") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L3P/GLOB/AVHRR_METOP_A/EUR': $!\n";
mkpath("store/ghrsst/open/data/L3P/NAR/AVHRR_METOP_A/EUR") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L3P/NAR/AVHRR_METOP_A/EUR': $!\n";
mkpath("store/ghrsst/open/data/L2P/AVHRR19_L/NAVO") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR19_L/NAVO': $!\n";
mkpath("store/ghrsst/open/data/L2P/AVHRR19_G/NAVO") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR19_G/NAVO': $!\n";
mkpath("store/ghrsst/open/data/L2P/MTSAT_1R/OSDPD") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/MTSAT_1R/OSDPD': $!\n";
mkpath("store/ghrsst/open/data/L2P/MSG_02/OSDPD") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/MSG_02/OSDPD': $!\n";
mkpath("store/ghrsst/open/data/L2P/AVHRR19_L/NEODAAS") or
   warn "Can't create directory '$rootdir/store/ghrsst/open/data/L2P/AVHRR19_L/NEODAAS': $!\n";
mkdir ("store/ascat") or
   warn "Can't create directory '$rootdir/store/ascat': $!\n";
mkdir ("store/ascat/preview") or
   warn "Can't create directory '$rootdir/store/ascat/preview': $!\n";
mkdir ("store/ascat/preview/L2") or
   warn "Can't create directory '$rootdir/store/ascat/preview/L2': $!\n";
mkdir ("store/ascat/preview/L2/data") or
   warn "Can't create directory '$rootdir/store/ascat/preview/L2/data': $!\n";
mkdir ("store/ascat/preview/L2/data/125") or
   warn "Can't create directory '$rootdir/store/ascat/preview/L2/data/125': $!\n";
mkdir ("store/ascat/preview/L2/data/250") or
   warn "Can't create directory '$rootdir/store/ascat/preview/L2/data/250': $!\n";
mkdir ("store/jason1") or 
   warn "Can't create directory '$rootdir/store/jason1': $!\n";
mkdir ("store/jason1/private") or 
   warn "Can't create directory '$rootdir/store/jason1/private': $!\n";
mkdir ("store/jason1/private/aux") or 
   warn "Can't create directory '$rootdir/store/jason1/private/aux': $!\n";
mkdir ("store/jason1/private/aux/data") or 
   warn "Can't create directory '$rootdir/store/jason1/private/aux/data': $!\n";
mkdir ("store/jason1/private/pltm") or 
   warn "Can't create directory '$rootdir/store/jason1/private/pltm': $!\n";
mkdir ("store/jason1/private/pltm/data") or 
   warn "Can't create directory '$rootdir/store/jason1/private/pltm/data': $!\n";
mkdir ("store/jason1/private/ncdf") or 
   warn "Can't create directory '$rootdir/store/jason1/private/ncdf': $!\n";
mkdir ("store/jason1/private/ncdf/data") or 
   warn "Can't create directory '$rootdir/store/jason1/private/ncdf/data': $!\n";
mkdir ("store/jason1/private/trsr") or 
   warn "Can't create directory '$rootdir/store/jason1/private/trsr': $!\n";
mkdir ("store/jason1/private/trsr/data") or 
   warn "Can't create directory '$rootdir/store/jason1/private/trsr/data': $!\n";
mkdir ("store/jason1/open") or 
   warn "Can't create directory '$rootdir/store/jason1/open': $!\n";
mkdir ("store/jason1/open/gdr_c") or 
   warn "Can't create directory '$rootdir/store/jason1/open/gdr_c': $!\n";
mkdir ("store/jason1/open/gdr_c/data") or 
   warn "Can't create directory '$rootdir/store/jason1/open/gdr_c/data': $!\n";
mkdir ("store/jason1/open/igdr") or 
   warn "Can't create directory '$rootdir/store/jason1/open/igdr': $!\n";
mkdir ("store/jason1/open/igdr/data") or 
   warn "Can't create directory '$rootdir/store/jason1/open/igdr/data': $!\n";
mkdir ("store/jason1/open/osdr") or 
   warn "Can't create directory '$rootdir/store/jason1/open/osdr': $!\n";
mkdir ("store/jason1/open/osdr/data") or 
   warn "Can't create directory '$rootdir/store/jason1/open/osdr/data': $!\n";
mkdir ("store/jason1/controlled") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled': $!\n";
mkdir ("store/jason1/controlled/gdr_cnes_c") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/gdr_cnes_c': $!\n";
mkdir ("store/jason1/controlled/gdr_cnes_c/data") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/gdr_cnes_c/data': $!\n";
mkdir ("store/jason1/controlled/gdr_nasa_c") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/gdr_nasa_c': $!\n";
mkdir ("store/jason1/controlled/gdr_nasa_c/data") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/gdr_nasa_c/data': $!\n";
mkdir ("store/jason1/controlled/jmr_a_b") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/jmr_a_b': $!\n";
mkdir ("store/jason1/controlled/jmr_a_b/data") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/jmr_a_b/data': $!\n";
mkdir ("store/jason1/controlled/sgdr_c") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/sgdr_c': $!\n";
mkdir ("store/jason1/controlled/sgdr_c/data") or 
   warn "Can't create directory '$rootdir/store/jason1/controlled/sgdr_c/data': $!\n";
mkdir ("store/oco") or 
   warn "Can't create directory '$rootdir/store/oco': $!\n";
mkdir ("store/oco/private") or 
   warn "Can't create directory '$rootdir/store/oco/private': $!\n";
mkdir ("store/oco/private/acs") or 
   warn "Can't create directory '$rootdir/store/oco/private/acs': $!\n";
mkdir ("store/oco/private/acs/data") or 
   warn "Can't create directory '$rootdir/store/oco/private/acs/data': $!\n";
mkdir ("store/oco/private/anc") or 
   warn "Can't create directory '$rootdir/store/oco/private/anc': $!\n";
mkdir ("store/oco/private/anc/data") or 
   warn "Can't create directory '$rootdir/store/oco/private/anc/data': $!\n";
mkdir ("store/oco/private/fts_anc") or 
   warn "Can't create directory '$rootdir/store/oco/private/fts_anc': $!\n";
mkdir ("store/oco/private/fts_anc/data") or 
   warn "Can't create directory '$rootdir/store/oco/private/fts_anc/data': $!\n";
mkdir ("store/oco/private/fts_igram") or 
   warn "Can't create directory '$rootdir/store/oco/private/fts_igram': $!\n";
mkdir ("store/oco/private/fts_igram/data") or 
   warn "Can't create directory '$rootdir/store/oco/private/fts_igram/data': $!\n";
mkdir ("store/oco/private/moc") or 
   warn "Can't create directory '$rootdir/store/oco/private/moc': $!\n";
mkdir ("store/oco/private/moc/data") or 
   warn "Can't create directory '$rootdir/store/oco/private/moc/data': $!\n";
mkdir ("store/oco/private/telem") or 
   warn "Can't create directory '$rootdir/store/oco/private/telem': $!\n";
mkdir ("store/oco/private/telem/data") or 
   warn "Can't create directory '$rootdir/store/oco/private/telem/data': $!\n";
mkdir ("store/oco/open") or 
   warn "Can't create directory '$rootdir/store/oco/open': $!\n";
mkdir ("store/oco/open/L1B") or 
   warn "Can't create directory '$rootdir/store/oco/open/L1B': $!\n";
mkdir ("store/oco/open/L1B/data") or 
   warn "Can't create directory '$rootdir/store/oco/open/L1B/data': $!\n";
mkdir ("store/oco/open/L2_AOPD") or 
   warn "Can't create directory '$rootdir/store/oco/open/L2_AOPD': $!\n";
mkdir ("store/oco/open/L2_AOPD/data") or 
   warn "Can't create directory '$rootdir/store/oco/open/L2_AOPD/data': $!\n";
mkdir ("store/oco/controlled") or 
   warn "Can't create directory '$rootdir/store/oco/controlled': $!\n";
mkdir ("store/oco/controlled/fts_spectra") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/fts_spectra': $!\n";
mkdir ("store/oco/controlled/fts_spectra/data") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/fts_spectra/data': $!\n";
mkdir ("store/oco/controlled/L1A") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L1A': $!\n";
mkdir ("store/oco/controlled/L1A/data") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L1A/data': $!\n";
mkdir ("store/oco/controlled/L1B") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L1B': $!\n";
mkdir ("store/oco/controlled/L1B/data") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L1B/data': $!\n";
mkdir ("store/oco/controlled/L2_AOPD") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L2_AOPD': $!\n";
mkdir ("store/oco/controlled/L2_AOPD/data") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L2_AOPD/data': $!\n";
mkdir ("store/oco/controlled/L2_FULL") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L2_FULL': $!\n";
mkdir ("store/oco/controlled/L2_FULL/data") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/L2_FULL/data': $!\n";
mkdir ("store/oco/controlled/RICA") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/RICA': $!\n";
mkdir ("store/oco/controlled/RICA/data") or 
   warn "Can't create directory '$rootdir/store/oco/controlled/RICA/data': $!\n";
mkdir ("store/quikscat") or 
   warn "Can't create directory '$rootdir/store/quikscat': $!\n";
mkdir ("store/quikscat/private") or 
   warn "Can't create directory '$rootdir/store/quikscat/private': $!\n";
mkdir ("store/quikscat/private/anc") or 
   warn "Can't create directory '$rootdir/store/quikscat/private/anc': $!\n";
mkdir ("store/quikscat/private/L0") or 
   warn "Can't create directory '$rootdir/store/quikscat/private/L0': $!\n";
mkdir ("store/quikscat/private/L1A") or 
   warn "Can't create directory '$rootdir/store/quikscat/private/L1A': $!\n";
mkdir ("store/quikscat/private/raw") or 
   warn "Can't create directory '$rootdir/store/quikscat/private/raw': $!\n";
mkdir ("store/quikscat/open") or 
   warn "Can't create directory '$rootdir/store/quikscat/open': $!\n";
mkdir ("store/quikscat/open/L2B") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L2B': $!\n";
mkdir ("store/quikscat/open/L2B/data") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L2B/data': $!\n";
mkdir ("store/quikscat/open/L2B/Q2B") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L2B/Q2B': $!\n";
mkdir ("store/quikscat/open/L2B12") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L2B12': $!\n";
mkdir ("store/quikscat/open/L2B12/data") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L2B12/data': $!\n";
mkdir ("store/quikscat/open/L2B12/Q2B12") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L2B12/Q2B12': $!\n";
mkdir ("store/quikscat/open/L3") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L3': $!\n";
mkdir ("store/quikscat/open/L3/data") or 
   warn "Can't create directory '$rootdir/store/quikscat/open/L3/data': $!\n";
mkdir ("store/quikscat/controlled") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled': $!\n";
mkdir ("store/quikscat/controlled/L1B") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L1B': $!\n";
mkdir ("store/quikscat/controlled/L1B/data") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L1B/data': $!\n";
mkdir ("store/quikscat/controlled/L1B/data/L1B") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L1B/data/L1B': $!\n";
mkdir ("store/quikscat/controlled/L1B/data/Q1B") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L1B/data/Q1B': $!\n";
mkdir ("store/quikscat/controlled/L2A") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L2A': $!\n";
mkdir ("store/quikscat/controlled/L2A/data") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L2A/data': $!\n";
mkdir ("store/quikscat/controlled/L2A/data/L2A") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L2A/data/L2A': $!\n";
mkdir ("store/quikscat/controlled/L2A/data/Q2A") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L2A/data/Q2A': $!\n";
mkdir ("store/quikscat/controlled/L2A/data/L2A12") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L2A/data/L2A12': $!\n";
mkdir ("store/quikscat/controlled/L2A/data/Q2A12") or 
   warn "Can't create directory '$rootdir/store/quikscat/controlled/L2A/data/Q2A12': $!\n";

open (LOG, "> " . $logFile) or
   die "Could not open file $logFile: $!\n";

print LOG "The following directory structure was created/exists:\n\n";
print LOG `ls -R $rootdir/store`;
print LOG "\n\n";
print "Successfully created the archive directory structure.\n\n";

# Create the sql file.
open (SQL, "> " . $sqlFile) or
   die "Could not open file $sqlFile: $!\n";

print SQL "/*\n";
print SQL "** Generated by archive_location.pl\n";
print SQL "*/\n\n";
print SQL "SET SERVEROUTPUT ON;\n";
print SQL "SET ECHO ON;\n\n";
print SQL "UPDATE dataset_location_policy\n";
print SQL "SET base_path =  CONCAT('$dbRootdir', SUBSTR(base_path, INSTR(base_path, 'store', 1, 1), LENGTH(base_path) - INSTR(base_path, 'store', 1, 1) + 1))\n";
print SQL "WHERE type LIKE 'ARCHIVE%';\n\n";
print SQL "COMMIT;\n\n";
print SQL "SET ECHO OFF;\n";
print SQL "QUIT;\n\n";

close (SQL);

# Execute the sql file, if requested.
if ($dbupdate eq 'y') {
   print "Performing the database update.\n";
   print LOG "Executing the following SQL command:\n\n";
   close (LOG);

   system ("sqlplus -l $username/$password \@$sqlFile >> $logFile");
}
else {
   print "Please execute the following SQL file in your favorite\n";
   print "Oracle interface (e.g., sqlplus):\n";
   print "  $sqlFile\n";
}

# Open the log file and check for errors/messages.
open (LOG, "< " . $logFile) or
   die "Could not open file $logFile: $!\n";

$messageCount = 0;

   while ($record = <LOG>) {
      if (($record =~ /ERROR.*/) or ($record =~ /Warning.*/)) {
         $messageCount++;
      }
   }

if ($messageCount > 0) {
   print "\nThe operation appears to have generated warning or error messages.\n";
}
else {
   print "\nThe operation appears to have completed successfully.\n";
}

close (LOG);

# Print messages and exit.
print "\nFor more information view the log file:\n";
print "  $logFile\n";
print "\nThe archive_location.pl script has completed.\n\n";

exit (0);


# subroutine:  prompt - Prompt the user for input. Return the response.

sub prompt {
   PROMPT: {
      print "$_[0]: ";
      chop ($response = <STDIN>);
      print "\n";
   }
   return $response;
}
