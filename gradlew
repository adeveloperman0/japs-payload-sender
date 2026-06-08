#!/bin/sh

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
APP_HOME=`dirname "$0"`
APP_HOME=`cd "$APP_HOME" && pwd`

warn () { echo "$*"; }
die () { echo; echo "$*"; echo; exit 1; }

cygwin=false; msys=false; darwin=false; nonstop=false
case "`uname`" in
  CYGWIN* ) cygwin=true ;;
  Darwin*  ) darwin=true ;;
  MSYS*|MINGW* ) msys=true ;;
  NONSTOP* ) nonstop=true ;;
esac

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
    [ ! -x "$JAVACMD" ] && die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
else
    JAVACMD="java"
    command -v java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
fi

exec "$JAVACMD" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
