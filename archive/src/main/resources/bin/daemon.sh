#!/bin/sh

PIDFILE="$1"
shift
LOG_CONFIG="$1"
shift
JAVA_APP="$1"
shift
if [ "$1" = stop ]
then
  if test -f "${PIDFILE}"
  then
    echo "Killing: `cat $PIDFILE`"
    kill `cat $PIDFILE`
  else
    echo "Kill failed: ${PIDFILE} not set"
  fi
  exit
fi

if test -f "${PIDFILE}"
then
   daemon_pid=`cat "${PIDFILE}"`
   if ps -p "${daemon_pid}" >/dev/null 2>&1
   then
      # daemon is already running.
      echo "${JAVA_APP}[pid=${daemon_pid}] is already launched."
      exit
   fi
fi

# Make the classpath:
CLASSPATH=`echo ${archive.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH
java -Darchive.config.file=${archive.home}/config/archive.config \
	-Ddaemon.pidfile="${PIDFILE}" \
	-Dlog4j.configuration="${LOG_CONFIG}" \
	"${JAVA_APP}"  $* <&- &
daemon_pid=$!
if ps -p "${daemon_pid}" | grep "${daemon_pid}" >/dev/null 2>&1
then
   # daemon is running.
   echo ${daemon_pid} > "${PIDFILE}"
   echo "${JAVA_APP} [pid=${daemon_pid}] started."
else
   echo "${JAVA_APP} did not start."
fi
