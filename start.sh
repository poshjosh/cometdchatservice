#!/bin/sh
if [ "$DEBUG" = true ] ; then
    echo 'ls -a' && ls -a
    echo 'cd app' && cd app 
    echo 'ls -a' && ls -a 
    echo 'cd lib' && cd lib 
    echo 'ls -a' && ls -a
    echo 'cd .. && cd ..'
    cd .. && cd ..
fi
echo java ${JAVA_OPTS} -cp app:app/lib/* ${MAIN_CLASS} ${@}
exec java ${JAVA_OPTS} -cp app:app/lib/* ${MAIN_CLASS} ${@}
